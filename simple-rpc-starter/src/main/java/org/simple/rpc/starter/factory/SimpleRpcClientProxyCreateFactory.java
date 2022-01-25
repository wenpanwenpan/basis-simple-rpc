package org.simple.rpc.starter.factory;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.simple.rpc.starter.client.SequenceIdGenerator;
import org.simple.rpc.starter.config.properties.SimpleRpcProperties;
import org.simple.rpc.starter.constant.SimpleRpcConstants;
import org.simple.rpc.starter.discovery.NacosRegistrarManager;
import org.simple.rpc.starter.exception.NoInstancesAvailableException;
import org.simple.rpc.starter.exception.SimpleRpcCallFailedException;
import org.simple.rpc.starter.exception.SimpleRpcChannelException;
import org.simple.rpc.starter.helper.ApplicationContextHelper;
import org.simple.rpc.starter.message.RpcRequestMessage;
import org.simple.rpc.starter.protocol.MessageCodecSharable;
import org.simple.rpc.starter.protocol.ProtocolFrameDecoder;
import org.simple.rpc.starter.protocol.RpcResponseMessageHandler;
import org.simple.rpc.starter.registrar.SimpleRpcServerChannelRegistrar;
import org.springframework.lang.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * simple rpc 客户端动态代理创建工厂
 *
 * @author Mr_wenpan@163.com 2022/01/24 13:08
 */
@Slf4j
public class SimpleRpcClientProxyCreateFactory {

    /**
     * 通过接口的class创建该接口的代理对象(这里直接基于JDK提供的创建动态代理的工具来创建代理对象)
     *
     * @param serviceClass 接口的class
     * @return T 代理对象
     */
    public static <T> T createProxyService(Class<T> serviceClass) {
        // 该接口的Class对象是被那个类加载器加载的
        ClassLoader classLoader = serviceClass.getClassLoader();
        // 获取到该接口所有的interface(这里先假设就只有一个接口)
        Class<?>[] interfaces = {serviceClass};

        // jdk代理必须的handler，代理对象的方法执行就会调用这里的invoke方法。自动传入调用的方法 + 方法参数
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 1、将方法调用转换为rpc请求消息(sequenceId为消息唯一编号，当请求响应时可以通过这个ID找到对应的等待的Promise，然后唤醒)
                int sequenceId = SequenceIdGenerator.nextId();
                // 封装RPC请求消息
                RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(sequenceId,
                        serviceClass.getName(),
                        method.getName(),
                        method.getReturnType(),
                        method.getParameterTypes(),
                        args);

                // 2、将消息对象发送出去(这里channel不会阻塞等待消息返回)
                Channel channel = getChannelByProviderInfName(serviceClass.getName());
                channel.writeAndFlush(rpcRequestMessage);
                System.out.println("channel.writeAndFlush(rpcRequestMessage);");

                // 3、准备一个空的promise对象来接收server返回的结果
                // 指定promise对象异步接收结果的线程，这里使用发送消息的channel的线程来接收消息（getChannel().eventLoop()）
                DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());

