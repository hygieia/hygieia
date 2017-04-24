package com.capitalone.dashboard.request;

import java.util.Collection;

import com.capitalone.dashboard.model.UserRole;

public class AdminRoleRequest {
    
    private String username;
    private Collection<UserRole> roles;

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
