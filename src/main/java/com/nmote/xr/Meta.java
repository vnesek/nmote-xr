/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.util.List;

/**
 * Definition for system.* XML-RPC methods exposed by several XML-RPC servers.
 * system.* calls provide meta information on methods provided by a server.
 */
public interface Meta {

	/**
	 * Lists method names exposed by endpoint.
	 *
	 * @return method name list
	 */
	@XRMethod(value = "system.listMethods", help = "List all method names available")
	List<String> listMethods();

	/**
	 * Provides help/usage information for a method.
	 *
	 * @param methodName
	 *            name of the method
	 * @return help/usage information for a method
	 */
	@XRMethod(value = "system.help", help = "Returns usage information for a method")
	String help(String methodName);

	// @XRMethod("system.multicall")
	// List multicall(List calls);

	/**
	 * Checks if method is supported by endpoint or not.
	 *
	 * @param methodName
	 *            name of the method
	 * @return true if method is supported by endpoint, false otherwise
	 */
	@XRMethod(value = "system.supports", help = "Returns true if method is supported, false otherwise")
	boolean supports(String methodName);
}
