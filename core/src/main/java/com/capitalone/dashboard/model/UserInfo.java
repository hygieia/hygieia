package com.capitalone.dashboard.model;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Sets;

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
	private String firstName;
	private String middleName;
	private String lastName;
	private String displayName;
	private String emailAddress;
	
	public UserInfo() {
	    authorities = Sets.newHashSet();
	}
	
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
}
