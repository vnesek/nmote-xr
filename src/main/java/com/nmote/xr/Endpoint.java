/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

/**
 * Functional interface for making XML-RPC calls. Use {@link EndpointBuilder} to
 * create client or server Endpoints.
 */
public interface Endpoint {

	MethodResponse call(MethodCall call);
}
