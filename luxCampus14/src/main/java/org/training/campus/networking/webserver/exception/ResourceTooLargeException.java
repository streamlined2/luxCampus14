package org.training.campus.networking.webserver.exception;

public class ResourceTooLargeException extends ResourceAccessException {

	public ResourceTooLargeException(String msg) {
		super(msg);
	}

	public ResourceTooLargeException(Exception e) {
		super(e);
	}

}
