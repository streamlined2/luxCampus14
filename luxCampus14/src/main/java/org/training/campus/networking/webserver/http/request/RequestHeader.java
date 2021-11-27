package org.training.campus.networking.webserver.http.request;

import java.util.Objects;

public record RequestHeader(String name, String value) implements Comparable<RequestHeader> {

	public enum HeaderType {
		CONTENT_LENGTH("Content-Length"), TRANSFER_ENCODING("Transfer-Encoding");

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

	public RequestHeader {
		Objects.requireNonNull(name, "header name can't be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("header name can't be blank");
		}
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
		if (o instanceof RequestHeader h) {
			return compareTo(h) == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", name, value);
	}

	@Override
	public int compareTo(RequestHeader o) {
		return name.compareToIgnoreCase(o.name);
	}

}
