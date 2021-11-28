package org.training.campus.networking.webserver.http;

import java.nio.charset.Charset;

public enum HttpContentType {

	TEXT_HTML("text/html") {
		@Override
		public String getEncoding(Charset charset) {
			return String.format("%s; charset=%s", getEncoding(), charset.name());
		}
	};

	private final String encoding;

	private HttpContentType(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getEncoding(Charset charset) {
		return getEncoding();
	}

}
