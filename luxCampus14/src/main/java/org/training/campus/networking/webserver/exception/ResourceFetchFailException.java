package org.training.campus.networking.webserver.exception;

public class ResourceFetchFailException extends ResourceAccessException {

	public ResourceFetchFailException(String msg) {
		super(msg);
	}

	public ResourceFetchFailException(Exception e) {
		super(e);
	}

}
