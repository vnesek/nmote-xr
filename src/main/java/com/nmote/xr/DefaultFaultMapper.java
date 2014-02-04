/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DefaultFaultMapper implements FaultMapper {

	private static FaultMapper INSTANCE = new DefaultFaultMapper();

	public Fault toFault(Throwable t) {
		return new Fault(Fault.CUSTOM_FAULT, format(t));
	}

	protected String format(Throwable t) {
		StringWriter result = new StringWriter();
		PrintWriter w = new PrintWriter(result);
		w.append(t.toString());
		w.append(":\n");
		t.printStackTrace(w);
		w.close();
		return result.toString();
	}

	public static FaultMapper getInstance() {
		return INSTANCE;
	}

	public static void setInstance(FaultMapper instance) {
		if (instance == null) { throw new NullPointerException("instance == null"); }
		DefaultFaultMapper.INSTANCE = instance;
	}
}
