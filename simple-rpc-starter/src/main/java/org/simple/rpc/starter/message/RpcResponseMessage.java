package org.simple.rpc.starter.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * rpc调用响应消息
 *
 * @author Mr_wenpan@163.com 2022/1/19 11:31 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}