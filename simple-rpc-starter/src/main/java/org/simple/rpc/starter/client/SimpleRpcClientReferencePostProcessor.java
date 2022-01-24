package org.simple.rpc.starter.client;

import org.simple.rpc.starter.annotation.SimpleRpcClientReference;
import org.simple.rpc.starter.factory.SimpleClientProxyCreateFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * simple rpc客户端后置处理器（为所有的标注了@SimpleRpcClientReference注解的类的属性设置值（动态代理对象））
 * https://www.jianshu.com/p/e0218c142d03
 *
 * @author Mr_wenpan@163.com 2022/01/24 12:45
 */
public class SimpleRpcClientReferencePostProcessor implements BeanPostProcessor {

    private static final Map<String, Object> SIMPLE_RPC_REFERENCE_MAP = new ConcurrentHashMap<>();

    /**
     * bean初始化之前
     *
     * @param bean     bean
     * @param beanName bean名称
     * @return java.lang.Object
     * @author Mr_wenpan@163.com 2022/1/24 12:47 下午
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 扫描含有@SimpleRpcClientReference注解的类
        Class<?> objClz = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        try {
            for (Field field : objClz.getDeclaredFields()) {
                SimpleRpcClientReference reference = field.getAnnotation(SimpleRpcClientReference.class);
                if (Objects.isNull(reference)) {
                    continue;
                }
                Assert.isTrue(field.getType().isInterface(), "@SimpleRpcClientReference can only be specified on interface");
                // 创建代理对象
                Object proxyService = SimpleClientProxyCreateFactory.createProxyService(field.getType());
                // todo 动态代理对象做缓存
                // 反射设置属性值
                field.setAccessible(true);
                field.set(bean, proxyService);
            }
        } catch (Exception e) {
            throw new BeanCreationException(beanName, e);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }
}
