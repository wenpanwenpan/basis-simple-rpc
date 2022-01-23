package org.simple.rpc.starter.nacos;

import com.alibaba.boot.nacos.discovery.properties.NacosDiscoveryProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.apache.commons.lang3.StringUtils;
import org.simple.rpc.starter.config.properties.SimpleRpcProperties;
import org.simple.rpc.starter.config.properties.SpringParamsProperties;
import org.simple.rpc.starter.constant.SimpleRpcConstants;
import org.simple.rpc.starter.registrar.SimpleRpcServerChannelRegistrar;
import org.simple.rpc.starter.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * nacos注册管理器(提供服务注册到nacos相关API)
 *
 * @author Mr_wenpan@163.com 2022/01/20 18:08
 */
public final class NacosRegistrarManager implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(NacosRegistrarManager.class);

    /**
     * simple rpc服务端注册到注册中心的前缀，方便区分
     */
    public static final String PREFIX = "simple-rpc-";

    /**
     * namingService
     */
    private final NamingService namingService;

    /**
     * 随机
     */
    private static final Random RANDOM = new Random();

    /**
     * simple rpc 配置
     */
    private final SimpleRpcProperties simpleRpcProperties;

    /**
     * spring相关参数配置
     */
    private final SpringParamsProperties springParamsProperties;

    /**
     * nacos服务发现Properties
     */
    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    /**
     * 是否初始化
     */
    private final AtomicBoolean namingServiceInitFlag = new AtomicBoolean(Boolean.FALSE);

    public NacosRegistrarManager(SimpleRpcProperties simpleRpcProperties,
                                 SpringParamsProperties springParamsProperties,
                                 NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        this.springParamsProperties = springParamsProperties;
        this.simpleRpcProperties = simpleRpcProperties;
        namingService = getNacosNamingService();
    }

    /**
     * 初始化
     *
     * @return com.alibaba.nacos.api.naming.NamingService
     */
    private NamingService getNacosNamingService() {
        if (!namingServiceInitFlag.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            throw new RuntimeException("namingService has been init.");
        }
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
//        namingService.registerInstance(PREFIX + serverName, simpleRpcProperties.getNacos().getGroupName(),
//                address.getHostName(), address.getPort(), simpleRpcProperties.getNacos().getClusterName());
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
//        return namingService.getAllInstances(PREFIX + serverName,
//                simpleRpcProperties.getNacos().getGroupName(),
//                Collections.singletonList(simpleRpcProperties.getNacos().getGroupName()));
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
     *
     * @param serverName serverName
     * @author Mr_wenpan@163.com 2022/1/21 2:46 下午
     */
    public void clearRegister(@NonNull String serverName) throws NacosException {
        if (StringUtils.isBlank(serverName)) {
            return;
        }
        serverName = PREFIX + serverName;
        // 从nacos上注销该实例（这里仅仅会从nacos上注销serverName下的当前ip + 端口的实例）
        try {
            namingService.deregisterInstance(serverName, NetworkUtil.getCurrentHostIp(), simpleRpcProperties.getSimpleRpcServerPort());
        } catch (UnknownHostException e) {
            throw new RuntimeException("logout from nacos failed.");
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("NacosRegistrarManager was destroyed, the registration information will be cleared from the registry");
        // todo 主动清除本地缓存的到provider端的通信channel
        List<Instance> instances = getAllInstanceByServerName(springParamsProperties.getName());
        if (!CollectionUtils.isEmpty(instances)) {
            for (Instance instance : instances) {
                SimpleRpcServerChannelRegistrar.removeChannel(buildUnionKeyByInstance(instance));
            }
        }
        // 清除本实例在nacos上的注册信息
        clearRegister(springParamsProperties.getName());
        namingServiceInitFlag.compareAndSet(Boolean.TRUE, Boolean.FALSE);
    }

    private static String buildUnionKeyByInstance(Instance instance) {
        if (Objects.isNull(instance)) {
            return StringUtils.EMPTY;
        }
        return instance.getServiceName() + instance.getIp() + SimpleRpcConstants.Symbol.COLON + instance.getPort();
    }

}
