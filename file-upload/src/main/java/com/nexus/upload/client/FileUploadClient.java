package com.nexus.upload.client;

import com.nexus.upload.bean.UploadFile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;

/**
 * @author panlf
 * @date 2021/10/3
 */
public class FileUploadClient {
    public void connect(int port, String host, final UploadFile uploadFile) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                    ch.pipeline().addLast(new FileUploadClientHandler(uploadFile));
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        try {
            UploadFile uploadFile = new UploadFile();
            File file = new File("C:\\Users\\Breeze\\Downloads\\Windows Soft\\7z2600-x64.exe");
            String fileMd5 = file.getName();// 文件名
            uploadFile.setFile(file);
            uploadFile.setFileMd5(fileMd5);
            uploadFile.setStartPosition(0);// 文件开始位置
            new FileUploadClient().connect(port, "127.0.0.1", uploadFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
