package org.simple.rpc.starter.exception;

/**
 * 服务提供者名称为空异常
 *
 * @author Mr_wenpan@163.com 2022/01/21 13:57
 */
public class ProviderNameNullException extends RuntimeException {

    public ProviderNameNullException(String code) {
        super(code);
    }

    public ProviderNameNullException(String code, Throwable cause) {
        super(code, cause);
    }

    public ProviderNameNullException(Throwable cause) {
        super(cause);
    }

}
