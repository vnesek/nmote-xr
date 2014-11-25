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

/**
 * XML-RPC method call representation. Used as an argument in a call made to the
 * {@link Endpoint}. Method call can be directly instantiated or made through a
 * dynamic proxy {@link FacadeEndpoint}.
 */
public class MethodCall implements Serializable {

	private static final long serialVersionUID = About.serialVersionUID;

	/**
	 * Creates MethodCall with no arguments.
	 *
	 * @param methodName
	 *            the method name
	 * @throws NullPointerException
	 *             if methodName is null
	 */
	public MethodCall(String methodName) {
		super();
		setMethodName(methodName);
		setParams(new ArrayList<Object>());
	}

	/**
	 * Creates MethodCall with passed argument list.
	 *
	 * @param methodName
	 *            the method name
	 * @param params
	 *            method call parameters
	 * @throws NullPointerException
	 *             if methodName is null
	 */
	public MethodCall(String methodName, Object... params) {
		super();
		setMethodName(methodName);
		setParams(Arrays.asList(params));
	}

	/**
	 * Gets an attribute attached to a call. Attributes are arbitrary contextual
	 * objects attached to a call.
	 *
	 * @param key
	 *            attribute key/name
	 * @return attached attribute or null if there is none
	 */
	public Object getAttribute(String key) {
		Object result;
		if (attributes == null) {
			result = null;
		} else {
			result = attributes.get(key);
		}
		return result;
	}

	/**
	 * Returns the method name.
	 *
	 * @return the method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Return a list of all method parameters. Can't be null.
	 *
	 * @return method parameters list
	 */
	public List<Object> getParams() {
		return params;
	}

	/**
	 * Attaches arbitrary object to a call. Used for example to trace timings,
	 * access rights etc.
	 *
	 * @param key
	 *            attribute key/name
	 * @param value
	 *            attribute value
	 */
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

	/**
	 * Sets XML-RPC method name.
	 *
	 * @param methodName
	 *            the method name
	 * @throws NullPointerException
	 *             if method name is null
	 */
	public void setMethodName(String methodName) {
		if (methodName == null) {
			throw new NullPointerException("methodName == null");
		}
		this.methodName = methodName;
	}

	/**
	 * Sets XML-RPC method parameters.
	 *
	 * @param params
	 *            the method parameter list
	 * @throws NullPointerException
	 *             if params is null
	 */
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