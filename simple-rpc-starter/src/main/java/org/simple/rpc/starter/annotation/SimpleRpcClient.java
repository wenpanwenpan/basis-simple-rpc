package org.simple.rpc.starter.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * rpc客户端注解（使用时请保证value和name至少有一个不为空，优先使用name）
 *
 * @author Mr_wenpan@163.com 2022/01/19 14:08
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleRpcClient {

    /**
     * 服务提供者名称
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 服务提供者名称
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Whether to mark the feign proxy as a primary bean. Defaults to true.
     */
    boolean primary() default true;

    /**
     * Sets the <code>@Qualifier</code> value for the feign client.
     */
    String qualifier() default "";

}
