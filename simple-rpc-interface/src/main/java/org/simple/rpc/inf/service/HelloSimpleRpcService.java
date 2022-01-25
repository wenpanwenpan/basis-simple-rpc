package org.simple.rpc.inf.service;

import org.simple.rpc.inf.entity.Customer;

import java.util.List;

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

    /**
     * 无参 sayHello
     *
     * @return java.lang.String
     * @author Mr_wenpan@163.com 2022/1/24 5:25 下午
     */
    String sayHello();

    /**
     * 多参数sayHello
     *
     * @param name  name
     * @param age   age
     * @param money money
     * @return java.lang.String
     * @author Mr_wenpan@163.com 2022/1/24 5:32 下午
     */
    String sayHello(String name, Integer age, Double money);

    /**
     * 通过一个customer获取多个customer（测试对象和数组以及多参数）
     *
     * @param customer customer
     * @param str      任意字符串
     * @return java.util.List<org.simple.rpc.inf.entity.Customer>
     * @author Mr_wenpan@163.com 2022/1/24 5:33 下午
     */
    List<Customer> getCustomerFriends(String str, Customer customer);

}