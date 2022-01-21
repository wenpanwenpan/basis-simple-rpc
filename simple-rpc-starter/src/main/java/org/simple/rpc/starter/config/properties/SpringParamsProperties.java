package org.simple.rpc.starter.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring相关配置参数映射properties
 *
 * @author Mr_wenpan@163.com 2022/01/20 22:11
 */
@Data
@ConfigurationProperties(prefix = SpringParamsProperties.PREFIX)
public class SpringParamsProperties {

    public static final String PREFIX = "spring.application";

    private String name;
}
