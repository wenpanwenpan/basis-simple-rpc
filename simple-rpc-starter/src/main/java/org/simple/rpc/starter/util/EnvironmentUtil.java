package org.simple.rpc.starter.util;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 环境工具
 *
 * @author Mr_wenpan@163.com 2021/09/06 17:15
 */
public class EnvironmentUtil {

    /**
     * 从环境信息Environment中解析出对应的属性值
     *
     * @param environment 环境
     * @param prefix      属性前缀
     * @param interval    间隔
     * @return java.util.Set<java.lang.String> 属性值
     */
    public static Set<String> parseFieldValueFromEnvironment(AbstractEnvironment environment, String prefix, int interval) {
        MutablePropertySources propertySources = environment.getPropertySources();
        Set<String> configs = StreamSupport.stream(propertySources.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .filter(propName -> propName.startsWith(prefix))
                .collect(Collectors.toSet());

        if (configs.size() > 0) {
            return configs.stream().map(item -> item.split("\\.")[interval]).collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }
}