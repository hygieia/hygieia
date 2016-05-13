package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents an EC2 instance from AWS
 */
@Document(collection = "cloud_instance_history")
public class CloudInstanceHistory extends BaseModel{
    @Indexed
    private String accountNumber;
    @Indexed
    private long time;
    private int total;
    private int nonTagged;
    private int stopped;
    private int expiredImage;
    private double estimatedCharge;
    private double cpu;
    private double diskRead;
    private double diskWrite;
    private double networkIn;
    private double networkOut;
    private String currency = "USD";

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNonTagged() {
        return nonTagged;
    }

    public void setNonTagged(int nonTagged) {
        this.nonTagged = nonTagged;
    }

    public int getStopped() {
        return stopped;
    }

    public void setStopped(int stopped) {
        this.stopped = stopped;
    }

    public int getExpiredImage() {
        return expiredImage;
    }

    public void setExpiredImage(int expiredImage) {
        this.expiredImage = expiredImage;
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

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public double getDiskRead() {
        return diskRead;
    }

    public void setDiskRead(double diskRead) {
        this.diskRead = diskRead;
    }

    public double getNetworkIn() {
        return networkIn;
    }

    public void setNetworkIn(double networkIn) {
        this.networkIn = networkIn;
    }

    public double getDiskWrite() {
        return diskWrite;
    }

    public void setDiskWrite(double diskWrite) {
        this.diskWrite = diskWrite;
    }

    public double getNetworkOut() {
        return networkOut;
    }

    public void setNetworkOut(double networkOut) {
        this.networkOut = networkOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CloudInstanceHistory that = (CloudInstanceHistory) o;

        return time == that.time && accountNumber.equals(that.accountNumber);

    }

    @Override
    public int hashCode() {
        int result = accountNumber.hashCode();
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }
}
