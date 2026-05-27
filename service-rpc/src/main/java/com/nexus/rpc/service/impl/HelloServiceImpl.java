package com.nexus.rpc.service.impl;

import com.nexus.rpc.service.HelloService;

public class HelloServiceImpl implements HelloService {

	@Override
	public String sayHello(String name) {
		return "Hi,"+name;
	}

}
