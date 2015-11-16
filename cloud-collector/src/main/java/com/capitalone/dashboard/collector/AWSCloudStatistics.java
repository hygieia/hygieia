package com.capitalone.dashboard.collector;

import java.util.Iterator;
import java.util.List;

import com.capitalone.dashboard.model.CloudComputeInstanceData;

public class AWSCloudStatistics {
	private List<CloudComputeInstanceData> rawData;
	private int ageWarningCount = 0;
	private int ageExpireCount = 0;
	private int ageGoodCount = 0;
	private int cpuHighCount = 0;
	private int cpuMidCount = 0;
	private int cpuLowCount = 0;
	private int unEcryptedCount = 0;
	private int unTaggedCount = 0;
	private int stoppedCount = 0;
	private int totalCount = 0;

	private static final int HIGH_AGE = 60;
	private static final int MID_AGE = 45;
	private static final int LOW_AGE = 15;

	private static final int HIGH_CPU = 80;
	private static final int MID_CPU = 50;
	private static final int LOW_CPU = 25;

	public AWSCloudStatistics(List<CloudComputeInstanceData> data) {
		rawData = data;
		runStat();
	}

	private void runStat() {
		for (CloudComputeInstanceData rd : rawData) {
			totalCount = totalCount + 1;

			if (!rd.isEncrypted()) { unEcryptedCount = unEcryptedCount + 1;}
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
		return unEcryptedCount;
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

}
