package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;

public class CloudRequest {
    @NotNull
    private ObjectId id;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}


}
