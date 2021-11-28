package org.training.campus.networking.webserver.exception;

public class ResourceNotFoundException extends ResourceAccessException {

	public ResourceNotFoundException(String msg) {
		super(msg);
	}

	public ResourceNotFoundException(Exception e) {
		super(e);
	}

}
