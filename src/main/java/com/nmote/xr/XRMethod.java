/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate methods exposed through XML-RPC services. Can be used on concrete
 * and static methods to expose server implementations or on interface methods
 * to declare proxies.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface XRMethod {

	static final String METHOD_NAME = "<method>";

	/**
	 * XML-RPC method name. If ommited Java method name will be used as XML-RPC
	 * method name.
	 *
	 * @return XML-RPC method name
	 */
	String value() default METHOD_NAME;

	/**
	 * Help/usage description of method. Exposed via {@link Meta} interface.
	 *
	 * @return help/usage description of method
	 */
	String help() default "";
}
