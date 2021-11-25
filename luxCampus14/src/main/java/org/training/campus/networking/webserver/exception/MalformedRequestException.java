package org.training.campus.networking.webserver.exception;

public class MalformedRequestException extends CommunicationException {

	public MalformedRequestException(Exception e) {
		super(e);
	}

	public MalformedRequestException(String reason) {
		super(reason);
	}

}
