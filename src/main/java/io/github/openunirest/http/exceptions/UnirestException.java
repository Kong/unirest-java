package io.github.openunirest.http.exceptions;

public class UnirestException extends RuntimeException {

	private static final long serialVersionUID = -3714840499934575734L;

	public UnirestException(Exception e) {
		super(e);
	}

	public UnirestException(String msg) {
		super(msg);
	}

	public UnirestException(Throwable ex) {
		super(ex);
	}
}
