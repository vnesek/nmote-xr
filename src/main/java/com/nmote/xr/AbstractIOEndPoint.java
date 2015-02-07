/*
 * Copyright (c) Nmote Ltd. 2003-2015. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.xml.sax.SAXException;

import com.nmote.xr.log.LoggerAdapter;
import com.nmote.xr.log.LoggingInputStream;
import com.nmote.xr.log.LoggingOutputStream;

// @PMD:REVIEWED:AtLeastOneConstructor: by vjeko on 2005.12.28 01:19
public abstract class AbstractIOEndPoint extends XmlRpcParseSupport implements Endpoint {

	public MethodResponse call(MethodCall call) {
		final LoggerAdapter logger = (LoggerAdapter) call.getAttribute(LoggerAdapter.LOGGER_KEY);
		try {
			OutputStream out = createOutputStream(call);
			if (logger != null) {
				out = new LoggingOutputStream(out, logger);
			}
			XmlRpcWriter xrw = new XmlRpcWriter(new OutputStreamWriter(out, "utf-8")); //$NON-NLS-1$
			try {
				xrw.writeMethodCall(call);
			} finally {
				xrw.close();
			}

			// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:19
			InputStream in = createInputStream(call);
			if (logger != null) {
				in = new LoggingInputStream(in, logger);
			}
			try {
				if (logger != null) {
					logger.debug("-- response --");
				}
				return handler.parseMethodResponse(in, xmlReader);
			} catch (SAXException e) {
				if (logger != null) {
					logger.debug("XML parse error: " + e);
				}
				throw Fault.newSystemFault(1014, e);
			} finally {
				handler.clear();
				// Apparently SAX parser will close input stream so making
				// following redundant:
				// in.close();
			}
		} catch (IOException ioe) {
			if (logger != null) {
				logger.debug("IO error: " + ioe);
			}
			throw Fault.newSystemFault(1008, ioe);
		}
	}

	protected abstract InputStream createInputStream(MethodCall call) throws IOException;

	protected abstract OutputStream createOutputStream(MethodCall call) throws IOException;

}