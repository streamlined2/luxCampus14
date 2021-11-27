package org.training.campus.networking.webserver.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.Charset;

public class Sink implements AutoCloseable {
	private final OutputStream outputStream;
	private final Writer writer;

	public Sink(Socket socket, int bufferSize, Charset charset) throws IOException {
		outputStream = new BufferedOutputStream(socket.getOutputStream(), bufferSize); 
		writer = new OutputStreamWriter(outputStream, charset);
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public Writer getWriter() {
		return writer;
	}

	public void flush() throws IOException {
		writer.flush();
		outputStream.flush();
	}

	@Override
	public void close() {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
		}
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
		}
	}

}
