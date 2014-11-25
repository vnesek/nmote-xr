/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoint implementation that adapts an object instance. It is possible to
 * export several object instance or static class methods through a single
 * ObjectEndpoint.
 */
public class ObjectEndpoint implements Endpoint, Meta {

	public MethodResponse call(MethodCall call) {
		MethodEndpoint method = methods.get(call.getMethodName());
		MethodResponse response;
		if (method != null) {
			response = method.call(call);
		} else {
			response = new MethodResponse(Fault.newSystemFault(Fault.METHOD_NOT_SUPPORTED, call.getMethodName()));
		}
		return response;
	}

	/**
	 * Exports public static methods annotated by {@link XRMethod}.
	 *
	 * @param clazz
	 *            to export methods from
	 * @return this instance for call chaining.
	 */
	public ObjectEndpoint export(Class<?> clazz) {
		return export(null, clazz);
	}

	/**
	 * Exports public methods annotated by {@link XRMethod} declared on a clazz.
	 *
	 * @param server
	 *            object instance exposing XML-RPC methods
	 * @param clazz
	 *            to export methods from
	 * @return this instance for call chaining.
	 */
	public ObjectEndpoint export(Object server, Class<?> clazz) {
		// If clazz isn't specified than export all methods on a object
		if (clazz == null) {
			clazz = (Class<?>) server.getClass();
		}

		for (Method m : clazz.getMethods()) {
			// Ignore all java.lang.Object methods
			if (Object.class.equals(m.getDeclaringClass())) {
				continue;
			}
			MethodEndpoint method = new MethodEndpoint(m, server, null, faultMapper, typeConverter);
			String name = method.getMethodName();
			if (prefix != null) {
				name = prefix + "." + name;
			}
			if (methods.containsKey(name)) {
				throw new IllegalArgumentException("methods " + m + " and " + methods.get(name) + " are exported as "
						+ name);
			}
			methods.put(name, method);
		}

		return this;
	}

	/**
	 * Exports {@link Meta} (system.*) methods.
	 *
	 * @return this instance for call chaining.
	 */
	public ObjectEndpoint exportMeta() {
		return export(this, Meta.class);
	}

	/**
	 * Sets a {@link FaultMapper} to use by this Endpoint. To be used,
	 * FaultMapper must be assigned prior to calling
	 * {@link ObjectEndpoint#export(Object, Class)}.
	 *
	 * @param faultMapper
	 *            for {@link Fault} conversion.
	 * @return this instance for call chaining.
	 * @throws NullPointerException
	 *             if faultMapper is null
	 */
	public ObjectEndpoint faultMapper(FaultMapper faultMapper) {
		if (faultMapper == null) {
			throw new NullPointerException("faultMapper == null");
		}
		this.faultMapper = faultMapper;
		return this;
	}

	public String help(String methodName) {
		MethodEndpoint method = methods.get(methodName);
		if (method == null) {
			throw Fault.newSystemFault(Fault.METHOD_NOT_SUPPORTED, methodName);
		}
		return method.help(methodName);
	}

	public List<String> listMethods() {
		List<String> result = new ArrayList<String>(methods.keySet());
		Collections.sort(result);
		return result;
	}

	/**
	 * Sets a prefix for all methods names exposed by this Endpoint. Prefix must
	 * be assigned prior to calling {@link ObjectEndpoint#export(Object, Class)}
	 * .
	 *
	 * @param prefix
	 *            used to scope method names. Pass null to clear prefix.
	 * @return this instance for call chaining.
	 */
	public ObjectEndpoint prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public boolean supports(String methodName) {
		return methods.keySet().contains(methodName);
	}

	/**
	 * Sets a {@link TypeConverter} to use by this Endpoint. To be used,
	 * TypeConverter must be assigned prior to calling
	 * {@link ObjectEndpoint#export(Object, Class)}.
	 *
	 * @param typeConverter
	 *            to convert method parameter and result value conversions
	 * @return this instance for call chaining.
	 * @throws NullPointerException
	 *             if typeConverter is null
	 */
	public ObjectEndpoint typeConverter(TypeConverter typeConverter) {
		if (typeConverter == null) {
			throw new NullPointerException("typeConverter == null");
		}
		this.typeConverter = typeConverter;
		return this;
	}

	private FaultMapper faultMapper = DefaultFaultMapper.getInstance();
	private final Map<String, MethodEndpoint> methods = new HashMap<String, MethodEndpoint>();
	private String prefix;
	private TypeConverter typeConverter = DefaultTypeConverter.getInstance();
}