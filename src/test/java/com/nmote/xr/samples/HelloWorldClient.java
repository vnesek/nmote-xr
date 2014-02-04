/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import java.net.URI;

import com.nmote.xr.Endpoint;
import com.nmote.xr.Meta;
import com.nmote.xr.MethodCall;
import com.nmote.xr.MethodResponse;
import com.nmote.xr.XR;

public class HelloWorldClient {

	public static void main(String[] args) throws Exception {
		Endpoint client = XR.client(new URI("http://localhost:7070"));
		MethodCall call = new MethodCall("example.helloWorld", "Foo");
		MethodResponse response = client.call(call);
		System.out.println(response);

		Meta system = XR.proxy(client, Meta.class);
		System.out.println(system.listMethods());
		System.out.println(system.help("example.helloWorld"));
	}
}
