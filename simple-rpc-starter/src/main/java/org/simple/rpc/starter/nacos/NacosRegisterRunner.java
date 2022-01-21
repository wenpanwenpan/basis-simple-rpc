package org.simple.rpc.starter.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import org.apache.commons.lang3.StringUtils;
import org.simple.rpc.starter.config.properties.SimpleRpcProperties;
import org.simple.rpc.starter.config.properties.SpringParamsProperties;
import org.simple.rpc.starter.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * nacos注册runner(服务启动时，把自己注册到注册中心)
 *
 * @author Mr_wenpan@163.com 2022/01/20 18:37
 */
public class NacosRegisterRunner implements CommandLineRunner {

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SpringParamsProperties springParamsProperties;

    private SimpleRpcProperties simpleRpcProperties;

    private NacosRegistrarManager nacosRegistrarManager;

    /**
     * whether the container has been initialized
     */
    private volatile boolean initialized = false;

    public NacosRegisterRunner(SpringParamsProperties springParamsProperties,
                               SimpleRpcProperties simpleRpcProperties,
                               NacosRegistrarManager nacosRegistrarManager) {
        this.springParamsProperties = springParamsProperties;
        this.simpleRpcProperties = simpleRpcProperties;
        this.nacosRegistrarManager = nacosRegistrarManager;
    }

    @Override
    public void run(String... args) throws Exception {
        // 防止并发
        if (!isInit.compareAndSet(false, true)) {
            logger.info("nacos register has been is init...");
            return;
        }
        // 获取应用名称
        String applicationName = springParamsProperties.getName();

        // 获取到当前服务名称
        if (StringUtils.isBlank(applicationName)) {
            logger.warn("register {} to nacos failed, because application name is null.", applicationName);
            return;
        }

        try {
            // 注册到nacos
            String currentHostIp = NetworkUtil.localIp();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(currentHostIp, simpleRpcProperties.getSimpleRpcServerPort());
            nacosRegistrarManager.registerServer(applicationName, inetSocketAddress);
        } catch (NacosException e) {
            throw new RuntimeException("can not register to nacos, occur exception.", e);
        }
    }

}
