package com.capitalone.dashboard.response;

public class CloudVolumeAggregatedResponse {
    private int nonEncryptedCount;
    private int noAttachmentCount;
    private int noAccountCount;
    private int nonTaggedCount;
    private int totalCount;
    private long lastUpdated;

    public int getNonEncryptedCount() {
        return nonEncryptedCount;
    }

    public void setNonEncryptedCount(int nonEncryptedCount) {
        this.nonEncryptedCount = nonEncryptedCount;
    }

    public int getNoAttachmentCount() {
        return noAttachmentCount;
    }

    public void setNoAttachmentCount(int noAttachmentCount) {
        this.noAttachmentCount = noAttachmentCount;
    }

    public int getNoAccountCount() {
        return noAccountCount;
    }

    public void setNoAccountCount(int noAccountCount) {
        this.noAccountCount = noAccountCount;
    }

    public int getNonTaggedCount() {
        return nonTaggedCount;
    }

    public void setNonTaggedCount(int nonTaggedCount) {
        this.nonTaggedCount = nonTaggedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}