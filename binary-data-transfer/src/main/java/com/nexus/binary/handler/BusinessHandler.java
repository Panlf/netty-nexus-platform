package com.nexus.binary.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author panlf
 * @date 2023/5/17
 */
@Slf4j
public class BusinessHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof byte []){
           log.info("获取到的数据:{}",msg);
        }
    }
}
