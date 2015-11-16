package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Cloud")
public class Cloud extends BaseModel {
	private ObjectId collectorItemId;
	private ObjectId componentId;
	private CloudComputeData compute;
	private CloudStorageData storage;
	public ObjectId getCollectorItemId() {
		return collectorItemId;
	}
	public void setCollectorItemId(ObjectId collectorItemId) {
		this.collectorItemId = collectorItemId;
	}
	public ObjectId getComponentId() {
		return componentId;
	}
	public void setComponentId(ObjectId componentId) {
		this.componentId = componentId;
	}
	public CloudComputeData getCompute() {
		return compute;
	}
	public void setCompute(CloudComputeData compute) {
		this.compute = compute;
	}
	public CloudStorageData getStorage() {
		return storage;
	}
	public void setStorage(CloudStorageData storage) {
		this.storage = storage;
	}
}
