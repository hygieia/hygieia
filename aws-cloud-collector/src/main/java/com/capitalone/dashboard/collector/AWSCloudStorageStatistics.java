package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CloudStorageBucket;
import com.capitalone.dashboard.model.CloudStorageObject;

import java.util.List;

public class AWSCloudStorageStatistics {
	private List<CloudStorageBucket> rawData;

	private int bucketCount = 0;
	private int objectCount = 0;
	private long maxObjectSize = 0;
	private long minObjectSize = 0;
	private CloudStorageBucket biggestBucket;
	private CloudStorageObject biggestObject;
	private CloudStorageBucket smallestBucket;
	private CloudStorageObject smallestObject;

	private int totalEncryptedObject = 0;

	public AWSCloudStorageStatistics(List<CloudStorageBucket> dataStorage) {
		rawData = dataStorage;
		runStat();
	}

	private void runStat() {
		for (CloudStorageBucket rd : rawData) {
			bucketCount = bucketCount + 1;
		}

	}

	public List<CloudStorageBucket> getRawData() {
		return rawData;
	}

	public void setRawData(List<CloudStorageBucket> rawData) {
		this.rawData = rawData;
	}

	public int getBucketCount() {
		return bucketCount;
	}

	public void setBucketCount(int bucketCount) {
		this.bucketCount = bucketCount;
	}

	public int getObjectCount() {
		return objectCount;
	}

	public void setObjectCount(int objectCount) {
		this.objectCount = objectCount;
	}

	public long getMaxObjectSize() {
		return maxObjectSize;
	}

	public void setMaxObjectSize(long maxObjectSize) {
		this.maxObjectSize = maxObjectSize;
	}

	public long getMinObjectSize() {
		return minObjectSize;
	}

	public void setMinObjectSize(long minObjectSize) {
		this.minObjectSize = minObjectSize;
	}

	public CloudStorageBucket getBiggestBucket() {
		return biggestBucket;
	}

	public void setBiggestBucket(CloudStorageBucket biggestBucket) {
		this.biggestBucket = biggestBucket;
	}

	public CloudStorageObject getBiggestObject() {
		return biggestObject;
	}

	public void setBiggestObject(CloudStorageObject biggestObject) {
		this.biggestObject = biggestObject;
	}

	public CloudStorageBucket getSmallestBucket() {
		return smallestBucket;
	}

	public void setSmallestBucket(CloudStorageBucket smallestBucket) {
		this.smallestBucket = smallestBucket;
	}

	public CloudStorageObject getSmallestObject() {
		return smallestObject;
	}

	public void setSmallestObject(CloudStorageObject smallestObject) {
		this.smallestObject = smallestObject;
	}

	public int getTotalEncryptedObject() {
		return totalEncryptedObject;
	}

	public void setTotalEncryptedObject(int totalEncryptedObject) {
		this.totalEncryptedObject = totalEncryptedObject;
	}

}
