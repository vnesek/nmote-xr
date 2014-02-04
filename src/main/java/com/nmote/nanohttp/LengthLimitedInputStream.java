/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.nanohttp;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * LengthLimitedInputStream can be used to limit a number of bytes that can be
 * read from wrapped stream. When LengthLimitedInputStream is closed it skips to
 * a limit.
 * 
 * @author Vjekoslav Nesek vnesek@nmote.com
 */
public class LengthLimitedInputStream extends FilterInputStream {

	/**
	 * @param in
	 */
	public LengthLimitedInputStream(InputStream in, int length) {
		super(in);
		if (length < 0) { throw new IllegalArgumentException("Length less than 0"); }
		this.bytesLeft = length;
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		int a = super.available();
		if (a > bytesLeft) {
			a = bytesLeft;
		}
		return a;
	}

	/**
	 * Skips to the limit if there are any bytes are left. Original stream
	 * remains open.
	 * 
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		while (bytesLeft > 0) {
			int skipped = (int) super.skip(bytesLeft);
			if (skipped > 0) {
				bytesLeft -= skipped;
			} else {
				break;
			}
		}
	}

	/**
	 * Returns a number of bytes left until a limit.
	 * 
	 * @return number of bytes left until a limit
	 */
	public int getBytesLeft() {
		return bytesLeft;
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int result;
		if (bytesLeft > 0) {
			result = super.read();
			--bytesLeft;
		} else {
			result = -1;
		}
		return result;
	}

	/**
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		int bl = bytesLeft;
		int r;
		if (bl > 0) {
			if (len > bl) {
				len = bl;
			}
			r = super.read(b, off, len);
			if (r != -1) {
				bytesLeft -= r;
			} else {
				bytesLeft = 0;
			}
		} else {
			r = -1;
		}
		return r;
	}

	/**
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		if (n > bytesLeft) {
			n = bytesLeft;
		}
		long skipped = super.skip(n);
		if (skipped > 0) {
			bytesLeft -= (int) skipped;
		}
		return skipped;
	}

	private int bytesLeft;
}