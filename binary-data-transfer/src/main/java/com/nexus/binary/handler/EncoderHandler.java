package com.nexus.binary.handler;

import com.nexus.binary.protocol.DataProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author panlf
 * @date 2023/5/17
 */
@Slf4j
public class EncoderHandler extends MessageToByteEncoder<DataProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          DataProtocol dataProtocol,
                          ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(dataProtocol.getHead());
        byteBuf.writeBytes(dataProtocol.getData());
        byteBuf.writeBytes(dataProtocol.getTail());
        log.debug("数据编码成功 : {}" ,byteBuf);
    }
}
