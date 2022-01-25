package org.simple.rpc.starter.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.simple.rpc.starter.constant.SimpleRpcConstants;
import org.simple.rpc.starter.message.RpcRequestMessage;
import org.simple.rpc.starter.message.RpcResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * rpc请求处理器(入站handler)
 *
 * @author Mr_wenpan@163.com 2021/09/28 16:11
 */
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestMessageHandler.class);

    /**
     * 反射接口本地缓存
     */
    private static final Map<String, Object> INTERFACE_OBJECT_MAP = new ConcurrentHashMap<>();

    /**
     * 反射方法本地缓存
     */
    private static final Map<String, Method> METHOD_OBJECT_MAP = new ConcurrentHashMap<>();

    /**
     * 接口锁对象
     */
    private static final Object INTERFACE_LOCK = new Object();

    /**
     * 方法锁对象
     */
    private static final Object METHOD_LOCK = new Object();

    private ApplicationContext applicationContext;

    /**
     * rpcRequestMessage消息处理线程池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    public RpcRequestMessageHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 处理rpc请求消息
     *
     * @param ctx     channel处理器上下文
     * @param message rpc请求消息
     * @author Mr_wenpan@163.com 2021/9/28 5:08 下午
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        // SequenceId保证响应消息的序列号和请求消息的序列号一致
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        responseMessage.setSequenceId(message.getSequenceId());

        // todo 利用线程池来提升消息rpc请求消息接收、处理性能
        try {
            // 通过请求消息中的接口名称获取到容器中该接口的对应实现类
            String interfaceName = message.getInterfaceName();
            String methodName = message.getMethodName();

            // 获取到要调用的接口实现类对象 + 具体的方法（使用本地缓存提高反射效率）
            Object targetBean = getTargetBean(interfaceName);
            Method method = getTargetMethod(targetBean, interfaceName, methodName, message.getParameterTypes());

            // 反射调用接口实现类的指定方法
            Object invoke = method.invoke(targetBean, message.getParameterValue());

            // 调用成功
            responseMessage.setReturnValue(invoke);
        } catch (Exception ex) {
            // 方法调用失败，返回异常调用
            ex.printStackTrace();
            responseMessage.setExceptionValue(ex);
        }

        // 执行结果写回给客户端
        ctx.writeAndFlush(responseMessage);
    }

    /**
     * 根据接口名获取目标bean（先从本地缓存获取，获取不到再通过反射去容器中获取，提升反射效率）
     *
     * @param interfaceName 接口名称
     * @return java.lang.Object 接口目标bean
     * @author Mr_wenpan@163.com 2022/1/24 4:34 下午
     */
    private Object getTargetBean(String interfaceName) throws ClassNotFoundException {

        Object targetBean = INTERFACE_OBJECT_MAP.get(interfaceName);

        if (!Objects.isNull(targetBean)) {
            return targetBean;
        }

        // double check.
        synchronized (INTERFACE_LOCK) {
            targetBean = INTERFACE_OBJECT_MAP.get(interfaceName);
            if (Objects.isNull(targetBean)) {
                targetBean = applicationContext.getBean(Class.forName(interfaceName));
                INTERFACE_OBJECT_MAP.put(interfaceName, targetBean);
                return targetBean;
            }
        }

        return targetBean;
    }

    /**
     * 根据interfaceName + methodName + 方法参数类型获取目标Method对象，提高反射效率
     *
     * @param targetBean     接口bean对象
     * @param interfaceName  接口名称
     * @param methodName     方法名称
     * @param parameterTypes 方法参数类型
     * @return java.lang.reflect.Method 接口的Method对象
     * @author Mr_wenpan@163.com 2022/1/24 4:45 下午
     */
    private static Method getTargetMethod(Object targetBean,
                                          String interfaceName,
                                          String methodName,
                                          Class<?>... parameterTypes) throws NoSuchMethodException {
        String key = buildMethodKey(interfaceName, methodName, parameterTypes);
        Method method = METHOD_OBJECT_MAP.get(key);

        // double check.
        if (method != null) {
            return method;
        }

        synchronized (METHOD_LOCK) {
            method = METHOD_OBJECT_MAP.get(key);
            if (method != null) {
                return method;
            }
            method = targetBean.getClass().getMethod(methodName, parameterTypes);
            METHOD_OBJECT_MAP.put(key, method);
            return method;
        }
    }

    /**
     * 构建method唯一key
     */
    private static String buildMethodKey(String interfaceName, String methodName, Class<?>[] parameterTypes) {
        StringJoiner joiner = new StringJoiner(SimpleRpcConstants.Symbol.COMMA);
        for (Class<?> parameterType : parameterTypes) {
            joiner.add(parameterType.getTypeName());
        }
        return interfaceName +
                SimpleRpcConstants.Symbol.WELL +
                methodName +
                SimpleRpcConstants.Symbol.LEFT_BRACKET +
                joiner.toString() +
                SimpleRpcConstants.Symbol.RIGHT_BRACKET;
    }

}