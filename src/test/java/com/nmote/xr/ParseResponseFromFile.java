/*
 * Copyright (c) Nmote Ltd. 2003-2015. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.InputStream;

import org.xml.sax.XMLReader;

public class ParseResponseFromFile {

	public static void main(String[] args) throws Exception {
		XmlRpcHandler xrh = new XmlRpcHandler();
		XMLReader reader = xrh.newXMLReader();
		InputStream in = ParseResponseFromFile.class.getResourceAsStream("response1.xml");
		MethodResponse response = xrh.parseMethodResponse(in, reader);
		System.out.println(" => " + response);
	}
}
