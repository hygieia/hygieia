package com.capitalone.dashboard.response;

public class CloudInstanceAggregatedResponse {
    private int nonTaggedCount;
    private int stoppedCount;
    private int expiredImageCount;
    private int ageAlert;
    private int ageError;
    private int ageGood;
    private int cpuLow;
    private int cpuAlert;
    private int cpuHigh;
    private int diskHigh;
    private int diskAlert;
    private int diskLow;
    private int networkHigh;
    private int networkAlert;
    private int networkLow;
    private int memoryHigh;
    private int memoryAlert;
    private int memoryLow;
    private int totalInstanceCount;
    private double estimatedCharge;
    private String currency = "USD";
    private long lastUpdated;

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

    public int getAgeAlert() {
        return ageAlert;
    }

    public void setAgeAlert(int ageAlert) {
        this.ageAlert = ageAlert;
    }

    public int getAgeError() {
        return ageError;
    }

    public void setAgeError(int ageError) {
        this.ageError = ageError;
    }

    public int getAgeGood() {
        return ageGood;
    }

    public void setAgeGood(int ageGood) {
        this.ageGood = ageGood;
    }

    public int getCpuLow() {
        return cpuLow;
    }

    public void setCpuLow(int cpuLow) {
        this.cpuLow = cpuLow;
    }

    public int getCpuAlert() {
        return cpuAlert;
    }

    public void setCpuAlert(int cpuAlert) {
        this.cpuAlert = cpuAlert;
    }

    public int getCpuHigh() {
        return cpuHigh;
    }

    public void setCpuHigh(int cpuHigh) {
        this.cpuHigh = cpuHigh;
    }

    public int getDiskHigh() {
        return diskHigh;
    }

    public void setDiskHigh(int diskHigh) {
        this.diskHigh = diskHigh;
    }

    public int getDiskAlert() {
        return diskAlert;
    }

    public void setDiskAlert(int diskAlert) {
        this.diskAlert = diskAlert;
    }

    public int getDiskLow() {
        return diskLow;
    }

    public void setDiskLow(int diskLow) {
        this.diskLow = diskLow;
    }

    public int getNetworkHigh() {
        return networkHigh;
    }

    public void setNetworkHigh(int networkHigh) {
        this.networkHigh = networkHigh;
    }

    public int getNetworkAlert() {
        return networkAlert;
    }

    public void setNetworkAlert(int networkAlert) {
        this.networkAlert = networkAlert;
    }

    public int getNetworkLow() {
        return networkLow;
    }

    public void setNetworkLow(int networkLow) {
        this.networkLow = networkLow;
    }

    public int getMemoryHigh() {
        return memoryHigh;
    }

    public void setMemoryHigh(int memoryHigh) {
        this.memoryHigh = memoryHigh;
    }

    public int getMemoryAlert() {
        return memoryAlert;
    }

    public void setMemoryAlert(int memoryAlert) {
        this.memoryAlert = memoryAlert;
    }

    public int getMemoryLow() {
        return memoryLow;
    }

    public void setMemoryLow(int memoryLow) {
        this.memoryLow = memoryLow;
    }

    public int getTotalInstanceCount() {
        return totalInstanceCount;
    }

    public void setTotalInstanceCount(int totalInstanceCount) {
        this.totalInstanceCount = totalInstanceCount;
    }

    public double getEstimatedCharge() {
        return estimatedCharge;
    }

    public void setEstimatedCharge(double estimatedCharge) {
        this.estimatedCharge = estimatedCharge;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getExpiredImageCount() {
        return expiredImageCount;
    }

    public void setExpiredImageCount(int expiredImageCount) {
        this.expiredImageCount = expiredImageCount;
    }
}