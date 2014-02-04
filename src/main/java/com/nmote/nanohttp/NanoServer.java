/*
 * Copyright (c) Nmote Ltd. 2003-2014. All rights reserved. 
 * See LICENSE doc in a root of project folder for additional information.
 */

package com.nmote.nanohttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NanoServer {

	private class Acceptor implements Runnable {
		public void run() {
			try {
				serverSocket.setSoTimeout(500);
			} catch (SocketException e) {
				log(e);
			}
			while (!executor.isShutdown()) {
				try {
					executor.execute(new Handler(serverSocket.accept()));
				} catch (SocketTimeoutException e) {
					// Loop again
				} catch (IOException e) {
					log(e);
				}
			}
		}
	}

	private class Handler implements Runnable, NanoRequest {

		private Handler(Socket socket) {
			this.socket = socket;
			this.requestHeaders = new HashMap<String, String>();
			this.responseHeaders = new HashMap<String, String>();
			
			responseHeaders.put("server", "Nmote NanoHTTP/1.0");
		}

		public InputStream getInputStream() throws IOException {
			assertProcessing();
			return socket.getInputStream();
		}

		public String getMethod() {
			return method;
		}

		public OutputStream getOutputStream() throws IOException {
			assertProcessing();
			OutputStream out = socket.getOutputStream();
			if (!responseCommited) {
				responseCommited = true;
				StringBuilder b = new StringBuilder(200);
				b.append("HTTP/1.1 ");
				b.append(response);
				b.append("\r\n");
				for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
					b.append(entry.getKey());
					b.append(": ");
					b.append(entry.getValue());
					b.append("\r\n");					
				}
				b.append("connection: close\r\n");
				b.append("\r\n");
				out.write(b.toString().getBytes("iso-8859-1"));
			}
			return out;
		}

		public Map<String, String> getRequestHeaders() {
			return requestHeaders;
		}

		public String getRequestPath() {
			return requestPath;
		}

		public Map<String, String> getResponseHeaders() {
			assertProcessing();
			return responseHeaders;
		}

		public void response(String response) {
			assertProcessing();
			if (responseCommited) {
				throw new IllegalStateException("HTTP response already generated");
			}
			this.response = response;
		}

		public void run() {
			try {
				InputStream in = socket.getInputStream();
				readHeaders(in);

				if (servlets != null) {
					for (NanoServlet nanoServlet : servlets) {
						if (nanoServlet.canProcess(this)) {
							processing = true;
							nanoServlet.process(this);
							break;
						}
					}
				}

				OutputStream out = getOutputStream();
				out.flush();
				out.close();
			} catch (IOException e) {
				log(e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log(e);
				}
			}
		}

		private void assertProcessing() {
			if (!processing) { throw new IllegalStateException(); }
		}

		private void readHeaders(InputStream in) throws IOException {
			char[] buffer = new char[256];
			String line = readLine(in, buffer);

			// Parse first line
			int space = line.indexOf(' ');
			method = line.substring(space).toUpperCase();
			line = line.substring(space + 1);
			space = line.indexOf(' ');
			requestPath = line.substring(0, space).trim();

			// Parse headers
			for (;;) {
				line = readLine(in, buffer);
				if (line == null) break;
				line = line.trim();
				if (line.length() > 0) {
					// Parse header
					int colon = line.indexOf(':');
					if (colon != -1) {
						String headerName = line.substring(0, colon).toLowerCase();
						String headerValue = line.substring(colon + 1).trim();
						requestHeaders.put(headerName, headerValue);
					}
				} else {
					break;
				}
			}
		}

		private String method;
		private boolean processing;
		private final Map<String, String> requestHeaders;
		private String requestPath;
		private String response = "404 Not Found";
		private boolean responseCommited;
		private final Map<String, String> responseHeaders;
		private final Socket socket;
	}

	public NanoServer() throws IOException {
		this(7070);
	}

	public NanoServer(int port) throws IOException {
		super();
		this.serverSocket = new ServerSocket(port, 20);
	}

	public NanoServer(String url) throws IOException {
		super();
		URL serverUrl = new URL(url);
		if (!"http".equals(serverUrl.getProtocol())) { throw new IllegalArgumentException("unsupported protocol: "
				+ url); }
		this.serverSocket = new ServerSocket(serverUrl.getPort(), 20, InetAddress.getByName(serverUrl.getHost()));
	}

	public void add(NanoServlet nanoServlet) {
		if (servlets == null) {
			servlets = new ArrayList<NanoServlet>();
		}
		servlets.add(nanoServlet);
	}

	public List<NanoServlet> getServlets() {
		return servlets;
	}

	public void remove(NanoServlet nanoServlet) {
		if (servlets != null) {
			servlets.remove(nanoServlet);
		}
	}

	public void setServlets(List<NanoServlet> servlets) {
		this.servlets = servlets;
	}

	public void start() {
		start(Executors.newCachedThreadPool());
	}

	public void start(ExecutorService executor) {
		if (this.executor != null) { throw new IllegalStateException("already in use"); }
		if (executor == null) { throw new NullPointerException("executor == null"); }

		this.executor = executor;
		this.executor.execute(new Acceptor());
	}

	public void stop() throws InterruptedException, IOException {
		stop(2, TimeUnit.SECONDS);
	}

	public void stop(long timeout, TimeUnit timeUnit) throws InterruptedException, IOException {
		if (executor == null) { throw new IllegalStateException("not started"); }
		executor.shutdown();
		executor.awaitTermination(timeout, timeUnit);
		serverSocket.close();
	}

	protected void log(Throwable t) {
		t.printStackTrace();
	}

	private ExecutorService executor;
	private ServerSocket serverSocket;
	private List<NanoServlet> servlets;

	private static String readLine(InputStream in, char[] buffer) throws IOException {
		int room = buffer.length;
		int offset = 0;
		int c = in.read();

		loop: while (offset < room) {
			switch (c) {
			case -1:
			case '\n':
				break loop;

			case '\r':
				int c2 = in.read();
				if ((c2 != '\n') && (c2 != -1)) { throw new IOException("expected \\n"); }
				break loop;

			default:
				buffer[offset++] = (char) c;
			}

			c = in.read();
		}
		if ((c == -1) && (offset == 0)) { return null; }
		return String.copyValueOf(buffer, 0, offset);
	}
}
