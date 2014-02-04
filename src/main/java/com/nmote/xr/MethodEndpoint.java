/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MethodEndpoint implements Meta {

	public MethodEndpoint(Method method) {
		this(method, null);
	}

	public MethodEndpoint(Method method, Object server) {
		this(method, server, null);
	}

	public MethodEndpoint(Method method, Object server, String methodName) {
		this(method, server, methodName, DefaultFaultMapper.getInstance());
	}

	public MethodEndpoint(Method method, Object server, String methodName, FaultMapper faultMapper) {
		this(method, server, methodName, faultMapper, DefaultTypeConverter.getInstance());
	}

	public MethodEndpoint(Method method, Object server, String methodName, FaultMapper faultMapper,
			TypeConverter typeConverter) {
		super();
		this.server = server;
		this.method = method;
		this.parameterTypes = method.getGenericParameterTypes();
		this.returnType = method.getGenericReturnType();

		if (method.isAnnotationPresent(XRMethod.class) && methodName == null) {
			methodName = method.getAnnotation(XRMethod.class).value();
			if (XRMethod.METHOD_NAME.equals(methodName)) {
				methodName = null;
			}
		}

		if (methodName == null) {
			methodName = method.getName();
		}
		this.methodName = methodName;

		if (faultMapper == null) { throw new IllegalArgumentException("faultMapper not specified"); }
		this.faultMapper = faultMapper;

		if (typeConverter == null) { throw new IllegalArgumentException("typeConverter not specified"); }
		this.typeConverter = typeConverter;
	}

	public MethodResponse call(MethodCall call) {
		Object result;
		try {
			// Convert params
			Object[] params = call.getParams().toArray();
			for (int i = params.length - 1; i >= 0; --i) {
				params[i] = typeConverter.toJavaObject(params[i], parameterTypes[i], typeConverter);
			}

			// Invoke method
			result = method.invoke(server, params);

			// Convert result
			result = typeConverter.toXmlRpcValue(result, returnType, typeConverter);
		} catch (IllegalArgumentException e) {
			result = Fault.newSystemFault(Fault.ILLEGAL_ARGUMENT, e.getMessage());
		} catch (IllegalAccessException e) {
			result = Fault.newSystemFault(Fault.ILLEGAL_ACCESS, e.getMessage());
		} catch (ExceptionInInitializerError e) {
			result = Fault.newSystemFault(Fault.SERVER_INITIALIZATION, e.getMessage());
		} catch (NullPointerException e) {
			result = Fault.newSystemFault(Fault.INSTANCE_METHOD, e.getMessage());
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof Fault) {
				result = cause;
			} else {
				result = faultMapper.toFault(cause);
				if (result == null) {
					result = new Fault(Fault.CUSTOM_FAULT, cause.toString());
				}
			}
		}
		return new MethodResponse(result);
	}

	public String getMethodName() {
		return methodName;
	}

	public String getSignature() {
		StringBuilder b = new StringBuilder();
		b.append(getMethodName());
		b.append('(');
		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < types.length; ++i) {
			if (i > 0) b.append(", ");
			b.append(types[i].getSimpleName().toLowerCase());
		}
		b.append(") => ");
		b.append(method.getReturnType().getSimpleName().toLowerCase());
		return b.toString();
	}

	public String help(String methodName) {
		StringBuilder result = new StringBuilder(getSignature());
		if (method.isAnnotationPresent(XRMethod.class)) {
			String help = method.getAnnotation(XRMethod.class).help();
			if (help.length() > 0) {
				result.append(" (");
				result.append(help);
				result.append(")");
			}
		}
		return result.toString();
	}

	public List<String> listMethods() {
		return Collections.singletonList(methodName);
	}

	public boolean supports(String methodName) {
		return this.methodName.equals(methodName);
	}
	
	@Override
	public String toString() {
		return methodName + "/" + method;
	}

	private final FaultMapper faultMapper;
	private final Method method;
	private final String methodName;
	private final Type[] parameterTypes;
	private final Type returnType;
	private final Object server;
	private final TypeConverter typeConverter;
}