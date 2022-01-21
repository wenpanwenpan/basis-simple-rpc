package org.simple.rpc.starter.registrar;

import java.net.InetSocketAddress;

/**
 * simple rpc 服务注册
 *
 * @author Mr_wenpan@163.com 2022/01/20 16:52
 */
public interface ServerRegistry {
    /**
     * 将服务的名称和地址注册进服务注册中心
     *
     * @param serviceName       服务名称
     * @param inetSocketAddress 地址
     * @author Mr_wenpan@163.com 2022/1/20 4:54 下午
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 根据服务名称从注册中心获取到服务提供者的地址
     *
     * @param serviceName 服务名称
     * @return java.net.InetSocketAddress 地址
     * @author Mr_wenpan@163.com 2022/1/20 4:54 下午
     */
    InetSocketAddress getService(String serviceName);
}
