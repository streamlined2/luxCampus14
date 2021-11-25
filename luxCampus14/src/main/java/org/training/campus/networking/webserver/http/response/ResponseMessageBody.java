package org.training.campus.networking.webserver.http.response;

import java.io.IOException;
import java.io.InputStream;

public interface ResponseMessageBody extends AutoCloseable {
	
	InputStream getInputStream() throws IOException;

}
