/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import java.net.URI;

import com.nmote.xr.Meta;
import com.nmote.xr.XR;

/**
 * Betty2 calls through dinamically generated type-safe proxy.
 */
public class Betty2 {

	public static void main(String[] args) throws Exception {
		Examples examples = XR.proxy(new URI("http://betty.userland.com/RPC2"), Examples.class);
		String result = examples.getStateName(41);
		System.out.println(result);

		Meta xrs = XR.proxy(new URI("http://betty.userland.com/RPC2"), Meta.class);
		System.out.println(xrs.listMethods());
	}
}
