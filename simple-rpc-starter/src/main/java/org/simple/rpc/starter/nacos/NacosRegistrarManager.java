package org.simple.rpc.starter.nacos;

import com.alibaba.boot.nacos.discovery.properties.NacosDiscoveryProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * nacos注册管理器(保证容器启动好后才能被主动使用)
 *
 * @author Mr_wenpan@163.com 2022/01/20 18:08
 */
public class NacosRegistrarManager {

    private static final Logger logger = LoggerFactory.getLogger(NacosRegistrarManager.class);

    /**
     * simple rpc服务端注册到注册中心的前缀，方便区分
     */
    private static final String PREFIX = "simple-rpc-";

    /**
     * namingService
     */
    private final NamingService namingService;

    /**
     * nacos服务发现Properties
     */
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    /**
     * 随机
     */
    private static final Random RANDOM = new Random();

    /**
     * 存放注册到nacos的所有服务名集合
     */
    private static final ConcurrentHashMap<String, InetSocketAddress> SERVICE_REGISTER_MAP = new ConcurrentHashMap<>();

    public NacosRegistrarManager(NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        namingService = getNacosNamingService();
    }

    /**
     * 初始化
     *
     * @return com.alibaba.nacos.api.naming.NamingService
     */
    private NamingService getNacosNamingService() {
        try {
            return NacosFactory.createNamingService(nacosDiscoveryProperties.getServerAddr());
        } catch (NacosException e) {
            throw new RuntimeException("create namingService and connect to nacos occur exception.", e);
        }
    }

    /**
     * 服务注册到本地缓存
     *
     * @param serverName 服务名
     * @param address    服务地址
     */
    public void registerServer(String serverName, InetSocketAddress address) throws NacosException {
        if (StringUtils.isBlank(serverName) || Objects.isNull(address)) {
            logger.warn("register {} to nacos failed, because serverName or address is null.", serverName);
            return;
        }
        // 注册到nacos
        namingService.registerInstance(PREFIX + serverName, address.getHostName(), address.getPort());
        // 注册到本地缓存
        SERVICE_REGISTER_MAP.put(serverName, address);
    }

    /**
     * 获取当前服务名下的所有实例
     *
     * @param serverName 服务名
     * @return java.util.List<com.alibaba.nacos.api.naming.pojo.Instance>
     */
    public List<Instance> getAllInstanceByServerName(@NonNull String serverName) throws NacosException {
        if (StringUtils.isBlank(serverName)) {
            return new ArrayList<>();
        }

        return namingService.getAllInstances(PREFIX + serverName);
    }

    /**
     * 通过注册的服务名随机获取一个实例
     *
     * @param serverName 注册的服务名
     * @return com.alibaba.nacos.api.naming.pojo.Instance
     * @author Mr_wenpan@163.com 2022/1/20 10:39 下午
     */
    public Instance getRandomInstanceByServerName(@NonNull String serverName) throws NacosException {
        List<Instance> allInstances = getAllInstanceByServerName(serverName);
        if (allInstances == null || allInstances.size() <= 0) {
            return null;
        }
        return allInstances.get(RANDOM.nextInt(allInstances.size()));
    }

    /**
     * 根据serverName获取一个provider实例
     *
     * @param serverName serverName
     * @return com.alibaba.nacos.api.naming.pojo.Instance
     * @author Mr_wenpan@163.com 2022/1/21 12:33 下午
     */
    public static Instance getInstanceByServerName(@NonNull String serverName) throws NacosException {
        // todo 按照配置文件中指定的策略，从nacos上获取实例

        return null;
    }

    /**
     * 根据serverName注销服务
     */
    public void clearRegister(@NonNull String serverName) throws NacosException {
        if (StringUtils.isBlank(serverName)
                || SERVICE_REGISTER_MAP.isEmpty()
                || Objects.isNull(SERVICE_REGISTER_MAP.get(serverName))) {
            return;
        }
        InetSocketAddress inetSocketAddress = SERVICE_REGISTER_MAP.get(serverName);
        String host = inetSocketAddress.getHostName();
        int port = inetSocketAddress.getPort();
        // 从nacos上注销
        namingService.deregisterInstance(serverName, host, port);
        // 从本地缓存注销
        SERVICE_REGISTER_MAP.remove(serverName);
    }

}
