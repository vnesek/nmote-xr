/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import java.net.URI;

import com.nmote.xr.Endpoint;
import com.nmote.xr.MethodCall;
import com.nmote.xr.MethodResponse;
import com.nmote.xr.XR;

public class Betty1 {

	public static void main(String[] args) throws Exception {
		Endpoint server = XR.client(new URI("http://betty.userland.com/RPC2"));
		MethodCall call = new MethodCall("examples.getStateName", 3);
		MethodResponse response = server.call(call);
		System.out.println(call + " => " + response);
	}
}
