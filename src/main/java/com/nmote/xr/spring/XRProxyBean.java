/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.spring;

import java.net.URI;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import com.nmote.xr.DefaultTypeConverter;
import com.nmote.xr.TypeConverter;
import com.nmote.xr.XR;

@SuppressWarnings({"unchecked", "rawtypes"})
public class XRProxyBean implements FactoryBean {

	public Class getInterfaceClass() {
		return interfaceClass;
	}

	public Object getObject() throws Exception {
		return XR.proxy(getUri(), getInterfaceClass(), typeConverter);
	}

	public Class getObjectType() {
		return interfaceClass;
	}

	public URI getUri() {
		return uri;
	}

	public boolean isSingleton() {
		return true;
	}

	public void setInterfaceClass(Class interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setTypeConverter(TypeConverter typeConverter) {
		Assert.notNull(typeConverter);
		this.typeConverter = typeConverter;
	}

	private Class interfaceClass;
	private URI uri;
	private TypeConverter typeConverter = DefaultTypeConverter.getInstance();
}