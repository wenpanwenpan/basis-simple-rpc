package org.simple.rpc.starter.util;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 网络工具类
 *
 * @author Mr_wenpan@163.com 2022/01/20 18:20
 */
public class NetworkUtil {

    private static String LOCAL_IP;

    /**
     * 获取当前机器ip
     */
    public static String getCurrentHostIp() throws UnknownHostException {
        if (StringUtils.isNotBlank(LOCAL_IP)) {
            return LOCAL_IP;
        }
        return LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * 获取本地IP
     *
     * @return java.lang.String 本机IP
     * @author Mr_wenpan@163.com 2022/1/21 10:42 上午
     */
    public static String localIp() {
        try {
            if (!StringUtils.isEmpty(LOCAL_IP)) {
                return LOCAL_IP;
            }
            String ip = System.getProperty("com.simple.rpc.server.naming.local.ip", InetAddress.getLocalHost().getHostAddress());
            return LOCAL_IP = ip;
        } catch (UnknownHostException e) {
            return "resolve_failed";
        }
    }
}
