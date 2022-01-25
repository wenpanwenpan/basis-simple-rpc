package org.simple.rpc.starter.client;

import org.simple.rpc.starter.annotation.SimpleRpcClientReference;
import org.simple.rpc.starter.factory.SimpleRpcClientProxyCreateFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * simple rpc客户端后置处理器（为所有的标注了@SimpleRpcClientReference注解的类的属性设置值（动态代理对象））
 *
 * @author Mr_wenpan@163.com 2022/01/24 12:45
 */
public class SimpleRpcClientReferencePostProcessor implements BeanPostProcessor {

    private static final Map<Class<?>, Object> SIMPLE_RPC_REFERENCE_MAP = new ConcurrentHashMap<>();

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
                // 创建代理对象
                Object proxyService = getProxyService(field.getType());
                // 反射设置属性值
                field.setAccessible(true);
                ReflectionUtils.setField(field, bean, proxyService);
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

    /**
     * 获取代理对象，先从缓存中获取，如果本地缓存中没有则创建一个代理对象并返回
     *
     * @param type 类型
     * @return java.lang.Object 代理对象
     * @author Mr_wenpan@163.com 2022/1/24 4:05 下午
     */
    private static Object getProxyService(Class<?> type) {
        Assert.isTrue(type.isInterface(), "@SimpleRpcClientReference can only be specified on interface");
        // double check
        Object target = SIMPLE_RPC_REFERENCE_MAP.get(type);

        if (target != null) {
            return target;
        }

        synchronized (SimpleRpcClientReferencePostProcessor.class) {
            target = SIMPLE_RPC_REFERENCE_MAP.get(type);
            if (target != null) {
                return target;
            }
            target = SimpleRpcClientProxyCreateFactory.createProxyService(type);
            SIMPLE_RPC_REFERENCE_MAP.put(type, target);
            return target;
        }

    }

}
