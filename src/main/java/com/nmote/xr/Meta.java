/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.util.List;

public interface Meta {

	@XRMethod(value = "system.listMethods", help = "List all method names available")
	List<String> listMethods();

	@XRMethod(value = "system.help", help = "Returns usage information for a method")
	String help(String methodName);

	// @XRMethod("system.multicall")
	// List multicall(List calls);

	@XRMethod(value = "system.supports", help = "Returns true if method is supported, false otherwise")
	boolean supports(String methodName);
}
