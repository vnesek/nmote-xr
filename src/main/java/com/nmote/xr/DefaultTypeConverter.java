/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultTypeConverter implements TypeConverter {

	private static TypeConverter INSTANCE = new DefaultTypeConverter();

	public static TypeConverter getInstance() {
		return INSTANCE;
	}

	public static void setInstance(TypeConverter instance) {
		DefaultTypeConverter.INSTANCE = instance;
	}

	public Object toJavaObject(Object value, Type type, TypeConverter converter) throws InstantiationException,
			IllegalAccessException {
		if (value instanceof Collection) {
			Type collectionType;
			Type elementType;
			if (type instanceof ParameterizedType) {
				elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
				collectionType = ((ParameterizedType) type).getRawType();
			} else {
				elementType = Object.class;
				collectionType = ArrayList.class;
			}
			Collection result = newCollection((Collection) value, collectionType);
			for (Object e : (Collection) value) {
				result.add(converter.toJavaObject(e, elementType, converter));
			}
			value = result;
		} else if (value instanceof Map) {
			Type keyType, valueType, mapType;
			if (type instanceof ParameterizedType) {
				Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
				keyType = actualTypes[0];
				valueType = actualTypes[1];
				mapType = ((ParameterizedType) type).getRawType();
			} else {
				keyType = Object.class;
				valueType = Object.class;
				mapType = LinkedHashMap.class;
			}
			Map result = newMap((Map) value, mapType);
			for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
				Object k = converter.toJavaObject(e.getKey(), keyType, converter);
				Object v = converter.toJavaObject(e.getValue(), valueType, converter);
				result.put(k, v);
			}
			value = result;
		}

		return value;
	}

	public Object toXmlRpcValue(Object value, Type type, TypeConverter converter) throws InstantiationException,
			IllegalAccessException {
		if (value instanceof Collection) {
			Collection result = newCollection((Collection) value, ArrayList.class);
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
			Map result = newMap((Map) value, LinkedHashMap.class);
			for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
				Object k = converter.toXmlRpcValue(e.getKey(), keyType, converter);
				Object v = converter.toXmlRpcValue(e.getValue(), valueType, converter);
				result.put(k, v);
			}
			value = result;
		}

		return value;
	}

	protected Collection newCollection(Collection prototype, Type type) throws InstantiationException,
			IllegalAccessException {
		int size = prototype.size();
		Collection result;
		if (prototype instanceof Set) {
			if (type instanceof Class && Set.class.isAssignableFrom((Class<?>) type)) {
				result = (Collection) ((Class<?>) type).newInstance();
			} else {
				result = new LinkedHashSet(size);
			}
		} else {
			if (type instanceof Class && List.class.isAssignableFrom((Class<?>) type)) {
				result = (Collection) ((Class<?>) type).newInstance();
			} else {
				result = new ArrayList(size);
			}
		}
		return result;
	}

	protected Map newMap(Map prototype, Type type) throws InstantiationException, IllegalAccessException {
		Map result;
		if (type instanceof Class && Map.class.isAssignableFrom((Class<?>) type)) {
			result = (Map) ((Class<?>) type).newInstance();
		} else {
			result = new LinkedHashMap(prototype.size());
		}
		return result;
	}
}