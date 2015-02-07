/*
 * Copyright (c) Nmote Ltd. 2015. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.log;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class that logs plain ascii dumps of data written to OutputStream to
 * a {@link LoggerAdapter}
 */
public class LoggingOutputStream extends FilterOutputStream {

	public LoggingOutputStream(OutputStream out, LoggerAdapter logger) {
		this(out, logger, 120);
	}

	public LoggingOutputStream(OutputStream out, LoggerAdapter logger, int size) {
		super(out);
		support = new LoggingStreamSupport(logger, size);
	}

	public void write(int b) throws IOException {
		support.logByte(b);
		super.write(b);
	}

	private void closeSupport() {
		if (support != null) {
			support.close();
			support = null;
		}
	}


	public synchronized void close() throws IOException {
		closeSupport();
		super.close();
	}

	public synchronized void flush() throws IOException {
		if (support != null) {
			support.flush();
		}
		super.flush();
	}

	private LoggingStreamSupport support;
}