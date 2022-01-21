package org.simple.rpc.starter.protocol;

import com.google.gson.*;
import org.simple.rpc.starter.exception.SimpleRpcDeserializeException;
import org.simple.rpc.starter.exception.SimpleRpcSerializeException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

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
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (Exception e) {
                    throw new SimpleRpcDeserializeException("deserialize message failed.", e);
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
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
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
         * 使用JSON序列化
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
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public int order() {
                return 1;
            }
        };

    }

    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        /**
         * 反序列化
         */
        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                System.out.println("=======>>>>>>str = " + str);
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