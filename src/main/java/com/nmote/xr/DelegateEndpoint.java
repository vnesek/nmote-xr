/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

public class DelegateEndpoint implements Endpoint {

	public DelegateEndpoint(Endpoint target) {
		if (target == null) throw new NullPointerException("target is null");
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