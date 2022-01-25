package org.simple.rpc.starter.registrar;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.simple.rpc.starter.exception.SimpleRpcChannelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * simple rpc provider 端 channel注册器
 *
 * @author Mr_wenpan@163.com 2022/01/21 10:53
 */
public class SimpleRpcServerChannelRegistrar {

    private final static Logger logger = LoggerFactory.getLogger(SimpleRpcServerChannelRegistrar.class);

    /**
     * 保存provider端channel的map
     */
    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 获取channel by key
     *
     * @param key key
     * @return channel
     * @author Mr_wenpan@163.com 2022/1/21 11:06 上午
     */
    public static Channel getChannel(String key) {

        return CHANNEL_MAP.get(key);
    }

    /**
     * 注册channel
     *
     * @param key     key
     * @param channel channel
     * @return boolean
     * @author Mr_wenpan@163.com 2022/1/21 11:02 上午
     */
    public static boolean registerChannel(String key, Channel channel) {
        if (StringUtils.isBlank(key) || Objects.isNull(channel)) {
            logger.error("simple rpc channel register occur excepion key and channel can not be null.");
            return false;
        }
        if (Objects.nonNull(CHANNEL_MAP.get(key))) {
            throw new SimpleRpcChannelException(String.format("[%s] has existed in channelMap, can not register.", key));
        }
        CHANNEL_MAP.put(key, channel);
        return true;
    }

    /**
     * 移除一个注册的channel，并关闭该channel
     *
     * @param key key
     * @author Mr_wenpan@163.com 2022/1/21 11:03 上午
     */
    public static void removeChannel(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        CHANNEL_MAP.remove(key);
        Channel channel = CHANNEL_MAP.get(key);
        if (channel.isActive()) {
            channel.close();
        }
    }

    /**
     * 关闭该服务建立的所有channel
     *
     * @author Mr_wenpan@163.com 2022/1/24 3:49 下午
     */
    public static void removeAllChannel() {
        if (CHANNEL_MAP.isEmpty()) {
            return;
        }
        CHANNEL_MAP.forEach((key, value) -> {
            CHANNEL_MAP.remove(key);
            Channel channel = CHANNEL_MAP.get(key);
            if (channel.isActive()) {
                channel.close();
            }
        });
    }

    public static Map<String, Channel> getChannelMap() {
        return CHANNEL_MAP;
    }

}
