package com.capitalone.dashboard.model;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="user_info")
public class UserInfo {

	private ObjectId id;
	private String username;
	private Collection<UserRole> authorities;
	
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
	
}
