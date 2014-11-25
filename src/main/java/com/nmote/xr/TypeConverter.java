/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.reflect.Type;

/**
 * TypeConverter converts to and from Java objects and XML-RPC values.
 */
public interface TypeConverter {

	/**
	 * Converts an Java object value to a XML-RPC suported values
	 *
	 * @param object
	 *            an Java object supported by this TypeConverter
	 * @param type
	 *            XML-RPC value type to convert to
	 * @param converter
	 *            to use for additional recursive conversions
	 * @return converted value or null if converter doesn't support conversion
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	Object toXmlRpcValue(Object object, Type type, TypeConverter converter) throws InstantiationException,
			IllegalAccessException;

	/**
	 * Converts an XML-RPC value to a Java object.
	 *
	 * @param object
	 *            an XML-RPC value supported by this TypeConverter
	 * @param type
	 *            Java object to convert to
	 * @param converter
	 *            to use for additional recursive conversions
	 * @return converted value or null if converter doesn't support conversion
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	Object toJavaObject(Object value, Type type, TypeConverter converter) throws InstantiationException,
			IllegalAccessException;
}
