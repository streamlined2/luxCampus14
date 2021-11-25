package org.training.campus.networking.webserver.http.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteArrayRequestMessageBody implements RequestMessageBody {

	private final ByteArrayOutputStream outputStream;

	public ByteArrayRequestMessageBody() {
		outputStream = new ByteArrayOutputStream();
	}

	@Override
	public void close() throws Exception {
		if (outputStream != null) {
			outputStream.close();
		}
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	@Override
	public byte[] toByteArray() {
		return outputStream.toByteArray();
	}

}
