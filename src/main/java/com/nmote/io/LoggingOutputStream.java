/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;

/**
 * Utility class that logs hex and plain ASCII dumps of data
 * written to OutputStream to a JakartaCommons log.
 * 
 * @author Vjekoslav Nesek vnesek@nmote.com
 */
public class LoggingOutputStream extends FilterOutputStream {

	public LoggingOutputStream(OutputStream in, Logger log) {
		this(in, log, 16);
	}

	public LoggingOutputStream(OutputStream in, Logger log, int size) {
		this(in, log, size, true);
	}

	public LoggingOutputStream(OutputStream in, Logger log, int size, boolean dumpPlainText) {
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
		support.flush();
		super.flush();
	}
	
	private final static String PREFIX = ">> ";
	private LoggingStreamSupport support;
}

