/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import com.nmote.nanohttp.NanoServer;
import com.nmote.xr.XR;
import com.nmote.xr.XRMethod;

/**
 */
public class HelloWorldServer {

	@XRMethod(value = "example.helloWorld", help = "Returns 'Helo ' + argument")
	public static String hello(Object s) {
		return "Hello '" + s + "'";
	}

	public static void main(String[] args) throws Exception {
		NanoServer server = new NanoServer("http://localhost:7070");
		server.add(XR.server(HelloWorldServer.class));
		server.start();
	}
}
