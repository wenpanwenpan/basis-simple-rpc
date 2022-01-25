package org.simple.rpc.starter.config.properties;

import lombok.Data;
import org.simple.rpc.starter.serialize.Serializer;
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
    public static final String DEFAULT_GROUP = "DEFAULT-GROUP";
    public static final String DEFAULT_CLUSTER = "DEFAULT-CLUSTER";

    /**
     * rpc server监听的端口
     */
    private Integer simpleRpcServerPort = 8888;

    /**
     * 一次请求最大的等待时间（如果到达等待时间后仍然没有收到响应结果，则直接抛出调用异常），单位秒
     */
    private Integer maxWaitTime = 60;

    /**
     * 数据传输时使用的序列化算法
     */
    private Serializer.Algorithm serializerAlgorithm = Serializer.Algorithm.Java;

    /**
     * nacos相关配置
     */
    private Nacos nacos = new Nacos();

    /**
     * RpcRequestMessageHandlerThreadPool线程池相关配置
     */
    private RpcRequestMessageHandlerThreadPool threadPool = new RpcRequestMessageHandlerThreadPool();

    /**
     * nacos相关配置
     */
    public static class Nacos {

        /**
         * 组名称
         */
        private String groupName;

        /**
         * 集群名称
         */
        private String clusterName;

        /**
         * 注册中心IP地址
         */
        private String registryServerIp;

        /**
         * 注册中心IP端口
         */
        private Integer registryServerPort;

        public Nacos() {
            groupName = DEFAULT_GROUP;
            clusterName = DEFAULT_CLUSTER;
        }

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

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }
    }

    /**
     * RpcRequestMessageHandlerExecutor 线程池配置
     */
    public static class RpcRequestMessageHandlerThreadPool {
        /**
         * 核心线程数
         */
        private int corePoolSize = 5;
        /**
         * 最大线程数
         */
        private int maxPoolSize = 8;
        /**
         * 线程队列容量
         */
        private int queueCapacity = 1024;
        /**
         * 线程前缀
         */
        private String namePrefix = "simple-rpc-executor";
        /**
         * 空闲销毁时间
         */
        private int keepAliveSeconds = 600;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public String getNamePrefix() {
            return namePrefix;
        }

        public void setNamePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        public int getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
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
