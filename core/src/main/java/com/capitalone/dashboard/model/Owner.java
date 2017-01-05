package com.capitalone.dashboard.model;

public class Owner {

	private String username;
	private AuthType authType;
	
	public Owner(String username, AuthType authType) {
		this.username = username;
		this.authType = authType;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public AuthType getAuthType() {
		return authType;
	}
	
	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}
	
}
