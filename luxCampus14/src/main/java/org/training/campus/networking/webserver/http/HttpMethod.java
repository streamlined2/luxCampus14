package org.training.campus.networking.webserver.http;

import java.util.Optional;

public enum HttpMethod {
	GET, POST;
	
	public static Optional<HttpMethod> getByName(String name) {
		for(HttpMethod method:values()) {
			if(method.name().equals(name)) {
				return Optional.of(method);
			}
		}
		return Optional.empty();
	}
}
