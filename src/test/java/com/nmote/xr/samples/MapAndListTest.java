/*
 * Copyright 2014 Adrian Imboden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nmote.xr.samples;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmote.nanohttp.NanoServer;
import com.nmote.xr.XR;
import com.nmote.xr.XRMethod;

/**
 *
 * @author adrian
 */
public class MapAndListTest {

    public static interface ListTestServerInterface {

        @XRMethod("concatenate")
        String concatenate(List<String> texts);
    }

    public static class ListTestServer implements ListTestServerInterface {

        public String concatenate(List<String> texts) {
            String result = "";

            for (String text : texts) {
                result += text;
            }

            return result;
        }
    }

    private static <T> void assertEquals(T expected, T actual) throws Exception
    {
        if (!expected.equals(actual))
        {
            throw new Exception("Test Failed (" + expected + " != " + actual + ")");
        }
    }

    public static void testList() throws Exception {
        ListTestServer rpcServer = new ListTestServer();
        NanoServer nanoServer = new NanoServer("http://localhost:8123");
        try {
            nanoServer.add(XR.server(rpcServer, ListTestServerInterface.class));
            nanoServer.start();

            //Class Server method.
            ListTestServerInterface rpcClient = XR.proxy(new URI("http://localhost:8123"), ListTestServerInterface.class);
            List<String> list = new ArrayList<String>();
            list.add("Hello");
            list.add(" ");
            list.add("World");
            String result = rpcClient.concatenate(list);
            assertEquals("Hello World", result);
        } finally {
            nanoServer.stop();
        }
    }

    public static interface MapTestServerInterface {

        @XRMethod("getFromMap")
        String getFromMap(Map<String, String> texts, String key);
    }

    public static class MapTestServer implements MapTestServerInterface {

        public String getFromMap(Map<String, String> texts, String key) {
            return texts.get(key);
        }
    }

    public static void testMap() throws Exception {
        MapTestServer rpcServer = new MapTestServer();
        NanoServer nanoServer = new NanoServer("http://localhost:8123");
        try {
            nanoServer.add(XR.server(rpcServer, MapTestServerInterface.class));
            nanoServer.start();

            //Class Server method.
            MapTestServerInterface rpcClient = XR.proxy(new URI("http://localhost:8123"), MapTestServerInterface.class);
            Map<String, String> map = new HashMap<String, String>();
            map.put("a", "8");
            map.put("b", "42");
            String result = rpcClient.getFromMap(map, "b");
            assertEquals("42", result);
        } finally {
            nanoServer.stop();
        }
    }

    public static interface MapOfListsTestServerInterface {

        @XRMethod("concatenateOne")
        String concatenateOne(Map<String, List<String>> texts, String key);
    }

    public static class MapOfListsTestServer implements MapOfListsTestServerInterface {

        public String concatenateOne(Map<String, List<String>> texts, String key) {
            String result = "";

            for (String text : texts.get(key)) {
                result += text;
            }

            return result;
        }
    }

    public static void testMapOfLists() throws Exception {
        MapOfListsTestServer rpcServer = new MapOfListsTestServer();
        NanoServer nanoServer = new NanoServer("http://localhost:8123");
        try {
            nanoServer.add(XR.server(rpcServer, MapOfListsTestServerInterface.class));
            nanoServer.start();

            //Class Server method.
            MapOfListsTestServerInterface rpcClient = XR.proxy(new URI("http://localhost:8123"), MapOfListsTestServerInterface.class);
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            List<String> list1 = new ArrayList<String>();
            list1.add("Hello");
            list1.add(" ");
            list1.add("World");
            map.put("a", list1);
            List<String> list2 = new ArrayList<String>();
            list2.add("Stay");
            list2.add(" ");
            list2.add("Away");
            map.put("b", list2);
            assertEquals("Hello World", rpcClient.concatenateOne(map, "a"));
            assertEquals("Stay Away", rpcClient.concatenateOne(map, "b"));
        } finally {
            nanoServer.stop();
        }
    }

    public static interface ListsOfMapsTestServerInterface {

        @XRMethod("concatenateOne")
        String concatenateOne(List<Map<String, String>> texts, String key);
    }

    public static class ListsOfMapsTestServer implements ListsOfMapsTestServerInterface {

        public String concatenateOne(List<Map<String, String>> texts, String key) {
            String result = "";

            for (Map<String, String> textParts : texts) {
                result += textParts.get(key);
            }

            return result;
        }
    }

    public static void testListOfMaps() throws Exception {
        ListsOfMapsTestServer rpcServer = new ListsOfMapsTestServer();
        NanoServer nanoServer = new NanoServer("http://localhost:8123");
        try {
            nanoServer.add(XR.server(rpcServer, ListsOfMapsTestServerInterface.class));
            nanoServer.start();

            //Class Server method.
            ListsOfMapsTestServerInterface rpcClient = XR.proxy(new URI("http://localhost:8123"), ListsOfMapsTestServerInterface.class);
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("a", "Hello");
            map1.put("b", "Stay");
            list.add(map1);
            Map<String, String> map2 = new HashMap<String, String>();
            map2.put("a", " ");
            map2.put("b", " ");
            list.add(map2);
            Map<String, String> map3 = new HashMap<String, String>();
            map3.put("a", "World");
            map3.put("b", "Away");
            list.add(map3);
            assertEquals("Hello World", rpcClient.concatenateOne(list, "a"));
            assertEquals("Stay Away", rpcClient.concatenateOne(list, "b"));
        } finally {
            nanoServer.stop();
        }
    }

    public static void main(String[] args) throws Exception {
        testList();
        testMap();
        testMapOfLists();
        testListOfMaps();
        System.out.println("Tests passed");
    }
}
