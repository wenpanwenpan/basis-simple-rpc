package org.simple.rpc.starter.server;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import org.simple.rpc.starter.annotation.SimpleRpcServerExpose;
import org.simple.rpc.starter.config.properties.SimpleRpcProperties;
import org.simple.rpc.starter.discovery.NacosRegistrarManager;
import org.simple.rpc.starter.helper.ApplicationContextHelper;
import org.simple.rpc.starter.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * simple rpc 服务端接口暴露（服务启动后将标注有@SimpleRpcServerExpose注解的类都暴露到注册中心）
 *
 * @author Mr_wenpan@163.com 2022/01/24 12:03
 */
public class SimpleRpcServerExposeRunner implements CommandLineRunner {

    /**
     * 暴露的接口信息缓存
     */
    private final static Set<String> INTERFACE_REGISTER_INFO_SET = new ConcurrentHashSet<>();

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SimpleRpcProperties simpleRpcProperties;

    private final NacosRegistrarManager nacosRegistrarManager;

    public SimpleRpcServerExposeRunner(SimpleRpcProperties simpleRpcProperties,
                                       NacosRegistrarManager nacosRegistrarManager) {
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

        // 扫描到标注有@SimpleRpcServerExpose注解的类
        ApplicationContext context = ApplicationContextHelper.getContext();
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(SimpleRpcServerExpose.class);

        String currentHostIp = NetworkUtil.localIp();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(currentHostIp, simpleRpcProperties.getSimpleRpcServerPort());

        // 接口暴露到注册中心
        beansWithAnnotation.forEach((key, value) -> {
            Class<?>[] interfaces = value.getClass().getInterfaces();
            if (!Objects.isNull(interfaces)) {
                // 将接口注册到nacos(以接口全限定名为服务名)
                try {
                    for (Class<?> anInterface : interfaces) {
                        String interfaceName = anInterface.getName();
                        INTERFACE_REGISTER_INFO_SET.add(interfaceName);
                        nacosRegistrarManager.registerServer(interfaceName, inetSocketAddress);
                    }
                } catch (NacosException e) {
                    throw new RuntimeException("can not register to nacos, occur exception.", e);
                }
            }
        });

        // 添加钩子，关闭时清理注册信息
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            INTERFACE_REGISTER_INFO_SET.forEach(interfaceName -> {
                try {
                    nacosRegistrarManager.clearRegister(interfaceName);
                } catch (Exception ex) {
                    logger.error("hook clear register info from nacos occur exception.");
                }
            });
        }));
    }

    /**
     * 获取到该实例所有已经暴露的接口
     */
    public static Set<String> getInterfaceRegisterInfoSet() {

        return INTERFACE_REGISTER_INFO_SET;
    }

}
