package com.capitalone.dashboard.model;

import java.util.List;

public class CloudStorageBucket {

	private String name;
	private String owner;
	private long creationDate;
	private List<CloudStorageObject> objects;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public long getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	public List<CloudStorageObject> getObjects() {
		return objects;
	}
	public void setObjects(List<CloudStorageObject> objects) {
		this.objects = objects;
	}
	
}
