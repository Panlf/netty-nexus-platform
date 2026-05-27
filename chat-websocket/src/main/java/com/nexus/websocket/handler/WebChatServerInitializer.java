package com.nexus.websocket.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebChatServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		//将请求和应答消息编码或者解码为http协议
		pipeline.addLast(new HttpServerCodec())
				.addLast(new HttpObjectAggregator(64*1024))
				//负责向客户端发送html的页面文件
				.addLast(new ChunkedWriteHandler())
				.addLast(new HttpRequestHandler("/chat"))
				.addLast(new WebSocketServerProtocolHandler("/chat"))
				.addLast(new TextWebSocketFrameHandler());
	
	}
}
