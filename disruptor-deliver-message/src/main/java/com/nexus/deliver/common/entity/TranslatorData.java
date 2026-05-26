package com.nexus.deliver.common.entity;

import java.io.Serializable;

public class TranslatorData implements Serializable{

    private static final long serialVersionUID = -5657467357642210238L;

    private String id;
	private String name;
	/**
	 * 传输消息体内容
	 */
	private String message;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
