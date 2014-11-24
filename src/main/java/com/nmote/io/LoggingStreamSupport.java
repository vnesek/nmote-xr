/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.io;

import java.io.IOException;

/**
 * Support class for formatting hex dumps of input and output streams. Builds a
 * hex dump and plain text representation and periodically dumps it to a
 * supplied log.
 *
 * @author Vjekoslav Nesek vnesek@nmote.com
 */
class LoggingStreamSupport {

	public LoggingStreamSupport(Appendable log, String prefix) {
		this(log, prefix, 16);
	}

	public LoggingStreamSupport(Appendable log, String prefix, int size) {
		this(log, prefix, size, true);
	}

	public LoggingStreamSupport(Appendable log, String prefix, int size, boolean dumpPlainText) {
		if (log == null) {
			throw new NullPointerException("log is null");
		}

		this.log = log;
		this.size = size;
		this.prefix = prefix != null ? prefix : "";
		hexDump = new StringBuffer(size * 2 + 4);
		if (dumpPlainText) {
			plainTextDump = new StringBuffer(size + 2);
		} else {
			plainTextDump = null;
		}
	}

	private void clear() {
		hexDump.setLength(0);
		if (plainTextDump != null) {
			plainTextDump.setLength(0);
		}
		pos = 0;
	}

	public void logByte(int b) throws IOException {
		if (atGroupEdge()) {
			hexDump.append(' ');
			plainTextDump.append(' ');
		}

		++pos;

		// Append to plain text dump
		if (plainTextDump != null) {
			char c = (char) b;
			if (!Character.isLetterOrDigit(c)) {
				c = '.';
			}
			plainTextDump.append(c);
		}

		// Append to hex dump
		hexDump.append(Integer.toHexString((b >> 4) & 0xf));
		hexDump.append(Integer.toHexString(b & 0xf));

		// Send a debug logging event
		if (pos == size) {
			logDumps(false);
		}
	}

	private boolean atGroupEdge() {
		return pos > 0 && (pos % groupSize) == 0;
	}

	public void close() throws IOException {
		logDumps(false);
	}

	public void flush() throws IOException {
		logDumps(true);
	}

	private void logDumps(boolean flush) throws IOException {
			log.append(prefix);
			log.append(hexDump);
			if (plainTextDump != null) {
				log.append(" | ");
				log.append(plainTextDump);
			}
			if (flush) {
				log.append(" <flush>");
			}
			clear();
	}

	private final Appendable log;
	private final StringBuffer hexDump;
	private final StringBuffer plainTextDump;
	private final String prefix;
	private int pos;
	private final int size;
	private int groupSize = 4;
}
