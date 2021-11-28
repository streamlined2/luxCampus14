package org.training.campus.networking.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.training.campus.networking.webserver.exception.CommunicationException;
import org.training.campus.networking.webserver.exception.MalformedRequestException;
import org.training.campus.networking.webserver.exception.MalformedResourceUrlException;
import org.training.campus.networking.webserver.exception.ResourceAccessException;
import org.training.campus.networking.webserver.exception.ResourceNotFoundException;
import org.training.campus.networking.webserver.exception.ResourceTooLargeException;
import org.training.campus.networking.webserver.exception.ResponseFailedException;
import org.training.campus.networking.webserver.http.HttpContentType;
import org.training.campus.networking.webserver.http.HttpParameters;
import org.training.campus.networking.webserver.http.request.HttpRequest;
import org.training.campus.networking.webserver.http.response.ByteArrayResponseMessageBody;
import org.training.campus.networking.webserver.http.response.HttpResponse;
import org.training.campus.networking.webserver.http.response.HttpStatusCode;
import org.training.campus.networking.webserver.http.response.ResponseHeader;
import org.training.campus.networking.webserver.io.Sink;
import org.training.campus.networking.webserver.io.Source;

public class Server extends Worker {
	private static final int ACCEPT_TIMEOUT = 1000;
	private static final int BUFFER_SIZE = 1024;
	private static final String SERVER_NAME = "TEST-SERVER";

	private final ServerSocket serverSocket;
	private final RequestParser requestParser;
	private final ResponseWriter responseWriter;
	private final ResourceReader resourceReader;
	private final ThreadGroup handlerGroup;
	private final String context;
	private int handlerCount;
	private Charset charset;

	public Server(int ordinal, int port, RequestParser requestParser, ResponseWriter responseWriter,
			ResourceReader resourceReader, String context) throws IOException {
		this.requestParser = requestParser;
		this.responseWriter = responseWriter;
		this.resourceReader = resourceReader;
		this.context = context;
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(ACCEPT_TIMEOUT);
		handlerGroup = new ThreadGroup(String.valueOf(ordinal));
		handlerCount = 0;
		charset = StandardCharsets.UTF_8;
	}

	@Override
	public void run() {
		try (serverSocket) {
			while (!(isDone() || Thread.interrupted())) {
				handleConnection();
			}
		} catch (IOException e) {
			if (!isDone()) {
				e.printStackTrace();
				throw new CommunicationException(e);
			}
		}
	}

