package org.simple.rpc.consumer.controller;

import org.simple.rpc.inf.service.HelloSimpleRpcService;
import org.simple.rpc.starter.annotation.SimpleRpcClientReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消费方测试controller
 *
 * @author Mr_wenpan@163.com 2022/01/19 15:52
 */
@RestController("ConsumerHelloController.v1")
@RequestMapping({"/v1/consumer-hello"})
public class ConsumerHelloController {

    @SimpleRpcClientReference
    private HelloSimpleRpcService helloSimpleRpcService;

    @GetMapping("/test")
    public ResponseEntity<String> test(String name) {

        System.out.println("consumer 接收到请求");
        return ResponseEntity.ok("test");
    }

    @GetMapping
    public ResponseEntity<String> sayHello(String name) {
        // 这里像调用本地方法一样远程调用
        String str = helloSimpleRpcService.sayHello(name);
        System.out.println("rpc 调用返回 str = " + str);
        return ResponseEntity.ok(str);
    }

}
