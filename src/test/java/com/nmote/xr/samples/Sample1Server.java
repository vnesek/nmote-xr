/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

public class Sample1Server implements Sample1 {

	public int add(int a, int b) {
		// throw new Fault(191);
		return a + b;
	}

	public String helloWord() {
		return "Hello World from XR!";
	}
}
