package com.nexus.rpc.common;

import java.io.Serializable;

public class Params implements Serializable{
    
    private static final long serialVersionUID = -5850218414678628426L;
    /*
	 * 全限定类名
	 */
	private String className;
	
	/**
	 * 调用的方法
	 */
	private String methodName;
	
	/**
	 * 方法参数类型列表
	 */
	private Class<?>[] parameterTypes;
	
	/**
	 * 参数列表
	 */
	private Object[] parameterValues;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Object[] parameterValues) {
		this.parameterValues = parameterValues;
	}
}
