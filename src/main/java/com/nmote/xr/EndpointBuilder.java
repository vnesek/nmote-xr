/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	public <T> T client(String uri, Class<T> clazz) throws URISyntaxException {
		return client(new URI(uri), clazz);
	}

	public <T> T client(URI uri, Class<T> clazz) {
		if (classLoader == null) {
			classLoader = clazz.getClassLoader();
		}

		Endpoint clientEndpoint;
		if (log != null) {
			clientEndpoint = new HTTPClientEndpoint(uri) {
				protected InputStream createInputStream(MethodCall call) throws IOException {
					log.append("\n--- method response ---\n");
					return new FilterInputStream(super.createInputStream(call)) {
						public int read() throws IOException {
							int r = super.read();
							if (r != -1) {
								log.append((char) r);
							}

							return r;
						}

						public int read(byte[] b, int off, int len) throws IOException {
							int r = super.read(b, off, len);
							if (r != -1) {
								log.append(new String(b, off, r, "UTF-8"));
							}
							return r;
						}

						public void close() throws IOException {
							log.append("\n--- end response ---\n");
							super.close();
						}
					};
				};

				protected OutputStream createOutputStream(MethodCall call) throws IOException {
					log.append("\n--- method call ---\n");
					return new FilterOutputStream(super.createOutputStream(call)) {
						public void write(int b) throws IOException {
							log.append((char) b);
							super.write(b);
						}

						public void close() throws IOException {
							log.append("\n--- end call ---\n");
							super.close();
						}
					};
				}
			};
		} else {
			clientEndpoint = new HTTPClientEndpoint(uri);
		}

		return new FacadeEndpoint<T>(clientEndpoint, //
				classLoader, clazz, typeConverter, //
				additionalInterfaces.toArray(new Class<?>[additionalInterfaces.size()])).newProxy();
	}

	/**
	 * Dump XML-RPC calls to System.err
	 */
	public EndpointBuilder debug() {
		return debug(System.err);
	}

	/**
	 * Dump XML-RPC calls to log
	 *
	 * @param log
	 */
	public EndpointBuilder debug(Appendable log) {
		this.log = log;
		return this;
	}

	public EndpointBuilder export(Class<?>... additional) {
		this.additionalInterfaces.addAll(Arrays.asList(additional));
		return this;
	}

	public EndpointBuilder exportMeta() {
		this.additionalInterfaces.add(Meta.class);
		return this;
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

	public EndpointBuilder using(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	public EndpointBuilder using(TypeConverter typeConverter) {
		this.typeConverter = typeConverter;
		return this;
	}

	private List<Class<?>> additionalInterfaces = new ArrayList<Class<?>>();
	private ClassLoader classLoader;
	private Appendable log;
	private TypeConverter typeConverter = DefaultTypeConverter.getInstance();
}
