package com.york.cs.server.exception;

public class NotAuthorizedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotAuthorizedException() {
		super();

	}

	public NotAuthorizedException(String arg0) {
		super(arg0);
	}
}
