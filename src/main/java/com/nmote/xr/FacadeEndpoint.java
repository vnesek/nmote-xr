/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class FacadeEndpoint<T> extends DelegateEndpoint implements InvocationHandler {

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
	}

	public T newProxy() {
		Object proxy = java.lang.reflect.Proxy.newProxyInstance(classLoader, exportInterfaces, this);
		return clazz.cast(proxy);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		XRMethod xrm = method.getAnnotation(XRMethod.class);
		if (xrm == null) {
			throw new Error("only calls to method annotated with " + XRMethod.class + " are permitted");
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

	@SuppressWarnings("unchecked")
	public static <T> FacadeEndpoint<T> getProxy(Object proxy) {
		return (FacadeEndpoint<T>) java.lang.reflect.Proxy.getInvocationHandler(proxy);
	}

	private final Class<T> clazz;
	private final Class<?>[] exportInterfaces;
	private final ClassLoader classLoader;
	private final TypeConverter typeConverter;
}