/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

// TODO GET method should return xhtml with a list of methods and method help
// @PMD:REVIEWED:AtLeastOneConstructor: by vjeko on 2005.12.28 01:18
public class XRServlet extends HttpServlet {

	public static final String ENDPOINT_KEY = "com.nmote.xr.Endpoint"; //$NON-NLS-1$

	private static final long serialVersionUID = About.serialVersionUID;
	private static final String TEXT_XML = "text/xml"; //$NON-NLS-1$

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		endpointKey = config.getInitParameter("endpointKey");
		if (endpointKey == null) {
			endpointKey = ENDPOINT_KEY;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		Endpoint endpoint = getEndpoint(request);

		MethodResponse result;
		if (TEXT_XML.equals(request.getContentType())) {
			try {
				XmlRpcParseSupport parseSupport = new XmlRpcParseSupport();
				// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:17
				InputStream in = request.getInputStream();
				MethodCall call = parseSupport.handler.parseMethodCall(in, parseSupport.xmlReader);
				result = endpoint.call(call);
			} catch (SAXException e) {
				result = new MethodResponse(Fault.newSystemFault(Fault.INVALID_XML_REQUEST, e));
			} catch (RuntimeException e) {
				result = new MethodResponse(Fault.newSystemFault(Fault.SERVER_ERROR, e));
			}
		} else {
			result = new MethodResponse(Fault.newSystemFault(1013, request.getContentType()));
		}

		response.setContentType(TEXT_XML);
		OutputStream out = response.getOutputStream();
		XmlRpcWriter xrw = new XmlRpcWriter(new OutputStreamWriter(out, "UTF-8")); //$NON-NLS-1$
		try {
			xrw.writeMethodResponse(result);
		} finally {
			xrw.close();
		}
		out.close();
	}

	protected Endpoint getEndpoint(HttpServletRequest request) throws ServletException {
		// Search request attribute
		Endpoint endpoint = (Endpoint) request.getAttribute(endpointKey);
		if (endpoint == null) {
			// Search servlet context attribute
			endpoint = (Endpoint) getServletContext().getAttribute(endpointKey);
			if (endpoint == null) {
				throw new ServletException("no Endpoint instance found named:" + endpointKey); //$NON-NLS-1$
			}
		}
		return endpoint;
	}

	protected String endpointKey;
}
