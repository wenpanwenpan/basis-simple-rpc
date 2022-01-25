package org.simple.rpc.starter.serialize;

import com.google.gson.*;
import org.simple.rpc.starter.exception.SimpleRpcDeserializeException;
import org.simple.rpc.starter.exception.SimpleRpcSerializeException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 序列化
 *
 * @author Mr_wenpan@163.com 2021/09/26 11:22
 */
public interface Serializer {

    /**
     * 反序列化算法
     *
     * @param clazz 反序列化的class类型
     * @param bytes 待反序列化的字节数组
     * @return T
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化算法
     *
     * @param object 将要序列化的对象
     * @return byte[]
     */
    <T> byte[] serialize(T object);

    /**
     * 序列化算法的顺序，自己定义，不能重复
     *
     * @return int
     */
    int order();

    enum Algorithm implements Serializer {

        /**
         * 使用Java序列化方式
         */
        Java {
            /**
             * 反序列化
             * @param clazz 反序列化结果Class
             * @param bytes 字节数组
             * @return T T
             * @author Mr_wenpan@163.com 2022/1/19 11:50 上午
             */
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                // auto close stream
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                    return (T) ois.readObject();
                } catch (Exception e) {
                    throw new SimpleRpcDeserializeException("serialize message failed.", e);
                }
            }

            /**
             * 序列化
             *
             * @param object 待序列化的对象
             * @return byte[] 序列化结果
             * @author Mr_wenpan@163.com 2022/1/19 11:51 上午
             */
            @Override
            public <T> byte[] serialize(T object) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new SimpleRpcSerializeException("serialize message failed.", e);
                }
            }

            @Override
            public int order() {
                return 0;
            }
        },

        /**
         * 使用JSON序列化（序列化对象时还有点问题，待优化）
         */
        Json {
            /**
             * 反序列化
             * @param clazz 反序列化结果Class
             * @param bytes 字节数组
             * @return T T
             */
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = new String(bytes, StandardCharsets.UTF_8);
                return gson.fromJson(json, clazz);
            }

            /**
             * 序列化
             *
             * @param object 待序列化的对象
             * @return byte[] 序列化结果
             * @author Mr_wenpan@163.com 2022/1/19 11:51 上午
             */
            @Override
            public <T> byte[] serialize(T object) {
                Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public int order() {
                return 1;
            }
        };

        /**
         * 序列化算法缓存
         */
        private static final Map<Integer, Algorithm> ALGORITHM_MAP = Collections.synchronizedMap(new HashMap<>());

        static {
            for (Algorithm algorithm : EnumSet.allOf(Algorithm.class)) {
                ALGORITHM_MAP.put(algorithm.order(), algorithm);
            }
        }

        /**
         * 通过order匹配对应的序列化算法
         */
        public static Algorithm match(int order) {

            return ALGORITHM_MAP.get(order);
        }
    }

    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        /**
         * 反序列化
         */
        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        /**
         * 序列化
         */
        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }

}