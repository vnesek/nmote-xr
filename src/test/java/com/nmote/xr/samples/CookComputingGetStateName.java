/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import com.nmote.xr.EndpointBuilder;

public class CookComputingGetStateName {

	public static void main(String[] args) throws Exception {
		String url = "http://www.cookcomputing.com/xmlrpcsamples/math.rem";
		Math m = EndpointBuilder.client(url, Math.class).debug().get();
		System.out.println(m.add(2, 3));
	}
}
