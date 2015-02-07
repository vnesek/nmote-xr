/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.Serializable;

public final class About implements Serializable {

	public static final long serialVersionUID = 220L;

	public static final String COPYRIGHT = "Copyright (c) 2005-2015, Nmote ltd., All rights reserved";
	public static final String NAME = "nmote-xr";
	public static final String VERSION = "2.2.0";

	private About() {}

	public static void main(String[] args) {
		System.out.println(NAME + "/" + VERSION);
		System.out.println(COPYRIGHT);
	}
}
