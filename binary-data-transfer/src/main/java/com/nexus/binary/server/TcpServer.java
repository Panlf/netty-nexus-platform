package com.nexus.binary.server;

import com.nexus.binary.handler.BusinessHandler;
import com.nexus.binary.handler.DecoderHandler;
import com.nexus.binary.handler.EncoderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author panlf
 * @date 2023/5/17
 */
@Slf4j
public class TcpServer {
    public void init(int port){
        log.info("正在启动TCP服务器...");
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();//主线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();//工作线程组

        try {
            ServerBootstrap bootstrap   = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)  //配置工作线程组
                    .channel(NioServerSocketChannel.class) //配置为NIO的socket通道
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast("logging",new LoggingHandler("DEBUG"));//设置log监听器，并且日志级别为debug，方便观察运行流程
                            ch.pipeline().addLast("encode",new EncoderHandler());//编码器。发送消息时候用过
                            ch.pipeline().addLast("decode",new DecoderHandler());//解码器，接收消息时候用
                            ch.pipeline().addLast("handler",new BusinessHandler());//业务处理类，最终的消息会在这个handler中进行业务处理
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync(); //使用了Future来启动线程，并绑定了端口

            log.info("启动TCP服务器启动成功，正在监听端口 : {}",port);

            future.channel().closeFuture().sync(); //以异步的方式关闭端口
        } catch (Exception e){
            log.error("启动出现异常 : {}",e.getMessage());
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new TcpServer().init(8888);
    }
}
