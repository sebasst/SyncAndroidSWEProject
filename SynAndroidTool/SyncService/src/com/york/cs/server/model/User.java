package com.york.cs.server.model;



import java.io.Serializable;
import java.util.List;

/**
 * 
 * Class model to send data to appserver for authentication
 * 
 * @author sebas
 * 
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;

	private List<String> admin_channels;

	private String email;

	private String password;

	public String sessionToken;

	

	public User() {
		super();
	}

	public User(String id, String name, String email, String password) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAdmin_channels() {
		return admin_channels;
	}

	public void setAdmin_channels(List<String> admin_channels) {
		this.admin_channels = admin_channels;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}


	/**
	 * @return true if account created successfully in server and device, false
	 *         if not
	 * @throws Exception
	 */
	public boolean userSignUp() throws Exception {
		return false;
	}
	
	public void print(){
		System.out.println("id: "+getId());
		System.out.println("name: "+getName());
		System.out.println("email: "+getEmail());
		System.out.println("pass: "+getPassword());
		System.out.println("token: "+getSessionToken());
	
		
	}

}
