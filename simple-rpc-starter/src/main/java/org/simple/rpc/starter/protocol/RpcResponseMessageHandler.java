package org.simple.rpc.starter.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.simple.rpc.starter.message.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc消息响应处理器(入站handler)
 *
 * @author Mr_wenpan@163.com 2021/09/28 16:16
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    /**
     * key ： 消息序号     value : 用来接收结果的 promise 对象
     */
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    /**
     * rpc响应消息处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {

        log.info("response msg = {}", msg);
        // 通过消息序号拿到对应的promise
        Promise<Object> promise = PROMISES.get(msg.getSequenceId());
        if (null != promise) {
            // 获取到server端返回的接口执行返回值
            Object returnValue = msg.getReturnValue();
            // 获取到server执行的异常信息
            Exception exceptionValue = msg.getExceptionValue();

            // 这里我们约定如果异常信息为空，则说明接口正常执行
            if (null == exceptionValue) {
                promise.setSuccess(returnValue);
            } else {
                promise.setFailure(exceptionValue);
            }
        }
    }

}