package org.simple.rpc.consumer.rpc.impl;

import org.simple.rpc.consumer.rpc.HelloSimpleRpc;
import org.springframework.stereotype.Service;

/**
 * HelloSimpleRpc实现
 *
 * @author Mr_wenpan@163.com 2022/01/20 10:36
 */
@Service
public class HelloSimpleRpcImpl implements HelloSimpleRpc {

    @Override
    public String sayHello(String name) {
        System.out.println("hello " + name);
        System.out.println("=================>>>>>>>>>>>我是服务提供端的HelloSimpleRpcImpl.");
        return "success-" + name;
    }

}
