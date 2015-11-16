package com.capitalone.dashboard.model;

public class CloudStorageObject {
	private String name;
	private long creationDate;
	private long updateDate;
	private String encryption;
	private long size;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	public long getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(long updateDate) {
		this.updateDate = updateDate;
	}
	public String getEncryption() {
		return encryption;
	}
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}

	
}
