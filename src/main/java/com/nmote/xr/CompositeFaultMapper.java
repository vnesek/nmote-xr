/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.util.Arrays;
import java.util.Collection;

public class CompositeFaultMapper implements FaultMapper {

	public CompositeFaultMapper(Collection<FaultMapper> faultMappers) {
		assert faultMappers != null;
		this.faultMappers = faultMappers;
	}
	
	public CompositeFaultMapper(FaultMapper... faultMappers) {
		this(Arrays.asList(faultMappers));
	}

	public Fault toFault(Throwable t) {
		Fault result = null;
		for (FaultMapper faultMapper : faultMappers) {
			result = faultMapper.toFault(t);
			if (result != null) break;
		}
		return result;
	}

	private final Collection<FaultMapper> faultMappers;
}