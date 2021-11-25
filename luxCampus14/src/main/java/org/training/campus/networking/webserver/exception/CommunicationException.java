package org.training.campus.networking.webserver.exception;

public class CommunicationException extends RuntimeException {

	public CommunicationException(String message) {
		super(message);
	}

	public CommunicationException(Exception e) {
		super(e);
	}

}
