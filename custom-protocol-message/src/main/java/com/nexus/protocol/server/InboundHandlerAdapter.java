package com.nexus.protocol.server;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static com.nexus.protocol.server.CacheChannelHandlerMap.*;

/**
 * 业务消息处理器
 * 负责设备注册、设备解绑、业务消息处理
 *
 * @author panlf
 * @date 2026/6/3
 */
@Slf4j
@ChannelHandler.Sharable
public class InboundHandlerAdapter extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = remote.getAddress().getHostAddress();
        int clientPort = remote.getPort();

        // 注册通道（此时 deviceCode 未知，先缓存 ctx）
        put(channelId, ctx, null);
        log.info("新连接加入: channelId={}, client={}:{}, 当前在线数={}",
                channelId, clientIp, clientPort, size());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        remove(channelId);
        InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("连接断开: channelId={}, client={}:{}, 剩余在线数={}",
                channelId, remote.getAddress().getHostAddress(), remote.getPort(), size());
        ctx.close();
        super.channelInactive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        log.info("收到业务消息: type={}, deviceCode={}", msg.getRequestType(), msg.getDeviceCode());

        // 更新通道与设备编码的绑定
        if (msg.getDeviceCode() != null && !msg.getDeviceCode().isEmpty()) {
            put(channelId, ctx, msg.getDeviceCode());
        }

        // 业务逻辑处理（例如根据 requestType 分发）
        processBusiness(msg);

        // 响应客户端（可根据需要选择是否回复）
        ctx.writeAndFlush(msg);
    }

    private void processBusiness(RequestMessage msg) {
        // 示例：填充服务端处理时间戳
        msg.setWorkTimestamp(System.currentTimeMillis());
        // 这里可以根据 msg.getRequestType() 调用不同的业务 Service
        log.info("业务处理完成: deviceCode={}, content={}", msg.getDeviceCode(), msg.getContent());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("通道异常，关闭连接: {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
