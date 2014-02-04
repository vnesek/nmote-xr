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


Author contact and support
--------------------------

For further information please contact
Vjekoslav Nesek (vnesek@nmote.com)
