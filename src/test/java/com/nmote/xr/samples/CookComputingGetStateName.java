/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import com.nmote.xr.EndpointBuilder;

public class CookComputingGetStateName {

	public static void main(String[] args) throws Exception {
		Math m = new EndpointBuilder().client("http://www.cookcomputing.com/xmlrpcsamples/math.rem", Math.class);
		System.out.println(m.add(2, 3));
	}
}
