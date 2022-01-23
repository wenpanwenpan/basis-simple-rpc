package org.simple.rpc.starter.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.simple.rpc.starter.message.Message;
import org.simple.rpc.starter.util.PropertyReadUtil;

import java.util.List;

/**
 * 自定义编码解码器（可共享的）（出站、入站handler）
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的
 *
 * @author Mr_wenpan@163.com 2021/09/22 10:53
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    /**
     * 编码（将编码后的消息写入到outList中）
     *
     * @param ctx     上下文
     * @param msg     待编码的消息
     * @param outList 编码消息结果收集
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        // 创建一个ByteBuf（开辟一块内存空间，可以是堆内存也可以是堆外内存）
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4个直接的魔术
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1个字节的版本号
        out.writeByte(1);
        // 3. 1个字节的序列化方式： 0 ： jdk ， 1 ： JSON
        int order = PropertyReadUtil.getSerializerAlgorithm().order();
        out.writeByte(order);
        // 4. 1个字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4个字节的序列号
        out.writeInt(msg.getSequenceId());
        // 1个字节对齐填充（无意义）
        out.writeByte(0xff);
        // 6. 序列化消息对象
        byte[] bytes = PropertyReadUtil.getSerializerAlgorithm().serialize(msg);
        // 7. 4个字节消息长度
        out.writeInt(bytes.length);
        // 8. 写入内容到byteBuf中
        out.writeBytes(bytes);

        System.out.println("消息编码码.....end " + msg);
        // 写入到outList中，以保证传递到下一个handler（这里可以看到，传递到这个handler以后的消息就是经过解码的消息了，就不含消息长度、魔数等字段了）
        outList.add(out);
    }

    /**
     * 解码（将解码后的消息写入到out中，以便于后面的handler使用）
     *
     * @param ctx ChannelHandlerContext
     * @param in  待解码的ByteBuf数据
     * @param out 解码后的数据
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 消息的魔数
        int magicNum = in.readInt();
        // 消息版本号
        byte version = in.readByte();
        // 序列化算法
        byte serializerAlgorithm = in.readByte();
        // 消息类型
        byte messageType = in.readByte();
        // 消息序列号
        int sequenceId = in.readInt();
        // 读掉这一个字节的无用字节
        in.readByte();
        // 消息的长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        // 从ByteBuf读取length长度的数据到bytes数组
        in.readBytes(bytes, 0, length);

        // 找到序列化算法
        Serializer.Algorithm algorithm = null;
        Serializer.Algorithm[] values = Serializer.Algorithm.values();
        for (Serializer.Algorithm value : values) {
            if (value.order() == serializerAlgorithm) {
                algorithm = value;
                break;
            }
        }
        if (algorithm == null) {
            throw new RuntimeException("无法找到反序列化算法，请检查算法order");
        }
        // 确定具体消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(messageClass, bytes);

//        log.debug("==========>>>>>>>> {}, {}, {}, {}, {}", magicNum, version, messageType, sequenceId, length);
//        log.debug("{}", message);

        System.out.println("消息解码......end " + message);
        // 传递到下一个handler中（这里可以看到，传递到这个handler以后的消息就是经过解码的消息了，就不含消息长度、魔数等字段了）
        out.add(message);
    }

}
