package org.training.campus.networking.webserver.exception;

public class ResponseFailedException extends CommunicationException {

	public ResponseFailedException(Exception e) {
		super(e);
	}

	public ResponseFailedException(String reason) {
		super(reason);
	}

}
