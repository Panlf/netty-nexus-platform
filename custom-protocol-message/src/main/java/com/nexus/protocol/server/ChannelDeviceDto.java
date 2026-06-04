package com.nexus.protocol.server;


import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 *
 * @author panlf
 * @date 2026/6/3
 */
@Data
public class ChannelDeviceDto {

    private ChannelHandlerContext channelHandlerContext;
    private String deviceCode;
}
