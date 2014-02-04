/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.spring;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

import com.nmote.xr.DefaultFaultMapper;
import com.nmote.xr.DefaultTypeConverter;
import com.nmote.xr.DelegateEndpoint;
import com.nmote.xr.Endpoint;
import com.nmote.xr.FaultMapper;
import com.nmote.xr.Meta;
import com.nmote.xr.MethodCall;
import com.nmote.xr.MethodResponse;
import com.nmote.xr.ObjectEndpoint;
import com.nmote.xr.TypeConverter;
import com.nmote.xr.XR;
import com.nmote.xr.XRServlet;

public class XRSpringExporter implements ServletContextAware, DisposableBean {

	private static final Logger LOG = LoggerFactory.getLogger("com.nmote.xr.Call");

	public void destroy() throws Exception {
		Assert.notNull(servletContext);
		servletContext.removeAttribute(endpointKey);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setEndpointKey(String endpointKey) {
		this.endpointKey = endpointKey;
	}

	public void setExportMeta(boolean exportMeta) {
		this.exportMeta = exportMeta;
	}

	public void setFaultMapper(FaultMapper faultMapper) {
		Assert.notNull(faultMapper);
		this.faultMapper = faultMapper;
	}

	public void setServers(List<XRExportDef<?>> servers) {
		Assert.notNull(servers);
		Assert.notEmpty(servers);
		this.servers = servers;
	}

	public void setServletContext(ServletContext servletContext) {
		Assert.notNull(servers);
		Assert.notEmpty(servers);

		// Save servlet context for destroy
		this.servletContext = servletContext;

		ObjectEndpoint endpoint = new ObjectEndpoint();
		for (@SuppressWarnings("rawtypes") XRExportDef e : servers) {
			endpoint.prefix(e.getPrefix());
			endpoint.faultMapper(e.getFaultMapper() != null ? e.getFaultMapper() : faultMapper);
			endpoint.typeConverter(typeConverter);
			endpoint.export(e.getServer(), e.getExport());
		}

		// Export system.* methods
		if (exportMeta) {
			endpoint.prefix(null);
			endpoint.faultMapper(faultMapper);
			endpoint.exportMeta();

			Meta meta = (Meta) XR.proxy(endpoint, Meta.class);
			LOG.debug("Exported " + meta.listMethods());
		}

		servletContext.setAttribute(endpointKey, prepare(endpoint));
	}

	public void setTypeConverter(TypeConverter typeConverter) {
		Assert.notNull(typeConverter);
		this.typeConverter = typeConverter;
	}

	/**
	 * Give subclass a chance to modify endpoint prior to export.
	 *
	 * @param endpoint
	 * @return
	 */
	protected Endpoint prepare(ObjectEndpoint endpoint) {
		Endpoint result = endpoint;
		if (debug) {
			result = new DelegateEndpoint(result) {
				public MethodResponse call(MethodCall call) {
					long started = System.currentTimeMillis();
					MethodResponse response = super.call(call);
					long elapsed = System.currentTimeMillis() - started;
					LOG.debug(call.toString() + " => " + response + " (" + elapsed + " ms)");
					return response;
				};
			};
		}
		return result;
	}

	private boolean debug = true;
	private String endpointKey = XRServlet.ENDPOINT_KEY;
	private boolean exportMeta = true;
	private FaultMapper faultMapper = DefaultFaultMapper.getInstance();
	private List<XRExportDef<?>> servers = Collections.emptyList();
	private ServletContext servletContext;
	private TypeConverter typeConverter = DefaultTypeConverter.getInstance();
}