package com.capitalone.dashboard.model;

import java.util.Collection;

import com.google.common.collect.Sets;

public class UserResponse {
    
    private String username;
    private Collection<UserRole> roles;
    
    public UserResponse() {
        roles = Sets.newHashSet();
    }
    
    public UserResponse(String username, Collection<UserRole> roles) {
        this.username = username;
        this.roles = roles;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Collection<UserRole> getRoles() {
        return roles;
    }
    
    public void setRoles(Collection<UserRole> roles) {
        this.roles = roles;
    }
    
}
