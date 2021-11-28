package org.training.campus.networking.webserver.http;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;

public enum HttpContentType {

	TEXT_HTML("text/html") {
		@Override
		public String getEncoding(Charset charset) {
			return String.format("%s; charset=%s", getEncoding(), charset.name());
		}
	},
	TEXT_CSS("text/css"), TEXT_PLAIN("text/plain") {
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

	public static Optional<HttpContentType> getByFileType(String fileType) {
		Objects.requireNonNull(fileType, "provide non-null file type parameter");
		if (fileType.isBlank())
			throw new IllegalArgumentException("file type shouldn't be blank");
		for (var type : values()) {
			int index = type.getEncoding().indexOf("/");
			if (index != -1) {
				String subPart = type.getEncoding().substring(index + 1);
				if (subPart.equalsIgnoreCase(fileType)) {
					return Optional.of(type);
				}
			}
		}
		return Optional.empty();
	}

	public static Optional<HttpContentType> getByUrl(String url) {
		Objects.requireNonNull(url, "provide non-null url parameter");
		int index = url.lastIndexOf('.');
		if (index != -1) {
			String fileType = url.substring(index + 1);
			if (!fileType.isBlank()) {
				return getByFileType(fileType);
			}
		}
		return Optional.empty();
	}

}
