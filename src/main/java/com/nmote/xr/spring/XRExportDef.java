/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.spring;

import java.io.Serializable;

import com.nmote.xr.About;
import com.nmote.xr.FaultMapper;

public class XRExportDef<T> implements Serializable {

	private static final long serialVersionUID = About.serialVersionUID;

	public Class<?>[] getAdditionalExport() {
		return additionalExport;
	}

	public Class<T> getExport() {
		return export;
	}

	public FaultMapper getFaultMapper() {
		return faultMapper;
	}

	public String getPrefix() {
		return prefix;
	}

	public T getServer() {
		return server;
	}

	public void setAdditionalExport(Class<?>[] additionalExport) {
		this.additionalExport = additionalExport;
	}

	public void setExport(Class<T> export) {
		this.export = export;
	}

	public void setFaultMapper(FaultMapper faultMapper) {
		this.faultMapper = faultMapper;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setServer(T server) {
		this.server = server;
	}

	private Class<?>[] additionalExport;
	private Class<T> export;
	private FaultMapper faultMapper;
	private String prefix;
	private T server;
}