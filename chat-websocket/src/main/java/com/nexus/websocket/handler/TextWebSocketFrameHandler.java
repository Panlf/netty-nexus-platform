package com.nexus.websocket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 当有客户端连接时，handlerAdded会自动调用，那么就该将连上的客户端记录下来
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();

		for (Channel channel : channels) {
			if (channel != incoming) {
				channel.writeAndFlush(new TextWebSocketFrame("[欢迎:]" + incoming.remoteAddress() + "进入聊天室"));
			}
		}
		channels.add(incoming);
	}

	// 退出
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();

		for (Channel channel : channels) {
			if (channel != incoming) {
				channel.writeAndFlush(new TextWebSocketFrame("[再见:]" + incoming.remoteAddress() + "离开聊天室"));
			}
		}
		channels.remove(incoming);
	}

	// 广播
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		String content = msg.text();
		Channel ichannel = ctx.channel();

		for (Channel channel : channels) {
			if (channel != ichannel) {
				channel.writeAndFlush(new TextWebSocketFrame("[用户" + ichannel.remoteAddress() + "说:]" + content));
			} else {
				channel.writeAndFlush(new TextWebSocketFrame("[我自己说:]" + content));
			}
		}
	}

}
