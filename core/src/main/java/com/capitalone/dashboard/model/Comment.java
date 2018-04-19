package com.capitalone.dashboard.model;

public class Comment {
    private String user;
    private String userLDAPDN;
    private long createdAt;
    private long updatedAt;
    private String body;
    private String status;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserLDAPDN() {
        return userLDAPDN;
    }

    public void setUserLDAPDN(String userLDAPDN) {
        this.userLDAPDN = userLDAPDN;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
