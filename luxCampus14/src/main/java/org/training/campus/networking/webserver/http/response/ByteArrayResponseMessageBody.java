package org.training.campus.networking.webserver.http.response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteArrayResponseMessageBody implements ResponseMessageBody {

	private final byte[] byteData;
	private InputStream inputStream;

	public ByteArrayResponseMessageBody(byte[] byteData) {
		this.byteData = byteData;
	}

	public ByteArrayResponseMessageBody(ByteBuffer buffer) {
		byteData = new byte[buffer.limit()];
		buffer.get(byteData);
	}

	@Override
	public void close() throws Exception {
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (inputStream == null) {
			inputStream = new ByteArrayInputStream(byteData);
		}
		return inputStream;
	}

}
