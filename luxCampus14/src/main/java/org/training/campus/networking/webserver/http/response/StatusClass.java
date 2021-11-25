package org.training.campus.networking.webserver.http.response;

import org.training.campus.networking.webserver.exception.MalformedRequestException;

public enum StatusClass {

	INFORMATIONAL(100, 200), SUCCESSFUL(200, 300), REDIRECTION(300, 400), CLIENT_ERROR(400, 500),
	SERVER_ERROR(500, 600);

	private final int start;
	private final int finish;

	private StatusClass(int start, int finish) {
		this.start = start;
		this.finish = finish;
	}

	public static StatusClass getStatusClass(int code) {
		for (StatusClass statusClass : values()) {
			if (statusClass.start <= code && code < statusClass.finish) {
				return statusClass;
			}
		}
		throw new MalformedRequestException(String.format("wrong status code %d", code));
	}

}
