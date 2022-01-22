package org.simple.rpc.starter.message;

import lombok.Getter;
import lombok.ToString;

/**
 * rpc请求消息
 *
 * @author Mr_wenpan@163.com 2022/1/19 12:34 下午
 */
@Getter
@ToString(callSuper = true)
public class RpcRequestMessage extends Message {

    /**
     * 调用的接口全限定名，服务端根据它找到实现
     */
    private final String interfaceName;
    /**
     * 调用接口中的方法名
     */
    private final String methodName;
    /**
     * 方法返回类型
     */
    private final Class<?> returnType;
    /**
     * 方法参数类型数组
     */
    private final Class<?>[] parameterTypes;
    /**
     * 方法参数值数组
     */
    private final Object[] parameterValue;

    public RpcRequestMessage(int sequenceId,
                             String interfaceName,
                             String methodName,
                             Class<?> returnType,
                             Class<?>[] parameterTypes,
                             Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
