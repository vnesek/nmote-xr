/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.xr.samples;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmote.nanohttp.NanoServer;
import com.nmote.xr.XR;
import com.nmote.xr.XRMethod;

/**
 * <p>
 * Validator1 implementation of UserLand's XmlRpc validation suite
 * (http://www.xmlrpc.com/validator1Docs).
 * <p>
 * 
 * <p>
 * You can start a server and validate this XmlRpc validation against a public
 * validator.
 * </p>
 */
public class Validator1 {

	@XRMethod("validator1.arrayOfStructsTest")
	public static int arrayOfStructsTest(List<Map<String, Integer>> a) {
		int sum = 0;
		for (Map<String, Integer> m : a) {
			sum += m.get("curly");
		}
		return sum;
	}

	@XRMethod("validator1.countTheEntities")
	public static Map<String, Integer> countTheEntities(String s) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("ctLeftAngleBrackets", 0);
		m.put("ctRightAngleBrackets", 0);
		m.put("ctAmpersands", 0);
		m.put("ctApostrophes", 0);
		m.put("ctQuotes", 0);

		for (int i = 0; i < s.length(); ++i) {
			switch (s.charAt(i)) {
			case '<':
				increase(m, "ctLeftAngleBrackets");
				break;
			case '>':
				increase(m, "ctRightAngleBrackets");
				break;
			case '&':
				increase(m, "ctAmpersands");
				break;
			case '\'':
				increase(m, "ctApostrophes");
				break;
			case '\"':
				increase(m, "ctQuotes");
				break;
			}
		}
		return m;
	}

	@XRMethod("validator1.easyStructTest")
	public static int easyStructTest(Map<String, Integer> a) {
		int sum = a.get("moe") + a.get("larry") + a.get("curly");
		return sum;
	}

	@SuppressWarnings("rawtypes")
	@XRMethod("validator1.echoStructTest")
	public static Map echoStructTest(Map a) {
		return a;
	}

	@SuppressWarnings("rawtypes")
	@XRMethod("validator1.manyTypesTest")
	public static List manyTypesTest(int n, boolean b, String s, double d, Date dateTime, byte[] base64) {
		return Arrays.asList(n, b, s, d, dateTime, base64);
	}

	@XRMethod("validator1.moderateSizeArrayCheck")
	public static String moderateSizeArrayCheck(List<String> a) {
		String first = a.get(0);
		String last = a.get(a.size() - 1);
		return first + last;
	}

	@XRMethod("validator1.nestedStructTest")
	public static int nestedStructTest(Map<String, Map<String, Map<String, Map<String, Integer>>>> a) {
		Map<String, Integer> foolsDay = a.get("2000").get("04").get("01");
		int result = easyStructTest(foolsDay);
		return result;
	}

	@XRMethod("validator1.simpleStructReturnTest")
	public static Map<String, Integer> simpleStructReturnTest(int n) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("times10", n * 10);
		result.put("times100", n * 100);
		result.put("times1000", n * 1000);
		return result;
	}

	private static void increase(Map<String, Integer> m, String key) {
		m.put(key, m.get(key) + 1);
	}

	public static void main(String[] args) throws Exception {
		NanoServer server = new NanoServer(7070);
		server.add(XR.server(Validator1.class));
		server.start();
	}
}