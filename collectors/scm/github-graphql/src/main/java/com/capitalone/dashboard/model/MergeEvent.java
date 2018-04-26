package com.capitalone.dashboard.model;

public class MergeEvent {
    private String mergeSha;
    private String gitRequestNumber;
    private String mergeRef;
    private String mergeAuthor;
    private String mergeAuthorLDAPDN;
    private long mergedAt;

    public String getMergeSha() {
        return mergeSha;
    }

    public void setMergeSha(String mergeSha) {
        this.mergeSha = mergeSha;
    }

    public String getGitRequestNumber() {
        return gitRequestNumber;
    }

    public void setGitRequestNumber(String gitRequestNumber) {
        this.gitRequestNumber = gitRequestNumber;
    }

    public String getMergeRef() {
        return mergeRef;
    }

    public void setMergeRef(String mergeRef) {
        this.mergeRef = mergeRef;
    }

    public String getMergeAuthor() {
        return mergeAuthor;
    }

    public void setMergeAuthor(String mergeAuthor) {
        this.mergeAuthor = mergeAuthor;
    }

    public String getMergeAuthorLDAPDN() {
        return mergeAuthorLDAPDN;
    }

    public void setMergeAuthorLDAPDN(String mergeAuthorLDAPDN) {
        this.mergeAuthorLDAPDN = mergeAuthorLDAPDN;
    }

    public long getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(long mergedAt) {
        this.mergedAt = mergedAt;
    }
}
