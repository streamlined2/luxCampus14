package org.training.campus.networking.webserver.http.response;

import java.util.Objects;

public record ResponseHeader(String name, String value) implements Comparable<ResponseHeader> {

	public enum HeaderType {
		CONTENT_TYPE("Content-Type"), CONTENT_ENCODING("Content-Encoding"), CONTENT_LANGUAGE("Content-Language"),
		CONTENT_LENGTH("Content-Length"), EXPIRES("Expires"), LAST_MODIFIED("Last-Modified"), SERVER("Server"),
		TRANSFER_ENCODING("Transfer-Encoding"), CONNECTION("Connection"), DATE("Date"), ACCEPT_RANGES("Accept-Ranges");

		private String name;

		private HeaderType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static HeaderType getType(String name) {
			for (var type : values()) {
				if (type.name.equals(name)) {
					return type;
				}
			}
			return null;
		}

	}

	public ResponseHeader {
		Objects.requireNonNull(name, "header name can't be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("header name can't be blank");
		}
	}

	public ResponseHeader(HeaderType headerType, String value) {
		this(headerType.name, value);
	}

	public boolean hasContentSize() {
		return HeaderType.getType(name) == HeaderType.CONTENT_LENGTH;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ResponseHeader h) {
			return compareTo(h) == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", name, value);
	}

	@Override
	public int compareTo(ResponseHeader o) {
		return name.compareToIgnoreCase(o.name);
	}

}
