package org.training.campus.networking.webserver.exception;

public class MalformedResourceUrlException extends ResourceAccessException {

	public MalformedResourceUrlException(String msg) {
		super(msg);
	}

	public MalformedResourceUrlException(Exception e) {
		super(e);
	}

}
