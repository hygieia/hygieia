package com.capitalone.dashboard.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Owner {

	private String username;
	private AuthType authType;
	
	public Owner() {}
	
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		
		Owner rhs = (Owner) obj;
		
		return new EqualsBuilder().append(username, rhs.username).append(authType, rhs.authType).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(username).append(authType).toHashCode();
	}
	

	
}
