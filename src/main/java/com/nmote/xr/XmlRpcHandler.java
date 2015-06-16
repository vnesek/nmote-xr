/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved.
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML XmlRpcHandler parses XmlRpc method calls and responses.
 */
// @PMD:REVIEWED:CyclomaticComplexity: by vjeko on 2005.12.28 01:28
@SuppressWarnings({ "unchecked", "rawtypes" })
class XmlRpcHandler extends DefaultHandler {

	/**
	 * Marker object for uninitialized response values.
	 */
	private static final Object UNDEFINED_RESPONSE_VALUE = new String("<response>");

	/**
	 * Marker class for multiple &lt;param&gt;s in response.
	 */
	private static final class ComplexResponse extends ArrayList<Object> {
		private static final long serialVersionUID = About.serialVersionUID;
	};

	public XmlRpcHandler() {
		super();
		text = new StringBuilder(256);
		stack = new Stack<Object>();
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] chars, int start, int length) throws SAXException {
		text.append(chars, start, length);
	}

	public void clear() {
		stack.clear();
		text.setLength(0);
		// @PMD:REVIEWED:NullAssignment: by vjeko on 2005.12.28 01:25
		result = null;
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		if (!stack.isEmpty()) {
			throw new SAXException("stack isn't empty: " + stack);
		}
		text.setLength(0);
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	// @PMD:REVIEWED:CyclomaticComplexity: by vjeko on 2005.12.28 01:28
	// @PMD:REVIEWED:AvoidReassigningParameters: by vjeko on 2005.12.28 01:15
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// Dispatch on qName
		qName = qName.intern();
		if (qName == "value" && !valueInjected) {
			endString();
		} else if (qName == "i4" || qName == "int") {
			endInt();
		} else if (qName == "array") {
			endArray();
		} else if (qName == "struct") {
			endStruct();
		} else if (qName == "boolean") {
			endBoolean();
		} else if (qName == "string") {
			endString();
		} else if (qName == "double") {
			endDouble();
		} else if (qName == "base64") {
			endBase64();
		} else if (qName == "dateTime.iso8601") {
			endDateTimeISO8601();
		} else if (qName == "fault") {
			endFault();
		} else if (qName == "member") {
			endMember();
		} else if (qName == "name") {
			endName();
		} else if (qName == "methodName") {
			endMethodName();
		} else if (qName == "methodResponse") {
			endMethodResponse();
		} else if (qName == "methodCall") {
			endMethodCall();
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	// @PMD:REVIEWED:CyclomaticComplexity: by vjeko on 2005.12.28 01:28
	// @PMD:REVIEWED:AvoidReassigningParameters: by vjeko on 2005.12.28 01:15
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// Dispatch on qName
		qName = qName.intern();
		if (qName == "value") {
			valueInjected = false;
			text.setLength(0);
		} else if (qName == "string") {
			startString();
		} else if (qName == "i4" || qName == "int") {
			startInt();
		} else if (qName == "array") {
			startArray();
		} else if (qName == "dateTime.iso8601") {
			startDateTimeISO8601();
		} else if (qName == "base64") {
			startBase64();
		} else if (qName == "struct") {
			startStruct();
		} else if (qName == "boolean") {
			startBoolean();
		} else if (qName == "double") {
			startDouble();
		} else if (qName == "fault") {
			startFault();
		} else if (qName == "member") {
			startMember();
		} else if (qName == "name") {
			startName();
		} else if (qName == "methodName") {
			startMethodName();
		} else if (qName == "methodResponse") {
			startMethodResponse();
		} else if (qName == "methodCall") {
			startMethodCall();
		}
	}

	private void endArray() {
		injectValue(popValue());
	}

	private void endBase64() {
		byte[] value = Base64.decode(text.toString());
		text.setLength(0);
		injectValue(value);
	}

	private void endBoolean() {
		String value = text.toString();
		text.setLength(0);
		injectValue(Boolean.valueOf("1".equals(value)));
	}

	private void endDateTimeISO8601() {
		Date value = ISO8601.decode(text.toString());
		text.setLength(0);
		injectValue(value);
	}

	private void endFault() {
		injectFault((Fault) popValue());
	}

	private void endInt() {
		String value = text.toString();
		text.setLength(0);
		injectValue(new Integer(value));
	}

	private void endDouble() {
		String value = text.toString();
		text.setLength(0);
		injectValue(new Double(value));
	}

	private void endMember() {
		injectMember((Object[]) popValue());
	}

	private void endMethodCall() {
		result = popValue();
	}

	private void endMethodName() {
		String value = text.toString().trim();
		text.setLength(0);
		((MethodCall) stack.peek()).setMethodName(value);
	}

	private void endMethodResponse() {
		result = popValue();
	}

	private void endName() {
		String value = text.toString();
		text.setLength(0);
		injectMemberName(value);
	}

	private void endString() {
		String value = text.toString();
		text.setLength(0);
		injectValue(value);
	}

	private void endStruct() {
		injectValue(popValue());
	}

	private void injectFault(Fault fault) {
		((MethodResponse) peekValue()).setValue(fault);
	}

	private void injectMember(Object[] member) {
		((Map) peekValue()).put(member[0], member[1]);
	}

	private void injectMemberName(String name) {
		((Object[]) peekValue())[0] = name;
	}

	private void injectValue(Object value) {
		valueInjected = true;
		Object obj = peekValue();
		if (obj instanceof List) {
			((List) obj).add(value);
		} else if (obj instanceof Object[]) {
			// Member
			((Object[]) obj)[1] = value;
		} else if (obj instanceof MethodCall) {
			((MethodCall) obj).getParams().add(value);
		} else if (obj instanceof MethodResponse) {
			MethodResponse response = ((MethodResponse) obj);
			Object responseValue = response.getValue();
			if (responseValue == UNDEFINED_RESPONSE_VALUE) {
				response.setValue(value);
			} else if (responseValue instanceof ComplexResponse) {
				((ComplexResponse) responseValue).add(value);
			} else {
				ComplexResponse cr = new ComplexResponse();
				cr.add(responseValue);
				response.setValue(cr);
			}
		} else if (obj instanceof Fault) {
			((Fault) obj).setValue((Map) value);
		}
	}

	private Object peekValue() {
		return stack.peek();
	}

	private Object popValue() {
		return stack.pop();
	}

	private void pushValue(Object object) {
		stack.push(object);
	}

	private void startArray() {
		pushValue(new ArrayList<Object>());
	}

	private void startBase64() {
		startString();
	}

	private void startBoolean() {
		text.setLength(0);
	}

	private void startDateTimeISO8601() {
		startString();
	}

	private void startFault() {
		pushValue(new Fault());
	}

	private void startInt() {
		text.setLength(0);
	}

	private void startDouble() {
		text.setLength(0);
	}

	private void startMember() {
		pushValue(new Object[2]);
	}

	private void startMethodCall() {
		pushValue(new MethodCall("<unknown>"));
	}

	private void startMethodName() {
		text.setLength(0);
	}

	private void startMethodResponse() {
		pushValue(new MethodResponse(UNDEFINED_RESPONSE_VALUE));
	}

	private void startName() {
		text.setLength(0);
	}

	private void startString() {
		text.setLength(0);
	}

	private void startStruct() {
		pushValue(new HashMap<String, Object>());
	}

	/**
	 * Convenience method that creates a XMLReader configured with this
	 * XmlRpcHandler instance.
	 *
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 */
	XMLReader newXMLReader() throws ParserConfigurationException, SAXException, FactoryConfigurationError {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = parserFactory.newSAXParser();
		XMLReader reader = saxParser.getXMLReader();
		reader.setContentHandler(this);
		return reader;
	}

	// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:25
	MethodCall parseMethodCall(InputStream in, XMLReader reader) throws IOException, SAXException {
		try {
			reader.parse(new InputSource(in));
			MethodCall result = (MethodCall) this.result;
			// @PMD:REVIEWED:NullAssignment: by vjeko on 2005.12.28 01:25
			this.result = null;
			return result;
		} finally {
			clear();
		}
	}

	// @PMD:REVIEWED:ShortVariable: by vjeko on 2005.12.28 01:26
	MethodResponse parseMethodResponse(InputStream in, XMLReader reader) throws IOException, SAXException {
		try {
			reader.parse(new InputSource(in));
			MethodResponse result = (MethodResponse) this.result;
			// @PMD:REVIEWED:NullAssignment: by vjeko on 2005.12.28 01:21
			this.result = null;
			return result;
		} finally {
			clear();
		}
	}

	private Object result;
	private final Stack<Object> stack;
	private final StringBuilder text;
	private boolean valueInjected;
}