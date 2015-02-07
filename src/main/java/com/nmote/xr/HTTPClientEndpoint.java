/*
 * Copyright (c) Nmote Ltd. 2003-2015. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import com.nmote.xr.log.LoggerAdapter;

public class HTTPClientEndpoint extends AbstractIOEndPoint {

	public static final String CONNECTION_KEY = "_connection";

	public HTTPClientEndpoint() {
		super();
	}

	public HTTPClientEndpoint(URI uri) {
		super();
		this.uri = uri;
	}

	@Override
	public MethodResponse call(MethodCall call) {
		final LoggerAdapter logger = (LoggerAdapter) call.getAttribute(LoggerAdapter.LOGGER_KEY);
		if (logger != null) {
			logger.debug("-- " + call.getMethodName() + " (" + uri + ") --");
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();

			// Open a connection to the host
			conn.setDoInput(true);

			// Post form data
			conn.setRequestMethod("POST"); //$NON-NLS-1$
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
			conn.setRequestProperty("Host", uri.getHost()); //$NON-NLS-1$
			call.setAttribute(CONNECTION_KEY, conn);

			return super.call(call);
		} catch (IOException ioe) {
			throw Fault.newSystemFault(1010, ioe);
		} finally {
			call.setAttribute(CONNECTION_KEY, null);
		}
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	private HttpURLConnection getConnection(MethodCall call) {
		return (HttpURLConnection) call.getAttribute(CONNECTION_KEY);
	}

	@Override
	protected InputStream createInputStream(MethodCall call) throws IOException {
		return getConnection(call).getInputStream();
	}

	@Override
	protected OutputStream createOutputStream(MethodCall call) throws IOException {
		return getConnection(call).getOutputStream();
	}

	private URI uri;
}