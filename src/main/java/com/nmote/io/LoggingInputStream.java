/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class that logs hex and plain ASCII dumps of data read from
 * InputStream to a log.
 *
 * @author Vjekoslav Nesek vnesek@nmote.com
 */
public class LoggingInputStream extends FilterInputStream {

	private final static String PREFIX = "<< ";

	public LoggingInputStream(InputStream in, Appendable log) {
		this(in, log, 16);
	}

	public LoggingInputStream(InputStream in, Appendable log, int size) {
		this(in, log, size, true);
	}

	public LoggingInputStream(InputStream in, Appendable log, int size, boolean dumpPlainText) {
		super(in);
		support = new LoggingStreamSupport(log, PREFIX, size, dumpPlainText);
	}

	public synchronized void close() throws IOException {
		try {
			closeSupport();
		} finally {
			super.close();
		}
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	public synchronized int read() throws IOException {
		int r = super.read();
		if (r != -1) {
			support.logByte(r);
		} else {
			closeSupport();
		}

		return r;
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		int r = super.read(b, off, len);
		if (r != -1) {
			for (int i = 0; i < r; ++i) {
				support.logByte(b[off + i]);
			}
		} else {
			closeSupport();
		}

		return r;
	}

	private void closeSupport() throws IOException {
		if (support != null) {
			support.close();
			support = null;
		}
	}
	private LoggingStreamSupport support;
}
