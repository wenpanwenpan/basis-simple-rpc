package org.simple.rpc.starter.exception;

/**
 * simple rpc channel 异常
 *
 * @author Mr_wenpan@163.com 2022/1/21 10:59 上午
 */
public class SimpleRpcChannelException extends RuntimeException {

    public SimpleRpcChannelException(String code) {
        super(code);
    }

    public SimpleRpcChannelException(String code, Throwable cause) {
        super(code, cause);
    }

    public SimpleRpcChannelException(Throwable cause) {
        super(cause);
    }

}