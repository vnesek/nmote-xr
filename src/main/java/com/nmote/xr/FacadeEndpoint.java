/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Provides a factory for generating proxies implementing Java interfaces that
 * make XML-RPC calls to a target Endpoint. Usually, target endpoint will be
 * {@link HTTPClientEndpoint} allowing you to make normal Java method calls to
 * remote XML-RPC services.
 *
 * @param <T>
 *            java interface declaring one or more XML-RPC methods
 */
public class FacadeEndpoint<T> extends DelegateEndpoint implements InvocationHandler {

	/**
	 * Returns an {@link FacadeEndpoint} instance that created proxy.
	 *
	 * @param proxy
	 *            instance created by FacadeEndpoint
	 * @return FacadeInterface that created proxy
	 */
	@SuppressWarnings("unchecked")
	public static <T> FacadeEndpoint<T> getProxy(Object proxy) {
		return (FacadeEndpoint<T>) Proxy.getInvocationHandler(proxy);
	}

	private static int countXRMethods(Class<?>... interfaces) {
		int count = 0;
		for (int i = 0; i < interfaces.length; ++i) {
			Method[] methods = interfaces[i].getMethods();
			for (int j = 0; j < methods.length; ++j) {
				if (methods[j].getAnnotation(XRMethod.class) != null) {
					++count;
				}
			}
		}
		return count;
	}

	public FacadeEndpoint(Endpoint endpoint, Class<T> clazz, Class<?>... additionalInterfaces) {
		this(endpoint, clazz.getClassLoader(), clazz, DefaultTypeConverter.getInstance(), additionalInterfaces);
	}

	public FacadeEndpoint(Endpoint endpoint, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) {
		this(endpoint, clazz.getClassLoader(), clazz, typeConverter, additionalInterfaces);
	}

	public FacadeEndpoint(Endpoint endpoint, ClassLoader classLoader, Class<T> clazz, Class<?>... additionalInterfaces) {
		this(endpoint, clazz.getClassLoader(), clazz, DefaultTypeConverter.getInstance(), additionalInterfaces);
	}

	public FacadeEndpoint(Endpoint endpoint, ClassLoader classLoader, Class<T> clazz, TypeConverter typeConverter,
			Class<?>... additionalInterfaces) {
		super(endpoint);
		this.clazz = clazz;
		this.classLoader = classLoader;
		this.typeConverter = typeConverter;
		this.exportInterfaces = new Class<?>[additionalInterfaces.length + 1];
		System.arraycopy(additionalInterfaces, 0, exportInterfaces, 0, additionalInterfaces.length);
		exportInterfaces[additionalInterfaces.length] = clazz;

		// Check for @XRMethod annotated methods, there should be at least one
		if (countXRMethods(exportInterfaces) == 0) {
			throw new RuntimeException("no methods annotated with " + XRMethod.class + " found on " + clazz);
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		XRMethod xrm = method.getAnnotation(XRMethod.class);
		if (xrm == null) {
			throw new RuntimeException("only calls to method annotated with " + XRMethod.class + " are permitted");
		}
		String name = xrm.value();
		if (XRMethod.METHOD_NAME.equals(name)) {
			name = method.getName();
		}
		if (args == null) {
			args = new Object[0];
		}

		Type[] parameterTypes = method.getGenericParameterTypes();
		Type returnType = method.getGenericReturnType();

		// Convert args
		for (int i = args.length - 1; i >= 0; --i) {
			args[i] = typeConverter.toXmlRpcValue(args[i], parameterTypes[i], typeConverter);
		}

		MethodCall call = new MethodCall(xrm.value(), args);
		MethodResponse response = call(call);
		if (response.isFault()) {
			// Rethrow fault to shorten a stack trace
			Fault fault = (Fault) response.getValue();
			throw new Fault(fault.getFaultCode(), fault.getFaultString());
		}
		Object result = response.getValue();

		// Convert result;
		result = typeConverter.toJavaObject(result, returnType, typeConverter);
		return result;
	}

	/**
	 * Creates an proxy implementing T and all passed additional interfaces.
	 *
	 * @return new proxy instance
	 */
	public T newProxy() {
		Object proxy = Proxy.newProxyInstance(classLoader, exportInterfaces, this);
		return clazz.cast(proxy);
	}

	private final ClassLoader classLoader;
	private final Class<T> clazz;
	private final Class<?>[] exportInterfaces;
	private final TypeConverter typeConverter;
}