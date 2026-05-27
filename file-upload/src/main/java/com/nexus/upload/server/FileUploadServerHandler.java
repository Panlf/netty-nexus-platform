package com.nexus.upload.server;

import com.nexus.upload.bean.UploadFile;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author panlf
 * @date 2021/10/3
 */
public class FileUploadServerHandler extends ChannelHandlerAdapter {
    private int start = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof UploadFile){
            UploadFile uploadFile = (UploadFile) msg;
            byte[] bytes = uploadFile.getBytes();
            int byteRead = uploadFile.getEndPosition();
            String fileMd5 = uploadFile.getFileMd5();
            String fileDir = "C:\\Users\\Breeze\\Desktop\\Temp";
            String path = fileDir + File.separator + fileMd5;
            File file = new File(path);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            start = start+ byteRead;
            if(byteRead > 0){
                ctx.writeAndFlush(start);
            }else{
                randomAccessFile.close();
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
