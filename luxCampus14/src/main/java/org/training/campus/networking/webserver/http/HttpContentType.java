package org.training.campus.networking.webserver.http;

import java.nio.charset.Charset;

public enum HttpContentType {

	TEXT_HTML("text/html") {
		@Override
		public String getEncoding(Charset charset) {
			return String.format("%s; charset=%s", getEncoding(), charset.name());
		}
	},
	TEXT_PLAIN("text/plain") {
		@Override
		public String getEncoding(Charset charset) {
			return String.format("%s; charset=%s", getEncoding(), charset.name());
		}
	},
	APPLICATION_OCTET_STREAM("application/octet-stream"), IMAGE_JPEG("image/jpeg"), VIDEO_MP4("video/mp4");

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
