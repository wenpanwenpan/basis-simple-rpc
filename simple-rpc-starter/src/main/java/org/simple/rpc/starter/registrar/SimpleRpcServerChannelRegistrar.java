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
     * 保存provider端channel的map(key和value与CHANNEL_MAP相反)
     */
    private static final Map<Channel, String> CHANNEL_MAP_BACK = new ConcurrentHashMap<>();

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
        CHANNEL_MAP_BACK.put(channel, key);
        return true;
    }

    /**
     * 移除一个注册的channel
     *
     * @param key key
     * @author Mr_wenpan@163.com 2022/1/21 11:03 上午
     */
    public static void removeChannel(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        Channel channel = CHANNEL_MAP.get(key);
        CHANNEL_MAP_BACK.remove(channel);
        CHANNEL_MAP.remove(key);
    }

    /**
     * 根据channel移除本地缓存
     *
     * @param channel channel
     * @author Mr_wenpan@163.com 2022/1/21 11:39 上午
     */
    public static void removeChannelByValue(Channel channel) {
        String key = CHANNEL_MAP_BACK.get(channel);
        CHANNEL_MAP.remove(key);
        CHANNEL_MAP_BACK.remove(channel);
    }

    public static Map<String, Channel> getChannelMap() {
        return CHANNEL_MAP;
    }

    public static Map<Channel, String> getChannelMapBack() {
        return CHANNEL_MAP_BACK;
    }
}
