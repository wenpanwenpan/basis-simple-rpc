package org.simple.rpc.consumer.rpc;

import org.simple.rpc.starter.annotation.SimpleRpcClient;

/**
 * 简单rpc客户端测试
 *
 * @author Mr_wenpan@163.com 2022/01/19 15:47
 */
@SimpleRpcClient(value = "simple-rpc-provider")
public interface HelloSimpleRpc {

    /**
     * sayHello方法
     *
     * @param name name
     */
    void sayHello(String name);
}
