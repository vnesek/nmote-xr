/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import com.nmote.xr.XRMethod;

public interface Sample1 {

	@XRMethod(value = "sample1.sum", help = "Sums two integer arguments")
	public int add(int a, int b);

	@XRMethod("sample1.helloWorld")
	public String helloWord();
}