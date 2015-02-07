/*
 * Copyright (c) Nmote Ltd. 2015. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.log;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class that logs plain ascii dumps of data read from InputStream to a
 * {@link LoggerAdapter}.
 */
public class LoggingInputStream extends FilterInputStream {

	public LoggingInputStream(InputStream in, LoggerAdapter logger) {
		this(in, logger, 120);
	}

	public LoggingInputStream(InputStream in, LoggerAdapter logger, int size) {
		super(in);
		support = new LoggingStreamSupport(logger, size);
	}

	public synchronized void close() throws IOException {
		closeSupport();
		super.close();
	}

	public synchronized int read() throws IOException {
		int b = super.read();
		if (b != -1) {
			support.logByte(b);
		} else {
			closeSupport();
		}

		return b;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int r = super.read(b, off, len);
		if (r != -1) {
			support.logBytes(b, off, r);
		}
		return r;
	}

	private void closeSupport() {
		if (support != null) {
			support.close();
			support = null;
		}
	}

	private LoggingStreamSupport support;
}
