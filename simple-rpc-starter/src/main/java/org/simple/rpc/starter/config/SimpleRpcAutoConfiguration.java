package org.simple.rpc.starter.config;

import org.simple.rpc.starter.config.properties.SimpleRpcProperties;
import org.simple.rpc.starter.config.properties.SpringParamsProperties;
import org.simple.rpc.starter.helper.ApplicationContextHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 简单RPC自动配置
 * EnableConfigurationProperties 注解，关于xxxProperties，如果再下面使用@Bean注入了，
 * 那么就可以不在@EnableConfigurationProperties再次引入，否则容器中会有两个该bean
 *
 * @author Mr_wenpan@163.com 2022/01/19 12:35
 */
@Configuration
@EnableConfigurationProperties({SimpleRpcProperties.class, SpringParamsProperties.class})
public class SimpleRpcAutoConfiguration {

    /**
     * 注入容器帮助器
     */
    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

}
