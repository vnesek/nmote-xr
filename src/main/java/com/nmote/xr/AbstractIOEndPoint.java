/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.xml.sax.SAXException;

// @PMD:REVIEWED:AtLeastOneConstructor: by vjeko on 2005.12.28 01:19
public abstract class AbstractIOEndPoint extends XmlRpcParseSupport implements Endpoint {

	public MethodResponse call(MethodCall call) {
		try {
			OutputStream out = createOutputStream(call);
			XmlRpcWriter xrw = new XmlRpcWriter(new OutputStreamWriter(out, "utf-8")); //$NON-NLS-1$
			try {
				xrw.writeMethodCall(call);
			} finally {
				xrw.close();
			}

			// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:19
			InputStream in = createInputStream(call);
			try {
				return handler.parseMethodResponse(in, xmlReader);
			} catch (SAXException e) {
				throw Fault.newSystemFault(1014, e);
			} finally {
				handler.clear();
				// Apparently SAX parser will close input stream so making
				// following redundant:
				// in.close();
			}
		} catch (IOException ioe) {
			throw Fault.newSystemFault(1008, ioe);
		}
	}

	protected abstract InputStream createInputStream(MethodCall call) throws IOException;

	protected abstract OutputStream createOutputStream(MethodCall call) throws IOException;
}