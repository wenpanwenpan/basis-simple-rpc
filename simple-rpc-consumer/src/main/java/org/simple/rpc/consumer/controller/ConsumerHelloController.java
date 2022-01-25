package org.simple.rpc.consumer.controller;

import org.simple.rpc.inf.entity.Customer;
import org.simple.rpc.inf.entity.Phone;
import org.simple.rpc.inf.service.HelloSimpleRpcService;
import org.simple.rpc.starter.annotation.SimpleRpcClientReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @GetMapping("/say-hello-1")
    public ResponseEntity<String> sayHello1(String name) throws InterruptedException {
        // 这里像调用本地方法一样远程调用
        String str = helloSimpleRpcService.sayHello(name);
        TimeUnit.SECONDS.sleep(10);
        System.out.println("sayHello1 rpc 调用返回 str = " + str);
        return ResponseEntity.ok(str);
    }

    @GetMapping("/say-hello-2")
    public ResponseEntity<String> sayHello2() {
        // 这里像调用本地方法一样远程调用
        String str = helloSimpleRpcService.sayHello();
        System.out.println("sayHello2 rpc 调用返回 str = " + str);
        return ResponseEntity.ok(str);
    }

    @GetMapping("/say-hello-3")
    public ResponseEntity<String> sayHello3(String name, Integer age, Double money) {
        // 这里像调用本地方法一样远程调用
        String str = helloSimpleRpcService.sayHello(name, age, money);
        System.out.println("sayHello3 rpc 调用返回 str = " + str);
        return ResponseEntity.ok(str);
    }

    @GetMapping("/get-customer-friend")
    public ResponseEntity<List<Customer>> getCustomerFriends(String str) {
        // 这里像调用本地方法一样远程调用
        Customer customer = new Customer();
        customer.setCustomerName("文攀");
        customer.setBirthDay(new Date());
        Phone phone = new Phone();
        phone.setBrand("小米");
        customer.setPhone(phone);
        customer.setPhoneList(Collections.singletonList(phone));
        customer.setCustomerAge(20);
        System.out.println("getCustomerFriends rpc 开始执行");
        List<Customer> customerFriends = helloSimpleRpcService.getCustomerFriends(str, customer);
        System.out.println("getCustomerFriends rpc 调用返回 customerFriends = " + customerFriends);
        return ResponseEntity.ok(customerFriends);
    }

}
