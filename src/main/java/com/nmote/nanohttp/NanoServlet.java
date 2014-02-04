/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.nanohttp;

import java.io.IOException;

public interface NanoServlet {

	boolean canProcess(NanoRequest nanoRequest);
	
	void process(NanoRequest nanoRequest) throws IOException;
}
