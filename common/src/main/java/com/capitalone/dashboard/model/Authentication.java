package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class serves as the model for storing credential used for login & Signup.
 */


@Document(collection="authentication")
public class Authentication extends BaseModel {
	
	
	@Indexed(unique = true)
	private String username;
	
	private String password;
	
	
	public Authentication(String username, String password)
	{
		this.username=username;
		this.password=password;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "Authentication [username=" + username + ", password=" + password + "]";
	}
}
