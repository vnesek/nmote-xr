/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;

/**
 * Utility class that logs hex and plain ASCII dumps of data
 * read from InputStream to a log.
 * 
 * @author Vjekoslav Nesek vnesek@nmote.com
 */
public class LoggingInputStream extends FilterInputStream {

	public LoggingInputStream(InputStream in, Logger log) {
		this(in, log, 16);
	}

	public LoggingInputStream(InputStream in, Logger log, int size) {
		this(in, log, size, true);
	}

	public LoggingInputStream(InputStream in, Logger log, int size, boolean dumpPlainText) {
		super(in);
		support = new LoggingStreamSupport(log, PREFIX, size, dumpPlainText);
	}
	
	/**
	 * @see java.io.InputStream#read()
	 */
	public synchronized int read() throws IOException {
		int b = super.read();
		if (b != -1) {
			support.logByte(b);
		} else {
			closeSupport();
		}
		
		return b;
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
	
	private final static String PREFIX = "<< ";
	private LoggingStreamSupport support;
}

