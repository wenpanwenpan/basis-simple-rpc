package org.simple.rpc.starter.exception;

/**
 * 序列化异常
 *
 * @author Mr_wenpan@163.com 2022/01/21 12:40
 */
public class SimpleRpcSerializeException extends RuntimeException {

    public SimpleRpcSerializeException(String code) {
        super(code);
    }

    public SimpleRpcSerializeException(String code, Throwable cause) {
        super(code, cause);
    }

    public SimpleRpcSerializeException(Throwable cause) {
        super(cause);
    }
}
