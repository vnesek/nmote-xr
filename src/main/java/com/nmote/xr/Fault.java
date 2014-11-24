/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Fault extends RuntimeException {

	public static final int CUSTOM_FAULT = 1000;
	public static final int ILLEGAL_ACCESS = 1003;
	public static final int ILLEGAL_ARGUMENT = 1002;
	public static final int INSTANCE_METHOD = 1016;
	public static final int INVALID_XML_REQUEST = 1012;
	public static final int METHOD_NOT_SUPPORTED = 1004;
	public static final int SERVER_ERROR = 1011;
	public static final int SERVER_INITIALIZATION = 1015;
	public static final int INSTANTIATION = 1017;

	private static final String BUNDLE_NAME = "com.nmote.xr.faults"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	private static final long serialVersionUID = About.serialVersionUID;

	public Fault(int faultCode) {
		this(faultCode, "");
	}

	@Override
	public String getMessage() {
		return getFaultString();
	}

	public Fault(int faultCode, String faultString) {
		super();
		this.faultCode = faultCode;
		this.faultString = faultString;
	}

	Fault() {
		super();
	}

	public int getFaultCode() {
		return faultCode;
	}

	public String getFaultString() {
		return faultString;
	}

	public Map getValue() {
		Map value = new HashMap();
		value.put("faultCode", faultCode); //$NON-NLS-1$
		value.put("faultString", faultString); //$NON-NLS-1$
		return value;
	}

	@Override
	public String toString() {
		// @PMD:REVIEWED:ConfusingTernary: by vjeko on 2005.12.28 01:04
		return "Fault[" + faultCode + (faultString != null ? (", '" + faultString + "']") : "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	void setValue(Map<String, Object> value) {
		Integer faultCode = (Integer) value.get("faultCode"); //$NON-NLS-1$
		if (faultCode != null) {
			this.faultCode = faultCode;
		}
		this.faultString = (String) value.get("faultString"); //$NON-NLS-1$
	}

	private int faultCode;
	private String faultString;

	/**
	 * @param faultCode
	 * @param args
	 * @return
	 */
	private static String formatFaultString(int faultCode, Object... args) {
		String result;
		try {
			result = new MessageFormat(RESOURCE_BUNDLE.getString("Fault." + faultCode)).format(args);
		} catch (MissingResourceException e) {
			result = faultCode + ": " + Arrays.asList(args);
		}
		return result;
	}

	static Fault newSystemFault(int faultCode, Object... args) {
		return new Fault(faultCode, formatFaultString(faultCode, args));
	}
}