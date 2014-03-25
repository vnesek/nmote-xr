/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DefaultTypeConverter implements TypeConverter {

	private static TypeConverter INSTANCE = new DefaultTypeConverter();

	public Object toJavaObject(Object value, Type type, TypeConverter converter, Annotation... annotations) {
		if (value instanceof Collection) {
			Type elementType = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0]
					: Object.class;
			Collection result = newCollection((Collection) value);
			for (Object e : (Collection) value) {
				result.add(converter.toJavaObject(e, elementType, converter));
			}
			value = result;
		} else if (value instanceof Map) {
			Type keyType, valueType;
			if (type instanceof ParameterizedType) {
				Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
				keyType = actualTypes[0];
				valueType = actualTypes[1];
			} else {
				keyType = Object.class;
				valueType = Object.class;
			}
			Map result = newMap((Map) value);
			for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
				Object k = converter.toJavaObject(e.getKey(), keyType, converter);
				Object v = converter.toJavaObject(e.getValue(), valueType, converter);
				result.put(k, v);
			}
			value = result;
		}

		return value;
	}

	public Object toXmlRpcValue(Object value, Type type, TypeConverter converter, Annotation... annotations) {
		if (value instanceof Collection) {
			// Type elementType = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0] : Object.class;
			Collection result = newCollection((Collection) value);
			for (Object e : (Collection) value) {
				result.add(converter.toXmlRpcValue(e, e.getClass(), converter));
			}
			value = result;
		} else if (value instanceof Map) {
			Type keyType, valueType;
			if (type instanceof ParameterizedType) {
				Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
				keyType = actualTypes[0];
				valueType = actualTypes[1];
			} else {
				keyType = Object.class;
				valueType = Object.class;
			}
			Map result = newMap((Map) value);
			for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
				Object k = converter.toXmlRpcValue(e.getKey(), keyType, converter);
				Object v = converter.toXmlRpcValue(e.getValue(), valueType, converter);
				result.put(k, v);
			}
			value = result;
		}

		return value;
	}

	public static TypeConverter getInstance() {
		return INSTANCE;
	}

	public static void setInstance(TypeConverter instance) {
		DefaultTypeConverter.INSTANCE = instance;
	}

	private static Collection newCollection(Collection prototype) {
		int size = prototype.size();
		Collection result;
		if (prototype instanceof Set) {
			result = new LinkedHashSet(size);
		} else {
			result = new ArrayList(size);
		}
		return result;
	}

	private static Map newMap(Map prototype) {
		int size = prototype.size();
		Map result = new LinkedHashMap(size);
		return result;
	}
}