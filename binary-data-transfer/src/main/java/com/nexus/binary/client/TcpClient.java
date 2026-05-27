package com.nexus.binary.client;

import com.nexus.binary.handler.EncoderHandler;
import com.nexus.binary.protocol.DataProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author panlf
 * @date 2023/5/17
 */
public class TcpClient {
    public void init(String host,Integer port) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("logging", new LoggingHandler("DEBUG"));
                            ch.pipeline().addLast(new EncoderHandler());
                            ch.pipeline().addLast(new EchoHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();

            DataProtocol dataProtocol = new DataProtocol();
            dataProtocol.setHead(new byte[]{(byte) 0xef, (byte) 0xef});
            dataProtocol.setData(new byte[]{(byte) 0x00, (byte) 0x02,(byte) 0x03, (byte) 0x04});
            dataProtocol.setTail(new byte[]{(byte) 0xfd, (byte) 0xfd});
            channel.writeAndFlush(dataProtocol);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new TcpClient().init("localhost",8888);
    }
}
