package org.training.campus.networking.webserver.exception;

public class ResourceAccessException extends CommunicationException {

	public ResourceAccessException(String msg) {
		super(msg);
	}

	public ResourceAccessException(Exception e) {
		super(e);
	}

}
