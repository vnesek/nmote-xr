/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.nanohttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface NanoRequest {

	InputStream getInputStream() throws IOException;
	
	OutputStream getOutputStream() throws IOException;
	
	Map<String, String> getRequestHeaders();
	
	Map<String, String> getResponseHeaders();
	
	void response(String response);
	
	String getRequestPath();
	
	String getMethod();
}
