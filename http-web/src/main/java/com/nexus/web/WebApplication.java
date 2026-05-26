package com.nexus.web;

/**
 * @author panlf
 * @date 2022/4/23
 */
public class WebApplication {
    public static void main(String[] args) throws Exception{
        HttpServer server = new HttpServer(8081);// 8081为启动端口
        server.start();
    }
}
