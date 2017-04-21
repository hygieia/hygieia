package com.capitalone.dashboard.model;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="user_info")
@CompoundIndexes({
    @CompoundIndex(name = "username_authType", def = "{'username' : 1, 'authType': 1}")
})
public class UserInfo {
	
	@Id
	private ObjectId id;
	private String username;
	private Collection<UserRole> authorities;
	private AuthType authType;
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Collection<UserRole> getAuthorities() {
		
		return authorities;
	}
	
	public void setAuthorities(Collection<UserRole> authorities) {
		this.authorities = authorities;
	}

	public AuthType getAuthType() {
		return authType;
	}

	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}
	
}
