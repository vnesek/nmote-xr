/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

public interface FaultMapper {

	/**
	 * Converts <code>t</code> to XMLRPC Fault object.
	 *
	 * @param t
	 *            Java exception to convert
	 * @return Fault or null if conversion isn't possible
	 */
	Fault toFault(Throwable t);
}
