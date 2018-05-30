package com.capitalone.dashboard.model;

public class CommitStatus {
    private String state;
    private String context;
    private String author;
    private String authorLDAPDN;
    private String description;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
