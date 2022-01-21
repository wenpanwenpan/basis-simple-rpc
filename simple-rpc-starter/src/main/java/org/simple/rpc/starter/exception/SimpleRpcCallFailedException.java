package org.simple.rpc.starter.exception;

/**
 * simple rpc 调用失败异常
 *
 * @author Mr_wenpan@163.com 2022/01/21 15:43
 */
public class SimpleRpcCallFailedException extends RuntimeException {

    public SimpleRpcCallFailedException(String code) {
        super(code);
    }

    public SimpleRpcCallFailedException(String code, Throwable cause) {
        super(code, cause);
    }

    public SimpleRpcCallFailedException(Throwable cause) {
        super(cause);
    }

}