	private void handleConnection() throws IOException {
		try {
			new ConnectionHandler(serverSocket.accept(), handlerCount++).start();
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

	private class ConnectionHandler extends Thread {

		private final Socket socket;

		private ConnectionHandler(Socket socket, int no) {
			super(handlerGroup, String.valueOf(no));
			this.socket = socket;
		}

		@Override
		public void run() {
			try (socket;
					Source source = new Source(socket, BUFFER_SIZE, charset);
					Sink sink = new Sink(socket, BUFFER_SIZE, charset)) {
				while (!(isDone() || Thread.interrupted())) {
					if (source.ready()) {
						handleRequest(source, sink);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new CommunicationException(e);
			}
		}

		private void handleRequest(Source source, Sink sink) throws IOException {
			try {
				HttpRequest request = requestParser.parse(source);
				responseWriter.send(sink, requestHandledResponse(request.getUrl()));
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
				responseWriter.send(sink, resourceNotFoundResponse(e));
			} catch (ResourceTooLargeException e) {
				e.printStackTrace();
				responseWriter.send(sink, resourceTooLargeResponse(e));
			} catch (MalformedResourceUrlException e) {
				e.printStackTrace();
				responseWriter.send(sink, malformedResourceUrlResponse(e));
			} catch (ResourceAccessException e) {
				e.printStackTrace();
				responseWriter.send(sink, resourceAccessResponse(e));
			} catch (MalformedRequestException e) {
				e.printStackTrace();
				responseWriter.send(sink, malformedRequestResponse(e));
			} catch (ResponseFailedException e) {
				e.printStackTrace();
				responseWriter.send(sink, responseFailResponse(e));
			} catch (CommunicationException e) {
				e.printStackTrace();
				responseWriter.send(sink, communicationErrorResponse(e));
			}
		}

		private HttpResponse requestHandledResponse(String url) throws IOException {
			ByteBuffer content = resourceReader.readResource(url, context);
			return createResponse(content, HttpContentType.getByUrl(url), HttpStatusCode.OK);
		}

		private HttpResponse communicationErrorResponse(CommunicationException e) {
			return createResponse(e.getLocalizedMessage(), HttpContentType.TEXT_HTML.getEncoding(charset),
					HttpStatusCode.SERVICE_UNAVAILABLE);
		}

		private HttpResponse responseFailResponse(ResponseFailedException e) {
			return createResponse(e.getLocalizedMessage(), HttpContentType.TEXT_HTML.getEncoding(charset),
					HttpStatusCode.INTERNAL_SERVER_ERROR);
		}

		private HttpResponse malformedRequestResponse(MalformedRequestException e) {
			return createResponse(e.getLocalizedMessage(), HttpContentType.TEXT_HTML.getEncoding(charset),
					HttpStatusCode.BAD_REQUEST);
		}

		private HttpResponse resourceTooLargeResponse(ResourceTooLargeException e) {
			return createResponse(e.getLocalizedMessage(), HttpContentType.TEXT_HTML.getEncoding(charset),
					HttpStatusCode.REQUEST_ENTITY_TOO_LARGE);
		}

		private HttpResponse resourceNotFoundResponse(ResourceNotFoundException e) {
			return createResponse(e.getLocalizedMessage(), HttpContentType.TEXT_HTML.getEncoding(charset),
					HttpStatusCode.NOT_FOUND);
		}

		private HttpResponse malformedResourceUrlResponse(MalformedResourceUrlException e) {
			return createResponse(e.getLocalizedMessage(), HttpContentType.TEXT_HTML.getEncoding(charset),
					HttpStatusCode.BAD_REQUEST);
		}

		private HttpResponse resourceAccessResponse(ResourceAccessException e) {
			return createResponse(e.getLocalizedMessage(), HttpContentType.TEXT_HTML.getEncoding(charset),
					HttpStatusCode.INTERNAL_SERVER_ERROR);
		}

		private HttpResponse createResponse(ByteBuffer dataBuffer, Optional<HttpContentType> contentType,
				HttpStatusCode statusCode) {
			String replyType = HttpContentType.TEXT_HTML.getEncoding(charset);
			if (contentType.isPresent()) {
				replyType = contentType.get().getEncoding(charset);
			}
			return createResponse(dataBuffer, replyType, statusCode);
		}

		private HttpResponse createResponse(ByteBuffer dataBuffer, String replyType, HttpStatusCode statusCode) {
			byte[] byteData = new byte[dataBuffer.limit()];
			dataBuffer.get(byteData);
			return createResponse(byteData, replyType, statusCode);
		}

		private HttpResponse createResponse(String reply, String replyType, HttpStatusCode statusCode) {
			return createResponse(reply.getBytes(charset), replyType, statusCode);
		}

		private HttpResponse createResponse(byte[] data, String replyType, HttpStatusCode statusCode) {
			HttpResponse response = new HttpResponse(HttpParameters.PROTOCOL_VERSION, statusCode.getCode());
			response.setReason(statusCode.getReason());
			response.setMessageBody(new ByteArrayResponseMessageBody(data));
			response.addHeader(ResponseHeader.HeaderType.SERVER, SERVER_NAME);
			response.addHeader(ResponseHeader.HeaderType.CONTENT_TYPE, replyType);
			response.addHeader(ResponseHeader.HeaderType.CONTENT_LENGTH, String.valueOf(data.length));
			response.addHeader(ResponseHeader.HeaderType.CONNECTION, "keep-alive");
			response.addHeader(ResponseHeader.HeaderType.ACCEPT_RANGES, "bytes");
			response.addHeader(ResponseHeader.HeaderType.DATE,
					DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(response.getFormedTime()));
			response.addHeader(ResponseHeader.HeaderType.LAST_MODIFIED,
					DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(response.getFormedTime()));
			return response;
		}

	}

}
