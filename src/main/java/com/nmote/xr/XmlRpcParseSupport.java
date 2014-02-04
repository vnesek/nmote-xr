/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import org.xml.sax.XMLReader;

class XmlRpcParseSupport {

	XmlRpcParseSupport() {
		super();
		handler = new XmlRpcHandler();
		try {
			xmlReader = handler.newXMLReader();
		} catch (Exception e) {
			throw Fault.newSystemFault(1011, e);
		}
	}

	protected XMLReader xmlReader;
	protected XmlRpcHandler handler;
}
