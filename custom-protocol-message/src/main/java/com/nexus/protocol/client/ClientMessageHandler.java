package com.nexus.protocol.client;


import com.nexus.protocol.server.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端业务处理器：接收服务端响应并打印
 *
 * @author panlf
 * @date 2026/6/3
 */
@Slf4j
public class ClientMessageHandler extends SimpleChannelInboundHandler<RequestMessage> {

    public ClientMessageHandler(String deviceCode) {
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        log.info("收到服务端响应: type={}, workTimestamp={}, content={}",
                msg.getRequestType(), msg.getWorkTimestamp(), msg.getContent());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端异常: ", cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("与服务端连接断开，尝试重连...");
        // 实际生产环境可在此加入重连逻辑
        super.channelInactive(ctx);
    }
}