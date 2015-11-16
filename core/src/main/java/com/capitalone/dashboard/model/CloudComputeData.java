package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class CloudComputeData {
	private int nonEncryptedCount;
	private int nonTaggedCount;
	private int stoppedCount;
	private int ageWarning;
	private int ageExpired;
	private int ageGood;
	private int cpuLow;
	private int cpuMid;
	private int cpuHigh;
	private int totalInstanceCount;
	private double estimatedCharge;
	private String currency = "USD";
	private long lastUpdated;
	private ArrayList<CloudComputeInstanceData> instanceDetailList;

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

	public int getCpuLow() {
		return cpuLow;
	}

	public void setCpuLow(int cpuLow) {
		this.cpuLow = cpuLow;
	}

	public int getCpuMid() {
		return cpuMid;
	}

	public void setCpuMid(int cpuMid) {
		this.cpuMid = cpuMid;
	}

	public int getCpuHigh() {
		return cpuHigh;
	}

	public void setCpuHigh(int cpuHigh) {
		this.cpuHigh = cpuHigh;
	}

	public void setDetailList(
			ArrayList<CloudComputeInstanceData> instanceDetailList) {
		this.instanceDetailList = instanceDetailList;
	}

	public List<CloudComputeInstanceData> getDetailList() {
		return instanceDetailList;
	}

	public int getTotalInstanceCount() {
		return totalInstanceCount;
	}

	public void setTotalInstanceCount(int totalInstanceCount) {
		this.totalInstanceCount = totalInstanceCount;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
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
}