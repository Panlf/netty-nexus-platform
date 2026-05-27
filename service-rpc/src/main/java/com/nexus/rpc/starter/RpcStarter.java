package com.nexus.rpc.starter;


import com.nexus.rpc.client.NettyRpcClient;
import com.nexus.rpc.service.HelloService;

/**
 *
 * @author panlf
 * @date 2026/5/27
 */
public class RpcStarter {
    public static void main(String[] args) {
        HelloService helloService = NettyRpcClient.proxy(HelloService.class);
        System.out.println(helloService.sayHello("Lilith"));
    }
}
