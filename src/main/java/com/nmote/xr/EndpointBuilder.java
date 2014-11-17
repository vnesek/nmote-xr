/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EndpointBuilder is an alternative to {@link XR} static methods. Supports
 * building of both client and server endpoints.
 *
 * @author vnesek
 */
public class EndpointBuilder {

	public EndpointBuilder() {
	}

	public EndpointBuilder export(Class<?>... additional) {
		this.additionalInterfaces.addAll(Arrays.asList(additional));
		return this;
	}

	public EndpointBuilder exportMeta() {
		this.additionalInterfaces.add(Meta.class);
		return this;
	}

	public EndpointBuilder using(TypeConverter typeConverter) {
		this.typeConverter = typeConverter;
		return this;
	}

	public EndpointBuilder using(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	public <T> T client(URI uri, Class<T> clazz) {
		if (classLoader == null) {
			classLoader = clazz.getClassLoader();
		}
		return new FacadeEndpoint<T>(new HTTPClientEndpoint(uri), //
				classLoader, clazz, typeConverter, //
				additionalInterfaces.toArray(new Class<?>[additionalInterfaces.size()])).newProxy();
	}

	public <T> T client(String uri, Class<T> clazz) throws URISyntaxException {
		return client(new URI(uri), clazz);
	}

	public <T> HTTPServerEndpoint server(T server, Class<T> clazz) {
		ObjectEndpoint endpoint = new ObjectEndpoint();
		endpoint.export(server, clazz);
		for (Class<?> i : additionalInterfaces) {
			endpoint.export(server, i);
		}
		endpoint.typeConverter(typeConverter);
		return new HTTPServerEndpoint(endpoint);
	}

	private List<Class<?>> additionalInterfaces = new ArrayList<Class<?>>();
	private ClassLoader classLoader;
	private TypeConverter typeConverter = DefaultTypeConverter.getInstance();
}
