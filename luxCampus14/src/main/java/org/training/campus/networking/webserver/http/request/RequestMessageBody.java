package org.training.campus.networking.webserver.http.request;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestMessageBody extends AutoCloseable {

	OutputStream getOutputStream() throws IOException;
	byte[] toByteArray();

}
