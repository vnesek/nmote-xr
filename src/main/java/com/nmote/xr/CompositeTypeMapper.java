/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

public class CompositeTypeMapper implements TypeConverter {

	public CompositeTypeMapper(Collection<TypeConverter> faultMappers) {
		assert faultMappers != null;
		this.typeConverters = faultMappers;
	}

	public CompositeTypeMapper(TypeConverter... faultMappers) {
		this(Arrays.asList(faultMappers));
	}

	public Object toJavaObject(Object value, Type type, TypeConverter converter) throws InstantiationException, IllegalAccessException {
		Object result = null;
		for (TypeConverter typeConverter : typeConverters) {
			result = typeConverter.toJavaObject(value, type, converter);
			if (result != null) break;
		}
		return result;
	}

	public Object toXmlRpcValue(Object object, Type type, TypeConverter converter)
			throws InstantiationException, IllegalAccessException {
		Object result = null;
		for (TypeConverter typeConverter : typeConverters) {
			result = typeConverter.toXmlRpcValue(object, type, converter);
			if (result != null) break;
		}
		return result;
	}

	private final Collection<TypeConverter> typeConverters;
}