package com.capitalone.dashboard.model;

public class Review {
    private String body;
    private String state;
    private String author;
    private String authorLDAPDN;
    private long createdAt;
    private long updatedAt;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorLDAPDN() {
        return authorLDAPDN;
    }

    public void setAuthorLDAPDN(String authorLDAPDN) {
        this.authorLDAPDN = authorLDAPDN;
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
}