                // 将这个promise缓存起来，以便于server响应结果回来的时候能够通过消息的sequenceId正确的找到这个promise
                // 那么这个promise在哪里接收的结果呢？当然是在响应handler里，因为只有当server端响应了promise才应该被设置值
                RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);

                // todo 这里可以提供多种获取结果的方式（比如方法回调，同步等待），提升channel性能
                /*promise.addListener(future -> {
                    // 线程
                });*/

                // 调用接口的线程等待，直到promise有结果（正常或异常）
                SimpleRpcProperties simpleRpcProperties = ApplicationContextHelper.getContext().getBean(SimpleRpcProperties.class);
                promise.await(simpleRpcProperties.getMaxWaitTime() * 1000);

                System.out.println("promise结果已返回, promise = " + promise.toString());
                // server返回结果后结束上面的阻塞，执行这里
                if (promise.isSuccess()) {
                    return promise.getNow();
                } else {
                    throw new SimpleRpcCallFailedException(String.format(
                            "simple rpc call failed, interface providerName is : [%s]", serviceClass.getName()), promise.cause());
                }
            }
        };

        Object proxy = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);

        // 返回代理对象
        return (T) proxy;
    }

    /**
     * 同步锁对象
     */
    private static final Object LOCK = new Object();

    /**
     * 获取唯一的 channel 对象(需要通过这个channel将数据发送给server端)
     */
    public static Channel getChannelByProviderInfName(@NonNull String interfaceName) throws NacosException {
        // 从nacos上随机获取一个可用的服务提供者实例
        NacosRegistrarManager nacosRegistrarManager = ApplicationContextHelper.getContext().getBean(NacosRegistrarManager.class);
        Instance instance = nacosRegistrarManager.getRandomInstanceByServerName(interfaceName);
        if (Objects.isNull(instance)) {
            throw new NoInstancesAvailableException(String.format("can not found available instance by service name [%s]", interfaceName));
        }
        // [服务名 + ip + 端口] 确定provider的唯一性
        String ip = instance.getIp();
        int port = instance.getPort();
        String serviceName = instance.getServiceName();
        String uniqueKey = serviceName + ip + SimpleRpcConstants.Symbol.COLON + port;
        Channel channel = SimpleRpcServerChannelRegistrar.getChannel(uniqueKey);

        if (Objects.nonNull(channel)) {
            return channel;
        }

        return initProviderChannel(serviceName, ip, port);
    }

    /**
     * 初始化到provider 的 channel
     *
     * @param serviceName serviceName
     * @param ip          provider的ip
     * @param port        provider监听的端口
     * @return io.netty.channel.Channel
     * @author Mr_wenpan@163.com 2022/1/21 11:51 上午
     */
    private static Channel initProviderChannel(String serviceName, String ip, int port) {
        // 唯一key
        String uniqueKey = serviceName + ip + SimpleRpcConstants.Symbol.COLON + port;
        Channel channel = SimpleRpcServerChannelRegistrar.getChannel(uniqueKey);
        // 双重检查
        if (Objects.nonNull(channel)) {
            return channel;
        }
        synchronized (LOCK) {
            // 通过uniqueKey获取到某个provider的channel
            channel = SimpleRpcServerChannelRegistrar.getChannel(uniqueKey);
            if (Objects.nonNull(channel)) {
                return channel;
            }
            // 创建到该provider的一个新channel并缓存起来
            // 客户端事件线程池组
            NioEventLoopGroup group = new NioEventLoopGroup();
            // 日志处理handler
            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
            // 自定义编码解码器
            MessageCodecSharable messageCodec = new MessageCodecSharable();
            // rpc调用响应消息处理handler
            RpcResponseMessageHandler rpcHandler = new RpcResponseMessageHandler();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);

            // 绑定handler
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                // 建立连接后为该client channel添加handler
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 自定义帧解码器
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    // 日志处理器
                    // ch.pipeline().addLast(loggingHandler);
                    // 自定义编码解码器（按自定义格式将消息解码，然后传递给下一个handler）
                    ch.pipeline().addLast(messageCodec);
                    // rpc 调用响应消息处理handler
                    ch.pipeline().addLast(rpcHandler);
                }
            });

            try {
                // 通过provider的ip + 端口，发起连接并注册到本地缓存
                channel = bootstrap.connect(ip, port).sync().channel();
                SimpleRpcServerChannelRegistrar.registerChannel(uniqueKey, channel);
                // 注册channel关闭监听事件
                channel.closeFuture().addListener(future -> {
                    // help gc and reconnect
                    SimpleRpcServerChannelRegistrar.removeChannel(uniqueKey);
                    group.shutdownGracefully();
                    log.warn("server channel shutdown, shutdown [NioEventLoopGroup] gracefully now.");
                });
            } catch (Exception e) {
                throw new SimpleRpcChannelException("create simple rpc provider channel occur excetion.", e);
            }
            return channel;
        }
    }
}
