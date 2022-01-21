package org.simple.rpc.starter.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义消息
 *
 * @author Mr_wenpan@163.com 2022/1/19 11:27 上午
 */
@Data
public abstract class Message implements Serializable {

    /**
     * 根据消息类型字节，获得对应的消息 class
     *
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    /**
     * 获取消息类型
     *
     * @return int 消息类型
     * @author Mr_wenpan@163.com 2022/1/19 11:29 上午
     */
    public abstract int getMessageType();

    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;
    public static final int RPC_MESSAGE_TYPE_RESPONSE = 102;

    private static final Map<Integer, Class<? extends Message>> MESSAGE_CLASSES = new HashMap<Integer, Class<? extends Message>>();

    static {
        MESSAGE_CLASSES.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        MESSAGE_CLASSES.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }
}