package org.simple.rpc.provider.service.rpc;

import lombok.extern.slf4j.Slf4j;
import org.simple.rpc.inf.entity.Customer;
import org.simple.rpc.inf.service.HelloSimpleRpcService;
import org.simple.rpc.starter.annotation.SimpleRpcServerExpose;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * HelloSimpleRpcService实现
 *
 * @author Mr_wenpan@163.com 2022/1/24 10:47 上午
 */
@Slf4j
@SimpleRpcServerExpose
@Service
public class HelloSimpleRpcImpl implements HelloSimpleRpcService {

    @Override
    public String sayHello(String name) {
        log.info("=================>>>>>>>>>>>我是服务提供端的HelloSimpleRpcImpl. hello {}", name);
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return "success-" + name;
    }

    @Override
    public String sayHello() {
        System.out.println("provider端的无参的sayHello执行了....");
        return "no args sayHello execute success. ";
    }

    @Override
    public String sayHello(String name, Integer age, Double money) {
        System.out.println("provider端的多参的sayHello执行了....");
        return "mutil args sayHello execute success. ";
    }

    @Override
    public List<Customer> getCustomerFriends(String str, Customer customer) {
        System.out.println("provider端的getCustomerFriend执行了....");
        List<Customer> res = new ArrayList<>();
        // 创建返回结果
        for (int i = 0; i < 5; i++) {
            Customer customer1 = new Customer();
            customer1.setCustomerAge(customer.getCustomerAge() + i);
            customer1.setBirthDay(new Date());
            customer1.setCustomerName(customer.getCustomerName() + "-" + i);
            customer1.setPhone(customer.getPhone());
            customer1.setPhoneList(customer.getPhoneList());
            res.add(customer1);
        }
        return res;
    }

}