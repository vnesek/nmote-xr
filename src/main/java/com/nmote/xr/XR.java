/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.net.URI;

/**
 * Helper class used to minimize effort needed to implement a XmlRpc server or
 * client.
 */
public final class XR {

	public static HTTPClientEndpoint client(URI uri) {
		return new HTTPClientEndpoint(uri);
	}

	public static <T> T proxy(Endpoint endPoint, Class<T> clazz, Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(endPoint, clazz, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(Endpoint endPoint, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(endPoint, clazz, typeConverter, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(Endpoint endPoint, ClassLoader classLoader, Class<T> clazz,
			Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(endPoint, classLoader, clazz, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(Endpoint endPoint, ClassLoader classLoader, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(endPoint, classLoader, clazz, typeConverter, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(URI uri, Class<T> clazz, Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(client(uri), clazz, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(URI uri, Class<T> clazz, TypeConverter typeConverter, Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(client(uri), clazz, typeConverter, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(URI uri, ClassLoader classLoader, Class<T> clazz, Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(client(uri), classLoader, clazz, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(URI uri, ClassLoader classLoader, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(client(uri), classLoader, clazz, typeConverter, additionalInterfaces).newProxy();
	}

	public static <T> HTTPServerEndpoint server(Class<T> clazz, Class<?>... additionalInterfaces) {
		return server(null, clazz, additionalInterfaces);
	}

	public static HTTPServerEndpoint server(Endpoint endPoint) {
		return new HTTPServerEndpoint(endPoint);
	}

	public static <T> HTTPServerEndpoint server(T server, Class<T> clazz, Class<?>... additionalInterfaces) {
		ObjectEndpoint endpoint = new ObjectEndpoint();
		endpoint.export(server, clazz);
		for (Class<?> i : additionalInterfaces) {
			endpoint.export(server, i);
		}
		endpoint.exportMeta();
		return server(new ObjectEndpoint().export(server, clazz).exportMeta());
	}

	private XR() {
	}
}