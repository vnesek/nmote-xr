/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;


@SuppressWarnings({"unchecked", "rawtypes"})
class XmlRpcWriter extends FilterWriter {

	/**
	 * @param out
	 */
	public XmlRpcWriter(Writer out) {
		super(out);
	}

	public void writeArray(Collection value) throws IOException {
		out.write("\n\t\t<value><array><data>");
		for (Object e : value) {
			writeValue(e);
		}
		out.write("</data></array></value>");
	}

	public void writeBase64(byte[] value) throws IOException {
		out.write("\n\t\t<value><base64>");
		out.write(Base64.encodeBytes(value));
		out.write("</base64></value>");
	}

	public void writeBoolean(boolean value) throws IOException {
		out.write("\n\t\t<value><boolean>");
		out.write(value ? "1" : "0");
		out.write("</boolean></value>");
	}

	public void writeDateTimeISO8601(Date value) throws IOException {
		out.write("\n\t\t<value><dateTime.iso8601>");
		out.write(ISO8601.encode(value));
		out.write("</dateTime.iso8601></value>");
	}

	public void writeDouble(double value) throws IOException {
		out.write("\n\t\t<value><double>");
		out.write(Double.toString(value));
		out.write("</double></value>");
	}

	public void writeInt(int value) throws IOException {
		out.write("\n\t\t<value><int>");
		out.write(Integer.toString(value));
		out.write("</int></value>");
	}

	public void writeMethodCall(MethodCall call) throws IOException {
		out.write("<?xml version='1.0' encoding='utf-8'?>\n<methodCall>\n\t<methodName>");
		out.write(call.getMethodName());
		out.write("</methodName>\n\t<params>");
		for (Object param : call.getParams()) {
			out.write("\n\t\t<param>");
			writeValue(param);
			out.write("\n\t\t</param>");
		}
		out.write("\n\t</params>\n</methodCall>");
	}

	public void writeMethodResponse(MethodResponse response) throws IOException {
		out.write("<?xml version='1.0' encoding='utf-8'?>\n<methodResponse>");
		Object value = response.getValue();
		if (value instanceof Fault) {
			out.write("\n\t<fault>");
			writeValue(((Fault) value).getValue());
			out.write("\n\t</fault>");
		} else {
			out.write("\n\t<params>\n\t\t<param>");
			writeValue(value);
			out.write("\n\t\t</param>\n\t</params>");
		}
		out.write("\n</methodResponse>");
	}

	// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:22
	public void writeString(String s) throws IOException {
		out.write("\n\t\t<value><string>");
		for (int i = 0, m = s.length(); i < m; ++i) {
			char c = s.charAt(i);
			switch (c) {
			case '&':
				out.write("&amp;");
				break;
			case '<':
				out.write("&lt;");
				break;
			case '>':
				out.write("&gt;");
				break;
			default:
				out.write(c);
			}
		}
		out.write("</string></value>");
	}

	public void writeStruct(Map value) throws IOException {
		out.write("\n\t\t<value><struct>");
		for (Map.Entry e : (Set<Map.Entry>) value.entrySet()) {
			out.write("\n\t\t\t<member><name>");
			out.write((String) e.getKey());
			out.write("</name>");
			writeValue(e.getValue());
			out.write("\n\t\t\t</member>");
		}
		out.write("\n\t\t</struct></value>");
	}

	public void writeValue(Object value) throws IOException {
		if (value instanceof byte[]) {
			writeBase64((byte[]) value);
		} else if (value.getClass().isArray()) {
			writeArray(Arrays.asList((Object[]) value));
		} else if (value instanceof String) {
			writeString((String) value);
		} else if (value instanceof Double || value instanceof Float) {
			writeDouble(((Number) value).doubleValue());
		} else if (value instanceof Number) {
			writeInt(((Number) value).intValue());
		} else if (value instanceof Map) {
			writeStruct((Map) value);
		} else if (value instanceof Collection) {
			writeArray((Collection) value);
		} else if (value instanceof Boolean) {
			writeBoolean(((Boolean) value).booleanValue());
		} else if (value instanceof Date) {
			writeDateTimeISO8601((Date) value);
		}
	}

	public static void main(String[] args) throws IOException {
		// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:22
		// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:22
		StringWriter w = new StringWriter();
		// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:26
		XmlRpcWriter xw = new XmlRpcWriter(w);
		MethodCall call = new MethodCall("sample.call");
		call.getParams().add("Foo");
		call.getParams().add(39);
		xw.writeMethodCall(call);
		xw.close();
		// @PMD:REVIEWED:SystemPrintln: by vjeko on 2005.12.28 01:22
		System.out.println(w);
	}
}