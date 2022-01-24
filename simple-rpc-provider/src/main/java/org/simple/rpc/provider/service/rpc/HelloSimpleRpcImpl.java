package org.simple.rpc.provider.service.rpc;

import org.simple.rpc.inf.service.HelloSimpleRpcService;
import org.simple.rpc.starter.annotation.SimpleRpcServerExpose;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * HelloSimpleRpcService实现
 *
 * @author Mr_wenpan@163.com 2022/1/24 10:47 上午
 */
@SimpleRpcServerExpose
@Service
public class HelloSimpleRpcImpl implements HelloSimpleRpcService {

    @Override
    public String sayHello(String name) {
        System.out.println("hello " + name);
        System.out.println("=================>>>>>>>>>>>我是服务提供端的HelloSimpleRpcImpl.");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return "success-" + name;
    }

}