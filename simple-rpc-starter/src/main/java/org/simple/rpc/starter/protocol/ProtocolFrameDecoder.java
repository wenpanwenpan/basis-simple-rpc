package org.simple.rpc.starter.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 自定义帧解码器(其实就是调用LengthFieldBasedFrameDecoder，按照自定义协议传入固定参数而已)
 * LengthFieldBasedFrameDecoder : 预设长度解码器
 *
 * @author Mr_wenpan@163.com 2021/9/23 10:46 上午
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    private static final int MAX_FRAME_LENGTH = 40960;
    private static final int LENGTH_FIELD_OFFSET = 12;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    /**
     * 无参构造器，使用默认值创建帧解码器
     */
    public ProtocolFrameDecoder() {
        this(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP);
    }

    /**
     * 构造器
     *
     * @param maxFrameLength      一帧消息的最大长度，超过该长度就会报错
     * @param lengthFieldOffset   消息长度的偏移量
     * @param lengthFieldLength   用多少个字节来表示消息长度
     * @param lengthAdjustment    消息长度位置后面需要调整（即往后数）多少个字节才能开始读取消息正文
     * @param initialBytesToStrip 需要剥离多少个字节。即，一条消息是包含着`消息长度 + 消息内容 + 其他内容`组成的。
     *                            这里可以通过指定字节的长度，来将那些额外的字节剥离掉，只剩消息体，然后将消息体传递给后面的handler
     */
    public ProtocolFrameDecoder(int maxFrameLength,
                                int lengthFieldOffset,
                                int lengthFieldLength,
                                int lengthAdjustment,
                                int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}