nmote-xr - Simple (X)ML-(R)PC library
=====================================

Java 1.5+ client and server implementation of XML-RPC protocol.
See http://en.wikipedia.org/wiki/XML-RPC

Features
--------
* Supports Java 1.5+
* Reflective and programmable calls
* Basic springframework integration
* No external dependecies other than slf4j-api
* Server and client support
* BSD style license

Building
--------
To produce nmote-xr.jar you will need apache maven installed. Run:

> mvn clean package

Usage
-----

* Call userland service

```java
	Endpoint server = XR.client(new URI("http://betty.userland.com/RPC2"));
	MethodCall call = new MethodCall("examples.getStateName", 3);
	MethodResponse response = server.call(call);
	System.out.println(call + " => " + response);
```

* Same as above, but use type safe interface 'Betty'

```java
	interface Betty {
		@XRMethod("examples.getStateName")
		public String getStateName(int n);
	}

	Betty betty = XR.proxy(new URI("http://betty.userland.com/RPC2"), Betty.class);
	String result = betty.getStateName(41);
	System.out.println(result);
```

* There is also a `com.nmote.xr.Meta` interface to support rudimentary reflection of XML-RPC

```java
		Meta xrs = XR.proxy(new URI("http://betty.userland.com/RPC2"), Meta.class);
		System.out.println(xrs.listMethods());
```

* To expose a simple XML-RPC HTTP server, you don't need no external dependencies

```java
	@XRMethod(value = "example.helloWorld", help = "Returns 'Helo ' + argument")
	public static String hello(Object s) {
		return "Hello '" + s + "'";
	}

	public static void main(String[] args) throws Exception {
		NanoServer server = new NanoServer("http://localhost:7070");
		server.add(XR.server(HelloWorldServer.class));
		server.start();
	}
```

* For production you will need to expose `com.nmote.xr.Endpoint` via one or  more 
  `com.nmote.xr.XRServlet` instances. Endpoints are passed in a servlet context.
  (See the source for more info)
  
* Package `com.nmote.xr.spring` has support classes for exposing clients and
  server using springframework contexts.
  

Author contact and support
--------------------------

For further information please contact
Vjekoslav Nesek (vnesek@nmote.com)
