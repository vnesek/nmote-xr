/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface TypeConverter {

	Object toXmlRpcValue(Object object, Type type, TypeConverter converter, Annotation... annotations);

	Object toJavaObject(Object value, Type type, TypeConverter converter, Annotation... annotations);
}
