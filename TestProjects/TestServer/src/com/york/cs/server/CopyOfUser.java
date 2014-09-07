package com.york.cs.server;

import java.util.ArrayList;
import java.util.List;


public class CopyOfUser {

	
	String id;
	String name;

	List<String> admin_channels;

	String email;

	String password;

	public CopyOfUser() {
		super();
		// TODO Auto-generated constructor stub
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

}
