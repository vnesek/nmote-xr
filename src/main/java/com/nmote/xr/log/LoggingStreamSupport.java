/*
 * Copyright (c) Nmote Ltd. 2015. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.log;

/**
 * Support class for formatting dumps of input and output streams. Builds a
 * plain text dumo representation and periodically sends it to a supplied log.
 */
class LoggingStreamSupport {

	public LoggingStreamSupport(LoggerAdapter logger) {
		this(logger, 40);
	}

	public LoggingStreamSupport(LoggerAdapter logger, int size) {
		if (logger == null) {
			throw new NullPointerException("log == null");
		}

		this.logger = logger;
		this.size = size;
		buffer = new StringBuffer(size + 2);
	}

	public void close() {
		logDumps(false);
	}

	public void flush() {
		logDumps(true);
	}

	public void logByte(int b) {
		++pos;

		// Append to buffer
		buffer.append((char) b);

		// Send a debug logging event
		if (pos == size) {
			logDumps(false);
		}
	}

	public void logBytes(byte[] b, int off, int len) {
		for (int i = 0; i < len; ++i) {
			logByte(b[i + off]);
		}
	}

	private void clear() {
		buffer.setLength(0);
		pos = 0;
	}

	private void logDumps(boolean flush) {
		logger.debug(buffer.toString());
		clear();
	}

	private final StringBuffer buffer;
	private final LoggerAdapter logger;
	private int pos;
	private final int size;
}
