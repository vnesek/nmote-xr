/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class that logs hex and plain ASCII dumps of data written to
 * OutputStream to a JakartaCommons log.
 *
 * @author Vjekoslav Nesek vnesek@nmote.com
 */
public class LoggingOutputStream extends FilterOutputStream {

	public LoggingOutputStream(OutputStream in, Appendable log) {
		this(in, log, 16);
	}

	public LoggingOutputStream(OutputStream in, Appendable log, int size) {
		this(in, log, size, true);
	}

	public LoggingOutputStream(OutputStream in, Appendable log, int size, boolean dumpPlainText) {
		super(in);
		support = new LoggingStreamSupport(log, PREFIX, size, dumpPlainText);
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		support.logByte(b);
		super.write(b);
	}

	private void closeSupport() throws IOException {
		if (support != null) {
			support.close();
			support = null;
		}
	}

	public synchronized void close() throws IOException {
		try {
			closeSupport();
		} finally {
			super.close();
		}
	}

	public synchronized void flush() throws IOException {
		support.flush();
		super.flush();
	}

	private final static String PREFIX = ">> ";
	private LoggingStreamSupport support;
}
