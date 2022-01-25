package org.simple.rpc.starter.exception;

/**
 * simple rpc 注册异常
 *
 * @author Mr_wenpan@163.com 2022/01/24 15:55
 */
public class SimpleRpcRegisterException extends RuntimeException {

    public SimpleRpcRegisterException(String code) {
        super(code);
    }

    public SimpleRpcRegisterException(String code, Throwable cause) {
        super(code, cause);
    }

    public SimpleRpcRegisterException(Throwable cause) {
        super(cause);
    }
}
