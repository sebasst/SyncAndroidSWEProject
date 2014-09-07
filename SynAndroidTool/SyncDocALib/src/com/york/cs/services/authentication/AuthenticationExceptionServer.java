package com.york.cs.services.authentication;


public class AuthenticationExceptionServer extends Exception {

	
	private static final long serialVersionUID = 1L;

	int status;
	
	public AuthenticationExceptionServer(int status,String detailMessage) {
		super("code: "+status+" Server Message"+detailMessage);
		this.status=status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	

}
