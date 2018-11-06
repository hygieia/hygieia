package com.capitalone.dashboard.model.webhook.github;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MergeEvent)) return false;
        MergeEvent that = (MergeEvent) o;
        return Objects.equals(getMergeSha(), that.getMergeSha()) &&
                Objects.equals(getGitRequestNumber(), that.getGitRequestNumber()) &&
                Objects.equals(getMergeAuthor(), that.getMergeAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMergeSha(), getGitRequestNumber(), getMergeAuthor());
    }
}
