package com.nexus.binary.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * @author panlf
 * @date 2023/5/17
 */
@Slf4j
public class DecoderHandler extends ByteToMessageDecoder {

    /**
     * 头 2 个字节
     * len 2个字节
     * 后续字节就是Data字节
     * 尾 2 个字节
     */
    private final static int MIN_DAA_LEN=6;

    private final byte[] PROTOCOL_HEADER = {(byte) 0xef, (byte) 0xef};

    private final byte[] PROTOCOL_TAIL = {(byte) 0xfd, (byte) 0xfd};

    @Override
        protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        log.info("读取到数据位数:{}",byteBuf.readableBytes());
        if(byteBuf.readableBytes() > MIN_DAA_LEN){
            log.info("开始解码数据...");

            //标记读操作的指针
            byteBuf.markReaderIndex();

            ByteBuf head = byteBuf.readBytes(2);
            //如果以这个开头
            if(Arrays.equals(head.array(), PROTOCOL_HEADER)){
                log.info("数据头正确");

                //读数据长度
                ByteBuf lenByteBuf =  byteBuf.readBytes(2);

                short len = ByteBuffer.wrap(lenByteBuf.array()).getShort();
                log.info("数据长度为:{}",len);


                log.info("再次读取数据的位数:{}",byteBuf.readableBytes());

                if(len >= byteBuf.readableBytes()){
                    log.debug("数据长度不够，数据协议len长度为：{},数据包实际可读内容为：{} 正在等待处理拆包……",len,byteBuf.readableBytes());
                    byteBuf.resetReaderIndex();
                    /*
                     **结束解码，这种情况说明数据没有到齐，在父类ByteToMessageDecoder的callDecode中会对out和in进行判断
                     * 如果in里面还有可读内容即in.isReadable为true,cumulation中的内容会进行保留，，直到下一次数据到来，将两帧的数据合并起来，再解码。
                     * 以此解决拆包问题
                     */
                    return;
                }
                byte [] data=new byte[len];
                byteBuf.readBytes(data);//读取核心的数据

                ByteBuf tail=byteBuf.readBytes(2);

                if (Arrays.equals(tail.array(), PROTOCOL_TAIL)){
                    log.debug("数据解码成功");
                    list.add(data);
                    //如果list有值，且in仍然可读，将继续调用decode方法再次解码in中的内容，以此解决粘包问题
                }else {
                    log.error("数据解码协议结束标志位错误");
                }
            }
        }else{
            log.error("不符合最小长度");
        }
    }
}
