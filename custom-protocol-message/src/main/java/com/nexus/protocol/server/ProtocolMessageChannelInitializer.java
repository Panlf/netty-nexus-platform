package com.nexus.protocol.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 通道初始化器：添加编解码器、超时处理器、业务处理器
 *
 * @author panlf
 * @date 2026/6/3
 */
@Slf4j
public class ProtocolMessageChannelInitializer extends ChannelInitializer<Channel> {
    private final InboundHandlerAdapter inboundHandler;
    private final HeartHandlerAdapter heartHandler;

    public ProtocolMessageChannelInitializer(InboundHandlerAdapter inboundHandler,
                                             HeartHandlerAdapter heartHandler) {
        this.inboundHandler = inboundHandler;
        this.heartHandler = heartHandler;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 编码器与解码器（顺序重要：入站时先解码，出站时先编码）
        ch.pipeline().addLast("encoder", new ProtocolMessageEncoder());
        ch.pipeline().addLast("decoder", new ProtocolMessageDecoder());

        // 读超时：300 秒未收到任何数据则触发 ReadTimeoutException
        ch.pipeline().addLast("readTimeout", new ReadTimeoutHandler(300, TimeUnit.SECONDS));

        // 心跳处理器（处理 HEART_BEAT 消息）
        ch.pipeline().addLast("heartHandler", heartHandler);

        // 业务处理器（处理其他业务消息）
        ch.pipeline().addLast("businessHandler", inboundHandler);

        log.info("通道初始化完成: {}", ch.remoteAddress());
    }
}
