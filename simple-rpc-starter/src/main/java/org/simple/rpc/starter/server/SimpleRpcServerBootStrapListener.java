package org.simple.rpc.starter.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.simple.rpc.starter.config.properties.SimpleRpcProperties;
import org.simple.rpc.starter.protocol.MessageCodecSharable;
import org.simple.rpc.starter.protocol.ProtocolFrameDecoder;
import org.simple.rpc.starter.protocol.RpcRequestMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * simple rpc server端启动监听
 *
 * @author Mr_wenpan@163.com 2022/01/19 16:41
 */
public class SimpleRpcServerBootStrapListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleRpcServerBootStrapListener.class);

    private SimpleRpcProperties simpleRpcProperties;

    public SimpleRpcServerBootStrapListener(SimpleRpcProperties simpleRpcProperties) {
        this.simpleRpcProperties = simpleRpcProperties;
    }

    /**
     * 关注容器启动完成后刷新事件，容器刷新后启动netty server
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        startSimpleRpcServer(event);
    }

    /**
     * 启动simple rpc 服务端
     */
    private void startSimpleRpcServer(ContextRefreshedEvent event) {
        // 获取监听的端口
        Integer serverPort = simpleRpcProperties.getSimpleRpcServerPort();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
            // 自定义协议解码器
            MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
            // rpc消息请求处理器
            RpcRequestMessageHandler rpcRequestMessageHandler = new RpcRequestMessageHandler(event.getApplicationContext());

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                // 在client连接建立时为这些client流水线添加处理器
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    // 添加自定义的帧解码器，解决黏包半包问题
                    pipeline.addLast(new ProtocolFrameDecoder());
                    // 日志handler
//                    pipeline.addLast(loggingHandler);
                    // 用自定义协议解码消息
                    pipeline.addLast(messageCodecSharable);
                    // rpc请求处理器
                    pipeline.addLast(rpcRequestMessageHandler);
                }

            });

            // 绑定端口并同步启动
            Channel channel = serverBootstrap.bind(serverPort).sync().channel();
            logger.info("simple rpc server setup, and listen port is : {}", serverPort);
            // channel关闭时监听，优雅的关闭NioEventLoopGroup
            channel.closeFuture().addListener(future -> {
                logger.warn("simple rpc server channel closed, shutdown boss and worker gracefully.");
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            });
            logger.info("simple rpc server channel.closeFuture().sync() execute.");
        } catch (Exception ex) {
            // 抛出异常中断容器启动
            throw new RuntimeException("setup simple rpc server occur exception.", ex);
        }

    }
}
