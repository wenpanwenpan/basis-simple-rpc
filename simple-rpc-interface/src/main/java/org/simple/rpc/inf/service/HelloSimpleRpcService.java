package org.simple.rpc.inf.service;

/**
 * 服务提供端测试
 *
 * @author Mr_wenpan@163.com 2022/1/20 10:35 上午
 */
public interface HelloSimpleRpcService {

    /**
     * sayHello方法
     *
     * @param name name
     * @return String
     */
    String sayHello(String name);
}