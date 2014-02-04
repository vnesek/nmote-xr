/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.xml.sax.SAXException;

import com.nmote.nanohttp.LengthLimitedInputStream;
import com.nmote.nanohttp.NanoRequest;
import com.nmote.nanohttp.NanoServlet;

public class HTTPServerEndpoint implements Endpoint, NanoServlet {

	public HTTPServerEndpoint(Endpoint delegate) {
		super();
		setDelegate(delegate);
	}

	public boolean canProcess(NanoRequest nanoRequest) {
		return true;
	}

	public void process(NanoRequest nanoRequest) throws IOException {
		Map<String, String> requestHeaders = nanoRequest.getRequestHeaders();

		// Get content length;
		String contentLength = requestHeaders.get("content-length");

		InputStream in = nanoRequest.getInputStream();
		if (contentLength != null) {
			in = new LengthLimitedInputStream(in, Integer.parseInt(contentLength));
		}

		MethodResponse result;
		String contentType = requestHeaders.get("content-type");
		if ("text/xml".equals(contentType)) {
			result = readBodyAndMakeCall(in);
		} else {
			result = new MethodResponse(Fault.newSystemFault(1013, contentType));
		}

		// Format response
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XmlRpcWriter xrw = new XmlRpcWriter(new OutputStreamWriter(baos, "utf-8")); //$NON-NLS-1$
		xrw.writeMethodResponse(result);
		xrw.close();

		// Generate HTTP headers
		Map<String, String> responseHeaders = nanoRequest.getResponseHeaders();
		responseHeaders.put("content-type", "text/xml");
		responseHeaders.put("content-length", Integer.toString(baos.size()));
		responseHeaders.put("server", About.NAME + "/" + About.VERSION);
		nanoRequest.response("200 OK");
		
		// Dump XML
		baos.writeTo(nanoRequest.getOutputStream());
	}

	/**
	 * @param in
	 * @param result
	 * @return
	 * @throws IOException
	 */
	private MethodResponse readBodyAndMakeCall(InputStream in) throws IOException {
		MethodResponse result;
		try {
			XmlRpcParseSupport parseSupport = new XmlRpcParseSupport();
			// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:17
			MethodCall call = parseSupport.handler.parseMethodCall(in, parseSupport.xmlReader);
			result = call(call);
		} catch (SAXException e) {
			result = new MethodResponse(Fault.newSystemFault(1012, e));
		} catch (RuntimeException e) {
			result = new MethodResponse(Fault.newSystemFault(1011, e));
		}
		return result;
	}
	
	public MethodResponse call(MethodCall call) {
		return delegate.call(call);
	}

	public Endpoint getDelegate() {
		return delegate;
	}

	public final void setDelegate(Endpoint delegate) {
		if (delegate == null) { throw new NullPointerException("delegate == null"); }
		this.delegate = delegate;
	}

	private Endpoint delegate;
	
}