/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.Serializable;

/**
 * XML-RPC method response representation. Responses are created as a result of
 * calls made on {@link Endpoint}.
 */
public class MethodResponse implements Serializable {

	private static final long serialVersionUID = About.serialVersionUID;

	/**
	 * Creates a new method response instance holding value.
	 *
	 * @param value
	 *            response value. If null it will be converted to string "ok" as
	 *            XML-RPC doesn't support null values.
	 */
	public MethodResponse(Object value) {
		super();
		setValue(value);
	}

	/**
	 * Returns method response values.
	 *
	 * @return returns the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns true if response is XML-RPC fault.
	 *
	 * @return true if response is XML-RPC fault, false otherwise
	 */
	public boolean isFault() {
		return value instanceof Fault;
	}

	/**
	 * Sets a method response value
	 *
	 * @param value
	 *            response value. If null it will be converted to string "ok" as
	 *            XML-RPC doesn't support null values.
	 */
	public void setValue(Object value) {
		this.value = value != null ? value : "ok";
	}

	@Override
	public String toString() {
		return String.valueOf(getValue());
	}

	private Object value;
}