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

import com.nmote.nanohttp.NanoServer;

/**
 * <p>
 * EndpointBuilder is an alternative to {@link XR} static methods. Supports
 * building of both client and server Endpoints through a fluent interface
 * style.
 * </p>
 *
 * <p>
 * Call static methods client() or server() on start building endpoint or proxy.
 * </p>
 */
public class EndpointBuilder<T> {

	/**
	 * Creates an proxy for a remote XML-RPC service located at uri
	 *
	 * @param uri
	 *            URL to a remote XML-RPC service
	 * @param clazz
	 *            declaring remote XML-RPC methods
	 * @return proxy to access XML-RPC service
	 * @throws URISyntaxException
	 *             if uri is mallformated.
	 */
	public static <T> EndpointBuilder<T> client(String uri, Class<T> clazz) throws URISyntaxException {
		return client(new URI(uri), clazz);
	}

	/**
	 * Builds an proxy for a remote XML-RPC service located at uri. Call get()
	 * to obtain instance.
	 *
	 * @param uri
	 *            URL to a remote XML-RPC service
	 * @param clazz
	 *            declaring remote XML-RPC methods
	 * @return proxy to access XML-RPC service
	 */
	public static <T> EndpointBuilder<T> client(URI uri, Class<T> clazz) {
		return new EndpointBuilder<T>(uri, clazz);
	}

	private EndpointBuilder(URI uri, Class<T> clazz) {
		this.uri = uri;
		this.clazz = clazz;
	}

	private EndpointBuilder(Object server, Class<T> clazz) {
		this.server = server;
		this.clazz = clazz;
	}

	/**
	 * Dump XML-RPC calls to System.err
	 *
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> debug() {
		return debug(System.err);
	}

	/**
	 * Dump XML-RPC calls to log.
	 *
	 * @param log
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> debug(Appendable log) {
		this.log = log;
		return this;
	}

	/**
	 * Adds classes to a list of additional interfaces implemeted by an XML-RPC
	 * server.
	 *
	 * @param additional
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> export(Class<?>... additional) {
		this.additionalInterfaces.addAll(Arrays.asList(additional));
		return this;
	}

	/**
	 * Adds system.* calls (see {@see Meta}) to a list of methods implemeted by
	 * an XML-RPC server.
	 *
	 * @param additional
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> exportMeta() {
		this.additionalInterfaces.add(Meta.class);
		return this;
	}

	/**
	 * Builds an XML-PRC server that can be exposed via {@link NanoServer}.
	 *
	 * @param server
	 * @param clazz
	 * @return HTTP server endpoint
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <Z> EndpointBuilder<HTTPServerEndpoint> server(Z server, Class<Z> clazz) {
		return new EndpointBuilder(server, clazz);
	}

	/**
	 * Builds an XML-PRC server that can be exposed via {@link NanoServer}.
	 *
	 * @param clazz
	 * @return HTTP server endpoint
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <Z> EndpointBuilder<HTTPServerEndpoint> server(Class<Z> clazz) {
		return new EndpointBuilder(null, clazz);
	}

	/**
	 * Returns either a client proxy or {@link HTTPServerEndpoint} instance.
	 *
	 * @return building result
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		T result;

		if (uri == null) {
			if (classLoader == null) {
				classLoader = clazz.getClassLoader();
			}

			Endpoint clientEndpoint;
			if (log != null) {
				clientEndpoint = new HTTPClientEndpoint(uri) {
					protected InputStream createInputStream(MethodCall call) throws IOException {
						log.append("\n--- method response ---\n");
						return new FilterInputStream(super.createInputStream(call)) {
							public void close() throws IOException {
								log.append("\n--- end response ---\n");
								super.close();
							}

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
						};
					};

					protected OutputStream createOutputStream(MethodCall call) throws IOException {
						log.append("\n--- method call ---\n");
						return new FilterOutputStream(super.createOutputStream(call)) {
							public void close() throws IOException {
								log.append("\n--- end call ---\n");
								super.close();
							}

							public void write(int b) throws IOException {
								log.append((char) b);
								super.write(b);
							}
						};
					}
				};
			} else {
				clientEndpoint = new HTTPClientEndpoint(uri);
			}

			result = new FacadeEndpoint<T>(clientEndpoint, //
					classLoader, clazz, typeConverter, //
					additionalInterfaces.toArray(new Class<?>[additionalInterfaces.size()])).newProxy();
		} else {
			ObjectEndpoint endpoint = new ObjectEndpoint();
			endpoint.faultMapper(faultMapper);
			endpoint.typeConverter(typeConverter);
			endpoint.export(server, clazz);
			for (Class<?> i : additionalInterfaces) {
				endpoint.export(server, i);
			}
			result = (T) new HTTPServerEndpoint(endpoint);
		}

		return result;
	}

	/**
	 * Use classLoader for creating proxies.
	 *
	 * @param classLoader
	 * @return
	 */
	public EndpointBuilder<T> using(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	public EndpointBuilder<T> using(FaultMapper faultMapper) {
		this.faultMapper = faultMapper;
		return this;
	}

	public EndpointBuilder<T> using(TypeConverter typeConverter) {
		this.typeConverter = typeConverter;
		return this;
	}

	private Class<T> clazz;
	private URI uri;
	private Object server;
	private List<Class<?>> additionalInterfaces = new ArrayList<Class<?>>();
	private ClassLoader classLoader;
	private FaultMapper faultMapper = DefaultFaultMapper.getInstance();
	private Appendable log;
	private TypeConverter typeConverter = DefaultTypeConverter.getInstance();
}
