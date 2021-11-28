package org.training.campus.networking.webserver.http.response;

import org.training.campus.networking.webserver.exception.MalformedRequestException;

public enum HttpStatusClass {

	INFORMATIONAL(100, 200), SUCCESSFUL(200, 300), REDIRECTION(300, 400), CLIENT_ERROR(400, 500),
	SERVER_ERROR(500, 600);

	private final int start;
	private final int finish;

	private HttpStatusClass(int start, int finish) {
		this.start = start;
		this.finish = finish;
	}

	public static HttpStatusClass getStatusClass(int code) {
		for (HttpStatusClass statusClass : values()) {
			if (statusClass.start <= code && code < statusClass.finish) {
				return statusClass;
			}
		}
		throw new MalformedRequestException(String.format("wrong status code %d", code));
	}

}
