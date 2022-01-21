package org.simple.rpc.provider;

import org.simple.rpc.starter.annotation.EnableSimpleRpcClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 服务提供者启动类
 *
 * @author Mr_wenpan@163.com 2022/01/19 16:08
 */
@ComponentScan(basePackages = {"org.simple.rpc.consumer", "org.simple.rpc.provider"})
@EnableSimpleRpcClients
@SpringBootApplication
public class SimpleRpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleRpcProviderApplication.class, args);
    }

}
