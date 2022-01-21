package org.simple.rpc.starter.registrar.impl;

import org.simple.rpc.starter.registrar.ServerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * nacos服务注册
 *
 * @author Mr_wenpan@163.com 2022/01/20 16:52
 */

public class NacosServerRegistryImpl implements ServerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServerRegistryImpl.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
//        try {
////            NacosRegistrar.registerServer(serviceName, inetSocketAddress);
//            logger.info("serviceName : [{}], address : [{}], port : [{}] It has been registered in the registry.",
//                    serviceName, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
//        } catch (NacosException e) {
//            throw new RuntimeException(String.format("[%s] register to nacos occur exception.", serviceName), e);
//        }
    }

    @Override
    public InetSocketAddress getService(String serviceName) {
        return null;
    }
}
