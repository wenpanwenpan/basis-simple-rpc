package org.simple.rpc.starter.config;

import com.alibaba.boot.nacos.discovery.properties.NacosDiscoveryProperties;
import org.simple.rpc.starter.client.SimpleRpcClientReferencePostProcessor;
import org.simple.rpc.starter.config.properties.SimpleRpcProperties;
import org.simple.rpc.starter.config.properties.SpringParamsProperties;
import org.simple.rpc.starter.nacos.NacosRegistrarManager;
import org.simple.rpc.starter.server.RpcRequestMessageHandlerExecutor;
import org.simple.rpc.starter.server.SimpleRpcServerBootStrapListener;
import org.simple.rpc.starter.server.SimpleRpcServerExposeRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 开启simple rpc 自动配置类
 *
 * @author Mr_wenpan@163.com 2022/01/24 15:27
 */
@Configuration
public class EnableSimpleRpcAutoConfiguration {

    /**
     * 注入SimpleRpcServerExposeRunner，暴露provider端接口到注册中心
     */
    @Bean
    public SimpleRpcServerExposeRunner simpleRpcServerExposeRunner(SimpleRpcProperties simpleRpcProperties,
                                                                   NacosRegistrarManager nacosRegistrarManager) {
        return new SimpleRpcServerExposeRunner(simpleRpcProperties, nacosRegistrarManager);
    }

    /**
     * 注入NacosRegistrarManager
     */
    @Bean
    public NacosRegistrarManager nacosRegistrarManager(SimpleRpcProperties simpleRpcProperties,
                                                       SpringParamsProperties springParamsProperties,
                                                       NacosDiscoveryProperties nacosDiscoveryProperties) {
        return new NacosRegistrarManager(simpleRpcProperties, springParamsProperties, nacosDiscoveryProperties);
    }

    /**
     * 注入bean后置处理器，为所有的标注了@SimpleRpcClientReference注解的类的属性设置值
     */
    @Bean
    public SimpleRpcClientReferencePostProcessor simpleRpcClientReferencePostProcessor() {

        return new SimpleRpcClientReferencePostProcessor();
    }

    /**
     * 注入simple rpc server启动监听（启动netty server）
     */
    @Bean
    public SimpleRpcServerBootStrapListener simpleRpcServerBootStrapListener(SimpleRpcProperties simpleRpcProperties) {
        return new SimpleRpcServerBootStrapListener(simpleRpcProperties);
    }

    /**
     * 注入RpcRequestMessageHandlerExecutor，用户可自定义线程池替换
     */
    @Bean
    @ConditionalOnMissingBean(RpcRequestMessageHandlerExecutor.class)
    public RpcRequestMessageHandlerExecutor rpcRequestMessageHandlerExecutor(SimpleRpcProperties simpleRpcProperties) {
        RpcRequestMessageHandlerExecutor threadPoolTaskExecutor = new RpcRequestMessageHandlerExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(simpleRpcProperties.getThreadPool().getMaxPoolSize());
        threadPoolTaskExecutor.setCorePoolSize(simpleRpcProperties.getThreadPool().getCorePoolSize());
        threadPoolTaskExecutor.setQueueCapacity(simpleRpcProperties.getThreadPool().getQueueCapacity());
        threadPoolTaskExecutor.setThreadNamePrefix(simpleRpcProperties.getThreadPool().getNamePrefix());
        threadPoolTaskExecutor.setKeepAliveSeconds(simpleRpcProperties.getThreadPool().getKeepAliveSeconds());
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 允许核心线程超时回收
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(Boolean.TRUE);
        return threadPoolTaskExecutor;
    }
}
