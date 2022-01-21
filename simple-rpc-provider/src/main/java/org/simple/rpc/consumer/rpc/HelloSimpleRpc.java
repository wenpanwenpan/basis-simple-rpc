package org.simple.rpc.consumer.rpc;

/**
 * 服务提供端测试
 *
 * @author Mr_wenpan@163.com 2022/1/20 10:35 上午
 */
public interface HelloSimpleRpc {

    /**
     * sayHello方法
     *
     * @param name name
     */
    void sayHello(String name);
}