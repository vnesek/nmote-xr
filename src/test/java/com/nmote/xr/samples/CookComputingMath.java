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

public class CookComputingMath {

	interface Math {
		
	}
	
	public static void main(String[] args) throws Exception {
		Endpoint client = XR.client(new URI("http://www.cookcomputing.com/xmlrpcsamples/RPC2.ashx"));
		MethodCall call = new MethodCall("examples.getStateName", 41);
		MethodResponse response = client.call(call);
		System.out.println(call + " => " + response);
	}
}
