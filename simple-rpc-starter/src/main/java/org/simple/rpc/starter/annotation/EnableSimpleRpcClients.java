package org.simple.rpc.starter.annotation;

import org.simple.rpc.starter.nacos.NacosRegisterRunner;
import org.simple.rpc.starter.nacos.NacosRegistrarManager;
import org.simple.rpc.starter.registrar.SimpleRpcClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启simple rpc客户端
 *
 * @author Mr_wenpan@163.com 2022/01/19 14:17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({SimpleRpcClientsRegistrar.class, NacosRegisterRunner.class, NacosRegistrarManager.class})
public @interface EnableSimpleRpcClients {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @ComponentScan("org.my.pkg")} instead of {@code @ComponentScan(basePackages="org.my.pkg")}.
     *
     * @return the array of 'basePackages'.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components.
     * <p>
     * {@link #value()} is an alias for (and mutually exclusive with) this attribute.
     * <p>
     *
     * @return the array of 'basePackages'.
     */
    String[] basePackages() default {};

}
