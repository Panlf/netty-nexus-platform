package com.nexus.upload.client;

import com.nexus.upload.bean.UploadFile;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.RandomAccessFile;

/**
 * @author panlf
 * @date 2021/10/3
 */
public class FileUploadClientHandler extends ChannelHandlerAdapter{
    private int byteRead;
    private volatile int start = 0;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;
    private UploadFile uploadFile;

    public FileUploadClientHandler(UploadFile uploadFile){
        if(uploadFile.getFile().exists()){
            if(!uploadFile.getFile().isFile()){
                System.out.println("Not a File : "+ uploadFile.getFile());
                return;
            }
        }
        this.uploadFile = uploadFile;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try{
            randomAccessFile = new RandomAccessFile(uploadFile.getFile(),"r");
            randomAccessFile.seek(uploadFile.getStartPosition());
            lastLength = Integer.parseInt(String.valueOf(randomAccessFile.length() / 10));
            byte[] bytes = new byte[lastLength];
            if((byteRead=randomAccessFile.read(bytes)) != 1){
                uploadFile.setEndPosition(byteRead);
                uploadFile.setBytes(bytes);
                ctx.writeAndFlush(uploadFile);
            }else{
                System.out.println("文件已经读完");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Integer){
            start = (int) msg;
            if(start != -1){
                randomAccessFile = new RandomAccessFile(uploadFile.getFile(),"r");
                randomAccessFile.seek(start);
                System.out.println("chunk file length : "+(randomAccessFile.length() / 10));
                System.out.println("length : "+(randomAccessFile.length() - start));
                int a = (int)(randomAccessFile.length()-start);
                int b = (int)(randomAccessFile.length() / 10);
                if(a<b){
                    lastLength = a;
                }
                byte[] bytes = new byte[lastLength];
                if((byteRead = randomAccessFile.read(bytes)) != -1 &&
                        (randomAccessFile.length()-start) > 0){
                    uploadFile.setEndPosition(byteRead);
                    uploadFile.setBytes(bytes);
                    ctx.writeAndFlush(uploadFile);
                }else {
                    ctx.writeAndFlush("");
                    randomAccessFile.close();
                    ctx.close();
                    System.out.println("文件已经读完 ===> " + byteRead);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
