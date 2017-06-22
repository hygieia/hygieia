package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;

public class CaApmRequest {
    @NotNull
    private ObjectId componentId;
    private String moduleName;
    
    public ObjectId getComponentId() {
        return componentId;
    }
    public void setComponentId(ObjectId componentId) {
        this.componentId = componentId;
    }
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
  
}