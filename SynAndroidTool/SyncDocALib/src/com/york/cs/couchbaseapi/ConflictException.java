package com.york.cs.couchbaseapi;


public class ConflictException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConflictException() {
		super();
		
	}

	public ConflictException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		
	}

	public ConflictException(String detailMessage) {
		super(detailMessage);
		
	}

	public ConflictException(Throwable throwable) {
		super("Update conflict exception", throwable);
		
	}

	
}
