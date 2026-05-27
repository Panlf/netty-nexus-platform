package com.nexus.websocket.handler;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedNioFile;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final String chatUri;

	private static File indexFile;

	public HttpRequestHandler(String chatUri) {
		this.chatUri = chatUri;
	}

	static {
		try {
			URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
			System.out.println(location.toURI().getPath());
			String path = location.toURI().getPath() + "index.html";
			System.out.println("path:" + path);
			indexFile = new File(path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		System.out.println(request.uri());

		if (chatUri.equalsIgnoreCase(request.uri())) {
			System.out.println("请求是WebSocket的请求");
			ctx.fireChannelRead(request.retain());
		} else {
			System.out.println("请求是http请求，则需要读取index.html页面并发送给客户端浏览器");
			if (HttpHeaderUtil.is100ContinueExpected(request)) {
				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
						HttpResponseStatus.CONTINUE);
				ctx.writeAndFlush(response);
			}

			// 读取默认的index.html页面
			RandomAccessFile file = new RandomAccessFile(indexFile, "r");
			// 设置Http协议的响应头
			HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

			boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);

			if (keepAlive) {
				response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, (int) file.length());
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}

			ctx.write(response);
			// 将html文件写到客户端
			ctx.write(new ChunkedNioFile(file.getChannel()));
			ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

			if (!keepAlive) {
				future.addListener(ChannelFutureListener.CLOSE);
			}

			file.close();
		}
	}
}
