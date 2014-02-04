/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.Serializable;

public class MethodResponse implements Serializable {

	private static final long serialVersionUID = About.serialVersionUID;

	/**
	 * @param value
	 */
	public MethodResponse(Object value) {
		super();
		this.value = value != null? value : "ok";
	}

	/**
	 * @return Returns the value.
	 */
	public Object getValue() {
		return value;
	}

	public boolean isFault() {
		return value instanceof Fault;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(getValue());
	}

	private Object value;
}