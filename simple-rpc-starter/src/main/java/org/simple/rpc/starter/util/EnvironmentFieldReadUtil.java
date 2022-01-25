package org.simple.rpc.starter.util;

import org.simple.rpc.starter.config.properties.SimpleRpcProperties;
import org.simple.rpc.starter.helper.ApplicationContextHelper;
import org.simple.rpc.starter.serialize.Serializer;

/**
 * 环境变量读取工具类
 *
 * @author Mr_wenpan@163.com 2022/01/24 22:07
 */
public class EnvironmentFieldReadUtil {

    private static SimpleRpcProperties simpleRpcProperties;

    private static final String SIMPLE_RPC_PROPERTIES = "simpleRpcProperties";

    static {
        ApplicationContextHelper.asyncStaticSetter(SimpleRpcProperties.class, EnvironmentFieldReadUtil.class, SIMPLE_RPC_PROPERTIES);
    }

    /**
     * 不可删除
     */
    public static void setSimpleRpcProperties(SimpleRpcProperties simpleRpcProperties) {
        EnvironmentFieldReadUtil.simpleRpcProperties = simpleRpcProperties;
    }

    /**
     * 获取配置文件中指定的序列化算法
     */
    public static Serializer.Algorithm getSerializerAlgorithm() {

        return simpleRpcProperties.getSerializerAlgorithm();
    }

}
