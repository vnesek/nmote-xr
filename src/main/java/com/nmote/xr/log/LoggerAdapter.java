/*
 * Copyright (c) Nmote Ltd. 2015. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.log;

import com.nmote.xr.EndpointBuilder;

/**
 * Simple logger adapter so you can use any logging system you desire.
 */
public interface LoggerAdapter {

	/**
	 * Add your logger as a method call attribute under LOGGER_KEY. Or use
	 * {@link EndpointBuilder} debug() call.
	 */
	String LOGGER_KEY = "_logger";

	/**
	 * Log debug text message.
	 *
	 * @param text
	 *            to log
	 */
	void debug(String text);

	/**
	 * LoggerAdapter that logs to System.err (stderr) stream.
	 */
	LoggerAdapter SYSTEM_ERR = new LoggerAdapter() {

		public void debug(String text) {
			System.err.println(text);
		}
	};
}
