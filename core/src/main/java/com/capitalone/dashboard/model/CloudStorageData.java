
package com.capitalone.dashboard.model;

import java.util.List;


public class CloudStorageData extends BaseModel {
    private int nonEncryptedCount;
    private int nonTaggedCount;
    private int stoppedCount;
    private int ageWarning;
    private int ageExpired;
    private int ageGood;
    private long lastUpdated;
    private List<CloudStorageBucket> bucketList;

    public int getNonEncryptedCount() {
        return nonEncryptedCount;
    }

    public void setNonEncryptedCount(int nonEncryptedCount) {
        this.nonEncryptedCount = nonEncryptedCount;
    }

    public int getNonTaggedCount() {
        return nonTaggedCount;
    }

    public void setNonTaggedCount(int nonTaggedCount) {
        this.nonTaggedCount = nonTaggedCount;
    }

    public int getStoppedCount() {
        return stoppedCount;
    }

    public void setStoppedCount(int stoppedCount) {
        this.stoppedCount = stoppedCount;
    }

    public int getAgeWarning() {
        return ageWarning;
    }

    public void setAgeWarning(int ageWarning) {
        this.ageWarning = ageWarning;
    }

    public int getAgeExpired() {
        return ageExpired;
    }

    public void setAgeExpired(int ageExpired) {
        this.ageExpired = ageExpired;
    }

    public int getAgeGood() {
        return ageGood;
    }

    public void setAgeGood(int ageGood) {
        this.ageGood = ageGood;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<CloudStorageBucket> getBucketList() {
        return bucketList;
    }

    public void setBucketList(List<CloudStorageBucket> bucketList) {
        this.bucketList = bucketList;
    }
}