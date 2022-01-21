package org.simple.rpc.starter.client;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 序列化ID生成器
 *
 * @author Mr_wenpan@163.com 2022/1/19 11:53 上午
 */
public abstract class SequenceIdGenerator {

    /**
     * 序列化ID
     */
    private static final AtomicInteger SEQUENCE_ID = new AtomicInteger();

    /**
     * 获取下一个ID
     */
    public static int nextId() {
        return SEQUENCE_ID.incrementAndGet();
    }
}