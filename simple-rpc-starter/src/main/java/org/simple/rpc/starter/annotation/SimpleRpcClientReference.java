package org.simple.rpc.starter.annotation;

import java.lang.annotation.*;

/**
 * simple rpc客户端引用
 *
 * @author Mr_wenpan@163.com 2022/01/24 12:49
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SimpleRpcClientReference {

}
