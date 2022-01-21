package org.simple.rpc.starter.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * simple rpc properties
 *
 * @author Mr_wenpan@163.com 2022/01/19 16:57
 */
@Data
@ConfigurationProperties(prefix = SimpleRpcProperties.PREFIX)
public class SimpleRpcProperties {

    public static final String PREFIX = "basis.simple.rpc";

    /**
     * rpc server监听的端口
     */
    private Integer simpleRpcServerPort = 8888;

    /**
     * 一次请求最大的等待时间（如果到达等待时间后仍然没有收到响应结果，则直接抛出调用异常），单位秒
     */
    private Integer maxWaitTime = 60;

    private Nacos nacos = new Nacos();

    public static class Nacos {
        /**
         * 注册中心IP地址
         */
        private String registryServerIp;
        /**
         * 注册中心IP端口
         */
        private Integer registryServerPort;

        public String getRegistryServerIp() {
            return registryServerIp;
        }

        public void setRegistryServerIp(String registryServerIp) {
            this.registryServerIp = registryServerIp;
        }

        public Integer getRegistryServerPort() {
            return registryServerPort;
        }

        public void setRegistryServerPort(Integer registryServerPort) {
            this.registryServerPort = registryServerPort;
        }
    }

    public Integer getSimpleRpcServerPort() {
        return simpleRpcServerPort;
    }

    public void setSimpleRpcServerPort(Integer simpleRpcServerPort) {
        this.simpleRpcServerPort = simpleRpcServerPort;
    }

    public Nacos getNacos() {
        return nacos;
    }

    public void setNacos(Nacos nacos) {
        this.nacos = nacos;
    }

    public Integer getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Integer maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
}
