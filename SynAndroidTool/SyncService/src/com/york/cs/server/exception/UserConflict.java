package com.york.cs.server.exception;

public class UserConflict extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UserConflict() {
		super();

	}

	public UserConflict(String arg0) {
		super(arg0);
	}
}
