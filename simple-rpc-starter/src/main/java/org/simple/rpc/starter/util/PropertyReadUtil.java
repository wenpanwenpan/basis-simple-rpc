package org.simple.rpc.starter.util;

import org.simple.rpc.starter.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * property属性读取工具
 *
 * @author Mr_wenpan@163.com 2021/09/24 23:41
 */
public class PropertyReadUtil {

    private static Properties properties;

    static {
        // 获取该配置文件的输入流
        try (InputStream in = PropertyReadUtil.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            // 将配置信息加载到properties中
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 根据属性名称读取配置文件中的属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    public static String getValue(String name) {
        return properties.getProperty(name);
    }

    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if (value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 获取序列化算法
     */
    public static Serializer.Algorithm getSerializerAlgorithm() {
        // 从配置文件中读取
        String value = properties.getProperty("serializer.algorithm");
        if (value == null) {
            return Serializer.Algorithm.Java;
        } else {
            // 使用枚举的valueOf方法将string转换为对应的序列化算法
            return Serializer.Algorithm.valueOf(value);
        }
    }

}