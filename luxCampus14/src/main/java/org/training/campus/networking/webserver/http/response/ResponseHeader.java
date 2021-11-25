package org.training.campus.networking.webserver.http.response;

import java.util.Objects;

public record ResponseHeader(String name, String value) implements Comparable<ResponseHeader> {

	public ResponseHeader {
		Objects.requireNonNull(name, "header name can't be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("header name can't be blank");
		}
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
		return String.format("%s=%s", name, value);
	}

	@Override
	public int compareTo(ResponseHeader o) {
		return name.compareToIgnoreCase(o.name);
	}

}
