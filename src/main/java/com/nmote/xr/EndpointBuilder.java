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

import com.nmote.nanohttp.NanoServer;
import com.nmote.xr.log.LoggerAdapter;

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
 *
 * @param <T>
 *            proxy interface
 */
public class EndpointBuilder<T> {

	/**
	 * Creates an proxy for a remote XML-RPC service located at <code>uri</code>
	 * . Call get() to obtain instance.
	 *
	 * @param uri
	 *            URL to a remote XML-RPC service
	 * @param clazz
	 *            declaring remote XML-RPC methods
	 * @param <T>
	 *            proxy interface
	 * @return proxy to access XML-RPC service
	 * @throws URISyntaxException
	 *             if uri is mallformated.
	 */
	public static <T> EndpointBuilder<T> client(String uri, Class<T> clazz) throws URISyntaxException {
		return client(new URI(uri), clazz);
	}

	/**
	 * Builds an proxy for a remote XML-RPC service located at <code>uri</code>.
	 * Call get() to obtain instance.
	 *
	 * @param uri
	 *            URL to a remote XML-RPC service
	 * @param clazz
	 *            declaring remote XML-RPC methods
	 * @param <T>
	 *            proxy interface
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
		return debug(LoggerAdapter.SYSTEM_ERR);
	}

	/**
	 * Dump XML-RPC calls to log.
	 *
	 * @param logger
	 *            logger adapter instance to log calls to
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> debug(LoggerAdapter logger) {
		this.logger = logger;
		return this;
	}

	/**
	 * Adds classes to a list of additional interfaces implemented by an XML-RPC
	 * server. Interface methods should be annotated by {@link XRMethod}.
	 *
	 * @param additional
	 *            interfaces implemented by XML-RPC server
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> export(Class<?>... additional) {
		this.additionalInterfaces.addAll(Arrays.asList(additional));
		return this;
	}

	/**
	 * Adds system.* calls to a list of methods implemeted by an XML-RPC server.
	 * Interface methods should be annotated by {@link XRMethod}.
	 *
	 * @see Meta
	 *
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> exportMeta() {
		this.additionalInterfaces.add(Meta.class);
		return this;
	}

	/**
	 * Builds an XML-PRC server that can be exposed via {@link NanoServer}.
	 * Methods declared by <code>clazz</code> should be annotated by
	 * {@link XRMethod}.
	 *
	 * @param server
	 *            Java object implementing service
	 * @param clazz
	 *            declaring XML-RPC methods
	 * @param <Z>
	 *            server type
	 * @return HTTP server endpoint
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <Z> EndpointBuilder<HTTPServerEndpoint> server(Z server, Class<Z> clazz) {
		return new EndpointBuilder(server, clazz);
	}

	/**
	 * Builds an XML-PRC server that can be exposed via {@link NanoServer}.
	 * Public static methods declared by <code>clazz</code> should be annotated
	 * by {@link XRMethod}.
	 *
	 * @param clazz
	 *            declaring XML-RPC methods
	 * @return HTTP server endpoint
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EndpointBuilder<HTTPServerEndpoint> server(Class<?> clazz) {
		return new EndpointBuilder(null, clazz);
	}

	private static class LoggerEndpoint implements Endpoint {

		LoggerEndpoint(Endpoint delegate, LoggerAdapter logger) {
			this.delegate = delegate;
			this.logger = logger;
		}


		private final Endpoint delegate;
		private final LoggerAdapter logger;
		public MethodResponse call(MethodCall call) {
			call.setAttribute(LoggerAdapter.LOGGER_KEY, logger);
			try {
			return delegate.call(call);
			} finally {
				call.setAttribute(LoggerAdapter.LOGGER_KEY, null);
			}
		}
	}

	/**
	 * Returns either a client proxy or {@link HTTPServerEndpoint} instance.
	 *
	 * @return building result
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		T result;

		if (uri != null) {
			if (classLoader == null) {
				classLoader = clazz.getClassLoader();
			}

			Endpoint clientEndpoint = new HTTPClientEndpoint(uri);
			if (logger != null) {
				clientEndpoint = new LoggerEndpoint(clientEndpoint, logger);
			}

			result = new FacadeEndpoint<T>(clientEndpoint, //
					classLoader, clazz, typeConverter, //
					additionalInterfaces.toArray(new Class<?>[additionalInterfaces.size()])) //
					.newProxy();
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
	 *            for creating proxies
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> using(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	/**
	 * Use faultMapper for creating proxies.
	 *
	 * @param faultMapper
	 *            for {@link Fault} conversion
	 * @return this for method chaining
	 */
	public EndpointBuilder<T> using(FaultMapper faultMapper) {
		this.faultMapper = faultMapper;
		return this;
	}

	/**
	 * Use typeConverter for parameter conversions between Java and XML-RPC
	 * values.
	 *
	 * @param typeConverter
	 *            to convert method parameter and result value conversions
	 * @return this for method chaining
	 */

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
	private LoggerAdapter logger = LoggerAdapter.SYSTEM_ERR;
	private TypeConverter typeConverter = DefaultTypeConverter.getInstance();
}
