package com.capitalone.dashboard.model.pullrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestUser implements Serializable {
    private static final long serialVersionUID = 1218944632514585924L;
    private UserData user;
    private String role;

    public PullRequestUser() {
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
