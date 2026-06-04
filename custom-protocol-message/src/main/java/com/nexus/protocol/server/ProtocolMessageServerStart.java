package com.nexus.protocol.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端启动类（基于 Netty 5.0.0.Alpha2）
 *
 * @author panlf
 * @date 2026/6/3
 */
@Slf4j
public class ProtocolMessageServerStart {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void start(int port) throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ProtocolMessageChannelInitializer(
                            new InboundHandlerAdapter(),
                            new HeartHandlerAdapter()));

            ChannelFuture f = b.bind(port).sync();
            log.info("Netty 服务端启动成功，监听端口: {}", port);
            f.channel().closeFuture().sync();
        } finally {
            stop();
        }
    }

    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("服务端已停止");
    }

    public static void main(String[] args) throws InterruptedException {
        new ProtocolMessageServerStart().start(8080);
    }
}
