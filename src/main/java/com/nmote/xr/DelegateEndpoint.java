/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

/**
 * DelegateEndpoint delegates method calls to the target Endpoint.
 */
public class DelegateEndpoint implements Endpoint {

	/**
	 * Creates delegate endpoint delegating XML-RPC calls to target
	 *
	 * @param target
	 *            Endpoint to delegate calls to
	 * @throws NullPointerException
	 *             if target is null
	 */
	public DelegateEndpoint(Endpoint target) {
		if (target == null) {
			throw new NullPointerException("target is null");
		}
		this.target = target;
	}

	public MethodResponse call(MethodCall call) {
		return getTarget().call(call);
	}

	public Endpoint getTarget() {
		return target;
	}

	private final Endpoint target;
}