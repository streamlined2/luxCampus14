package org.training.campus.networking.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

import org.training.campus.networking.webserver.exception.CommunicationException;

public class Server extends Worker {
	private static final int ACCEPT_TIMEOUT = 1000;
	private static final int BUFFER_SIZE = 1024;

	private final int port;
	private final Charset charset;
	private int handlerCount = 0;
	private ServerSocket serverSocket;
	private ThreadGroup threadGroup;

	public Server(int ordinal, int port, Charset charset) {
		this.port = port;
		this.charset = charset;
		threadGroup = new ThreadGroup(String.valueOf(ordinal));
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(ACCEPT_TIMEOUT);
			while (!(isDone() || Thread.interrupted())) {
				handleRequest();
			}
		} catch (IOException e) {
			if (!isDone()) {
				e.printStackTrace();
				throw new CommunicationException(e);
			}
		} finally {
			closeSocket();
		}
	}

	private void handleRequest() throws IOException {
		try {
			Socket socket = serverSocket.accept();
			new RequestHandler(socket, handlerCount++).start();
		} catch (SocketTimeoutException e) {
			// let server check if thread should be interrupted and then continue waiting
			// for incoming connection
		}
	}

	private synchronized void closeSocket() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		super.cancel(mayInterruptIfRunning);
		closeSocket();
		threadGroup.interrupt();
		return true;
	}

	private class RequestHandler extends Thread {
		private final Socket socket;
		private String author;
		private LocalDateTime startStamp;

		private RequestHandler(Socket socket, int no) {
			super(threadGroup, String.valueOf(no));
			this.socket = socket;
			this.startStamp = LocalDateTime.now();
		}

		@Override
		public void run() {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset),
					BUFFER_SIZE);
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream(), charset), BUFFER_SIZE)) {

				while (!(isDone() || Thread.interrupted())) {
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
