/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodCall implements Serializable {

	private static final long serialVersionUID = About.serialVersionUID;

	public MethodCall(String methodName) {
		super();
		this.methodName = methodName;
		this.params = new ArrayList<Object>();
	}

	public MethodCall(String methodName, Object... params) {
		super();
		this.methodName = methodName;
		this.params = Arrays.asList(params);
	}

	public Object getAttribute(String key) {
		Object result;
		if (attributes == null) {
			result = null;
		} else {
			result = attributes.get(key);
		}
		return result;
	}

	public String getMethodName() {
		return methodName;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setAttribute(String key, Object value) {
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}
		if (value == null) {
			attributes.remove(key);
		} else {
			attributes.put(key, value);
		}
	}

	public void setMethodName(String methodName) {
		if (methodName == null) {
			throw new NullPointerException("methodName == null");
		}
		this.methodName = methodName;
	}

	public void setParams(List<Object> params) {
		if (params == null) {
			throw new NullPointerException("params == null");
		}
		this.params = params;
	}

	@Override
	public String toString() {
		return String.valueOf(methodName) + params;
	}

	private Map<String, Object> attributes;
	private String methodName;
	private List<Object> params;
}