package com.nexus.rpc.server;

public class NettyRpcServerBootstrap {
	public static void main(String[] args) {
		try {
			new NettyRpcServer().start(8765);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
