/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.net.MalformedURLException;
import java.net.URI;

/**
 * Helper class used to minimize effort needed to implement a XmlRpc server or
 * client.
 */
public final class XR {

	private XR() {}

	public static HTTPClientEndpoint client(URI uri) throws MalformedURLException {
		return new HTTPClientEndpoint(uri);
	}

	public static <T> FacadeEndpoint<T> facade(Endpoint endPoint, Class<T> clazz, Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(endPoint, clazz, additionalInterfaces);
	}

	public static <T> FacadeEndpoint<T> facade(Endpoint endPoint, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(endPoint, clazz, typeConverter, additionalInterfaces);
	}

	public static <T> FacadeEndpoint<T> facade(Endpoint endPoint, ClassLoader classLoader, Class<T> clazz,
			Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(endPoint, classLoader, clazz, additionalInterfaces);
	}

	public static <T> FacadeEndpoint<T> facade(Endpoint endPoint, ClassLoader classLoader, Class<T> clazz,
			TypeConverter typeConverter, Class<?>... additionalInterfaces) {
		return new FacadeEndpoint<T>(endPoint, classLoader, clazz, typeConverter, additionalInterfaces);
	}

	public static <T> FacadeEndpoint<T> facade(URI uri, Class<T> clazz, Class<?>... additionalInterfaces)
			throws MalformedURLException {
		return new FacadeEndpoint<T>(client(uri), clazz, additionalInterfaces);
	}

	public static <T> FacadeEndpoint<T> facade(URI uri, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) throws MalformedURLException {
		return new FacadeEndpoint<T>(client(uri), clazz, typeConverter, additionalInterfaces);
	}

	public static <T> FacadeEndpoint<T> facade(URI uri, ClassLoader classLoader, Class<T> clazz,
			Class<?>... additionalInterfaces) throws MalformedURLException {
		return new FacadeEndpoint<T>(client(uri), classLoader, clazz, additionalInterfaces);
	}

	public static <T> FacadeEndpoint<T> facade(URI uri, ClassLoader classLoader, Class<T> clazz,
			TypeConverter typeConverter, Class<?>... additionalInterfaces) throws MalformedURLException {
		return new FacadeEndpoint<T>(client(uri), classLoader, clazz, typeConverter, additionalInterfaces);
	}

	public static <T> T proxy(Endpoint endPoint, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) {
		return facade(endPoint, clazz, typeConverter, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(Endpoint endPoint, ClassLoader classLoader, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) {
		return facade(endPoint, classLoader, clazz, typeConverter, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(URI uri, Class<T> clazz, TypeConverter typeConverter, Class<?>... additionalInterfaces)
			throws MalformedURLException {
		return facade(uri, clazz, typeConverter, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(URI uri, ClassLoader classLoader, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) throws MalformedURLException {
		return facade(uri, classLoader, clazz, typeConverter, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(Endpoint endPoint, Class<T> clazz, Class<?>... additionalInterfaces) {
		return facade(endPoint, clazz, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(Endpoint endPoint, ClassLoader classLoader, Class<T> clazz,
			Class<?>... additionalInterfaces) {
		return facade(endPoint, classLoader, clazz, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(URI uri, Class<T> clazz, Class<?>... additionalInterfaces) throws MalformedURLException {
		return facade(uri, clazz, additionalInterfaces).newProxy();
	}

	public static <T> T proxy(URI uri, ClassLoader classLoader, Class<T> clazz, Class<?>... additionalInterfaces)
			throws MalformedURLException {
		return facade(uri, classLoader, clazz, additionalInterfaces).newProxy();
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
}