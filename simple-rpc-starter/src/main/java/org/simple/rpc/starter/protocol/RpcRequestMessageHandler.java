package org.simple.rpc.starter.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.simple.rpc.starter.message.RpcRequestMessage;
import org.simple.rpc.starter.message.RpcResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * rpc请求处理器(入站handler)
 *
 * @author Mr_wenpan@163.com 2021/09/28 16:11
 */
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestMessageHandler.class);

    private ApplicationContext applicationContext;

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
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        System.out.println("provider收到consumer的请求...." + message);
        // 保证响应消息的序列号和请求消息的序列号一致
        responseMessage.setSequenceId(message.getSequenceId());

        try {
            // 通过请求消息中的接口名称获取到容器中该接口的对应实现类
            String interfaceName = message.getInterfaceName();
            String methodName = message.getMethodName();
            logger.info("interfaceName = {} , methodName = {}", interfaceName, methodName);
            Object targetBean = applicationContext.getBean(Class.forName(interfaceName));

            // 通过请求消息中的调用方法名，在接口中获取到要调用的方法对象
            Method method = targetBean.getClass().getMethod(methodName, message.getParameterTypes());
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

}