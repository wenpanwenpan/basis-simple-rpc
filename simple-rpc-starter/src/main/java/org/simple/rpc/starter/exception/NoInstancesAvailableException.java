package org.simple.rpc.starter.exception;

/**
 * 没有可用实例异常
 *
 * @author Mr_wenpan@163.com 2022/01/21 14:51
 */
public class NoInstancesAvailableException extends RuntimeException {

    public NoInstancesAvailableException(String code) {
        super(code);
    }

    public NoInstancesAvailableException(String code, Throwable cause) {
        super(code, cause);
    }

    public NoInstancesAvailableException(Throwable cause) {
        super(cause);
    }
}
