package org.training.campus.networking.webserver.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Source implements AutoCloseable {
	private final InputStream inputStream;
	private final Scanner scanner;

	public Source(Socket socket, int bufferSize, Charset charset) throws IOException {
		inputStream = new BufferedInputStream(socket.getInputStream(), bufferSize);
		scanner = new Scanner(inputStream, charset);
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public Scanner getScanner() {
		return scanner;
	}

	@Override
	public void close() {
		try {
			if (scanner != null) {
				scanner.close();
			}
		} catch (Exception e) {
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception e) {
		}
	}

}
