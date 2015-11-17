package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CloudComputeInstanceData;

import java.util.List;

public class AWSCloudComptuteStatistics {
	private List<CloudComputeInstanceData> rawData;
	private int ageWarningCount = 0;
	private int ageExpireCount = 0;
	private int ageGoodCount = 0;
	private int cpuHighCount = 0;
	private int cpuMidCount = 0;
	private int cpuLowCount = 0;
	private int unEcryptedComputeCount = 0;
	private int unTaggedCount = 0;
	private int stoppedCount = 0;
	private int totalCount = 0;
		
	private static final int HIGH_AGE = 60;
	private static final int MID_AGE = 45;
	private static final int LOW_AGE = 15;

	private static final int HIGH_CPU = 80;
	private static final int MID_CPU = 50;
	private static final int LOW_CPU = 25;
	
	public AWSCloudComptuteStatistics(List<CloudComputeInstanceData> dataCompute) {
		rawData = dataCompute;
		runStat();
	}

	private void runStat() {
		for (CloudComputeInstanceData rd : rawData) {
			totalCount = totalCount + 1;
			
			if (!rd.isEncrypted()) { unEcryptedComputeCount = unEcryptedComputeCount + 1;}
			if (rd.isStopped()) {stoppedCount = stoppedCount + 1;}
			if (!rd.isTagged()) {unTaggedCount = unTaggedCount + 1;}
			if (rd.getAge() >= HIGH_AGE) {ageExpireCount = ageExpireCount + 1;}
			if ((rd.getAge() < HIGH_AGE) && (rd.getAge() >= MID_AGE)) {ageWarningCount = ageWarningCount + 1;}
			if (rd.getAge() < MID_AGE) {ageGoodCount = ageGoodCount + 1;}
			if (rd.getCpuUtilization() >= HIGH_CPU) {cpuHighCount = cpuHighCount + 1;}
			if ((rd.getCpuUtilization() < HIGH_CPU) && (rd.getCpuUtilization() >= MID_CPU)) {cpuMidCount = cpuMidCount + 1;}
			if (rd.getCpuUtilization() < MID_CPU) {cpuLowCount = cpuLowCount + 1;}
			
		}

	}

	public int getAgeWarningCount() {
		return ageWarningCount;
	}

	public int getAgeExpireCount() {
		return ageExpireCount;
	}

	public int getAgeGoodCount() {
		return ageGoodCount;
	}

	public int getCpuHighCount() {
		return cpuHighCount;
	}

	public int getCpuMidCount() {
		return cpuMidCount;
	}

	public int getCpuLowCount() {
		return cpuLowCount;
	}

	public int getUnEcryptedCount() {
		return unEcryptedComputeCount;
	}

	public int getUnTaggedCount() {
		return unTaggedCount;
	}

	public int getStoppedCount() {
		return stoppedCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public List<CloudComputeInstanceData> getRawData() {
		return rawData;
	}

	public void setRawData(List<CloudComputeInstanceData> rawData) {
		this.rawData = rawData;
	}

	public int getUnEcryptedComputeCount() {
		return unEcryptedComputeCount;
	}

	public void setUnEcryptedComputeCount(int unEcryptedComputeCount) {
		this.unEcryptedComputeCount = unEcryptedComputeCount;
	}


	public void setAgeWarningCount(int ageWarningCount) {
		this.ageWarningCount = ageWarningCount;
	}

	public void setAgeExpireCount(int ageExpireCount) {
		this.ageExpireCount = ageExpireCount;
	}

	public void setAgeGoodCount(int ageGoodCount) {
		this.ageGoodCount = ageGoodCount;
	}

	public void setCpuHighCount(int cpuHighCount) {
		this.cpuHighCount = cpuHighCount;
	}

	public void setCpuMidCount(int cpuMidCount) {
		this.cpuMidCount = cpuMidCount;
	}

	public void setCpuLowCount(int cpuLowCount) {
		this.cpuLowCount = cpuLowCount;
	}

	public void setUnTaggedCount(int unTaggedCount) {
		this.unTaggedCount = unTaggedCount;
	}

	public void setStoppedCount(int stoppedCount) {
		this.stoppedCount = stoppedCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
