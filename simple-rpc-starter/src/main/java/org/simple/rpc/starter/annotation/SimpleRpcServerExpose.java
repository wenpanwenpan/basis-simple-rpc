package org.simple.rpc.starter.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 服务提供端暴露接口注解(组合注解，组合了@Component)
 *
 * @author Mr_wenpan@163.com 2022/01/24 11:57
 */
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleRpcServerExpose {

    /**
     * value
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
