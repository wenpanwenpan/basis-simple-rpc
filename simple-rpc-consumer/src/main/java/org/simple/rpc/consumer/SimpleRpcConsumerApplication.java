package org.simple.rpc.consumer;

import org.simple.rpc.starter.annotation.EnableSimpleRpcClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 简单rpc消费方启动类
 *
 * @author Mr_wenpan@163.com 2022/01/19 15:39
 */
@EnableSimpleRpcClients
@SpringBootApplication
public class SimpleRpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleRpcConsumerApplication.class, args);
    }

}
