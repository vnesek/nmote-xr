/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

final class ISO8601 {
	
	private static final SimpleDateFormat[] FORMATS = new SimpleDateFormat[] {
		new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss"),
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
	};

	private ISO8601() {}
	
	public static synchronized Date decode(String s) {
		s = s.trim();
		for (int i = 0; i < FORMATS.length; ++i) {
			try {
				return FORMATS[i].parse(s);
			} catch (ParseException e) {
				// Ignored
			}
		}
		throw new RuntimeException("invalid date format: " + s);
	}
	
	public static synchronized String encode(Date date) {
		return FORMATS[0].format(date);
	}
}
