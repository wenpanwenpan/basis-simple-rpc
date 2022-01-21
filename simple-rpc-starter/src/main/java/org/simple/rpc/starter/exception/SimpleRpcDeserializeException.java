package org.simple.rpc.starter.exception;

/**
 * 反序列化异常
 *
 * @author Mr_wenpan@163.com 2022/01/21 12:41
 */
public class SimpleRpcDeserializeException extends RuntimeException {

    public SimpleRpcDeserializeException(String code) {
        super(code);
    }

    public SimpleRpcDeserializeException(String code, Throwable cause) {
        super(code, cause);
    }

    public SimpleRpcDeserializeException(Throwable cause) {
        super(cause);
    }
}
