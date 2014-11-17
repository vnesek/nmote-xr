/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import com.nmote.xr.XRMethod;

interface Math {

	@XRMethod("math.Add")
	public Number add(Number a, Number b);
}