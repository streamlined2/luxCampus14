package org.training.campus.networking.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import org.training.campus.networking.webserver.exception.CommunicationException;
import org.training.campus.networking.webserver.exception.MalformedRequestException;
import org.training.campus.networking.webserver.http.request.HttpRequest;
import org.training.campus.networking.webserver.http.response.ByteArrayResponseMessageBody;
import org.training.campus.networking.webserver.http.response.HttpResponse;
import org.training.campus.networking.webserver.http.response.ResponseHeader;
import org.training.campus.networking.webserver.io.Sink;
import org.training.campus.networking.webserver.io.Source;

public class Server extends Worker {
	private static final int ACCEPT_TIMEOUT = 1000;
	private static final int BUFFER_SIZE = 1024;
	private static final Charset CURRENT_CHARSET = StandardCharsets.UTF_8;
	private static final String SERVER_NAME = "Venus";

	private final ServerSocket serverSocket;
	private final RequestParser requestParser;
	private final ResponseWriter responseWriter;
	private final ThreadGroup handlerGroup;
	private int handlerCount;
	private Charset charset;

	public Server(int ordinal, int port, RequestParser requestParser, ResponseWriter responseWriter)
			throws IOException {
		this.requestParser = requestParser;
		this.responseWriter = responseWriter;
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(ACCEPT_TIMEOUT);
		handlerGroup = new ThreadGroup(String.valueOf(ordinal));
		handlerCount = 0;
		charset = CURRENT_CHARSET;
	}

	@Override
	public void run() {
		try (serverSocket) {
			while (!(isDone() || Thread.interrupted())) {
				handleRequest();
			}
		} catch (IOException e) {
			if (!isDone()) {
				e.printStackTrace();
				throw new CommunicationException(e);
			}
		}
	}

	private void handleRequest() throws IOException {
		try {
			new RequestHandler(serverSocket.accept(), handlerCount++).start();
		} catch (SocketTimeoutException e) {
			// let server check if thread should be interrupted and then continue waiting
			// for incoming connection
		}
	}

	private synchronized void closeSocket() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		super.cancel(mayInterruptIfRunning);
		closeSocket();
		handlerGroup.interrupt();
		return true;
	}

	private class RequestHandler extends Thread {
		private final Socket socket;

		private RequestHandler(Socket socket, int no) {
			super(handlerGroup, String.valueOf(no));
			this.socket = socket;
		}

		@Override
		public void run() {
			try (Source source = new Source(socket, BUFFER_SIZE, CURRENT_CHARSET);
					Sink sink = new Sink(socket, BUFFER_SIZE, CURRENT_CHARSET)) {
				while (!(isDone() || Thread.interrupted())) {
					if (source.ready()) {
						// TODO implement request handler
						try {
							HttpRequest request = requestParser.parse(source);
							HttpResponse response = new HttpResponse("HTTP/1.1", 200);
							response.setReason("OK");
							String reply = """
											<!DOCTYPE html>
											<html>
											<body>
											<h1>My First Heading</h1>
											<p>My first paragraph.</p>
											</body>
											</html>
									""";
							response.setMessageBody(new ByteArrayResponseMessageBody(reply.getBytes()));
							response.addHeader(ResponseHeader.HeaderType.SERVER, SERVER_NAME);
							response.addHeader(ResponseHeader.HeaderType.CONTENT_TYPE, "text/html; charset=UTF-8");
							response.addHeader(ResponseHeader.HeaderType.CONTENT_LENGTH,
									String.valueOf(reply.getBytes().length));
							response.addHeader(ResponseHeader.HeaderType.CONNECTION, "close");
							response.addHeader(ResponseHeader.HeaderType.ACCEPT_RANGES, "bytes");
							response.addHeader(ResponseHeader.HeaderType.DATE,
									DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(response.getFormedTime()));
							response.addHeader(ResponseHeader.HeaderType.LAST_MODIFIED,
									DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(response.getFormedTime()));
							responseWriter.send(sink, response);
							break;
						} catch (MalformedRequestException e) {
							// TODO form bad response status
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new CommunicationException(e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
