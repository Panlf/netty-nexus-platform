package com.nexus.protocol.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳处理器
 * 处理心跳请求，一般是更新一下缓存中的设备最后活跃度
 *
 * @author panlf
 * @date 2026/6/3
 */
@Slf4j
@ChannelHandler.Sharable
public class HeartHandlerAdapter extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        if ("HEART_BEAT".equals(msg.getRequestType())) {
            // 填充通道ID和设备编码（从缓存获取）
            String channelId = ctx.channel().id().asLongText();
            msg.setChannelId(channelId);
            msg.setWorkTimestamp(System.currentTimeMillis());

            // 业务处理：例如记录心跳时间、更新缓存中的心跳标记
            doHeartBeatBusiness(msg);

            // 原样返回心跳响应
            ctx.writeAndFlush(msg);
            log.debug("心跳响应: channelId={}", channelId);
        } else {
            // 非心跳消息，传递给下一个 Handler
            ctx.fireChannelRead(msg);
        }
    }

    private void doHeartBeatBusiness(RequestMessage msg) {
        // 示例：可在此更新数据库或缓存中的设备最后在线时间
        log.info("收到设备[{}]心跳，时间={}", msg.getDeviceCode(), msg.getWorkTimestamp());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            log.warn("心跳超时，关闭通道: {}", ctx.channel().remoteAddress());
            ctx.close();
        } else {
            log.error("心跳处理器异常: ", cause);
            ctx.close();
        }
    }
}