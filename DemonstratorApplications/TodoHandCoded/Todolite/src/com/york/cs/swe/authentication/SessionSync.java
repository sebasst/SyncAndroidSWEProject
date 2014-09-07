package com.york.cs.swe.authentication;



/**
 * 
 * Class model of the object that the appserver sends to the app after authentication
 * @author sebas
 *
 */
public class SessionSync {

	public SessionSync() {
		super();

	}

	String session_id;
	String expires;
	String cookie_name;

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getCookie_name() {
		return cookie_name;
	}

	public void setCookie_name(String cookie_name) {
		this.cookie_name = cookie_name;
	}

	@Override
	public String toString() {

		return session_id + "- " + expires;
	}

}
