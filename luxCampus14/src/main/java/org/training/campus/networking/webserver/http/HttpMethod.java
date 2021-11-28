package org.training.campus.networking.webserver.http;

import java.util.Optional;

public enum HttpMethod {

	GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS;

	public static Optional<HttpMethod> getByName(String name) {
		for (var method : values()) {
			if (method.name().equals(name)) {
				return Optional.of(method);
			}
		}
		return Optional.empty();
	}
}
