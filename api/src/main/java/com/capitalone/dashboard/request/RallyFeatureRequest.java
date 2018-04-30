package com.capitalone.dashboard.request;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.RallyFeatureType;

public class RallyFeatureRequest {
	
	 private ObjectId componentId;
	 private String projectId;
	 private static final String ITERATION_ID = "iterationId";
	 private Map<String, Object> options = new HashMap<>();

	private RallyFeatureType type;
	 
	 public RallyFeatureType getType() {
	        return type;
	    }

	    public void setType(RallyFeatureType type) {
	        this.type = type;
	    }
	 
	 public ObjectId getComponentId() {
		return componentId;
	}


	public void setComponentId(ObjectId componentId) {
		this.componentId = componentId;
	}


	public String getProjectId() {
		return projectId;
	}


	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	
	 
	 public Map<String, Object> getOptions() {
			return options;
		}
	 
	 
	    public String getIterationId() {
	        return (String) getOptions().get(ITERATION_ID);
	    }

	    public void setIterationId(String id) {
	        getOptions().put(ITERATION_ID, id);
	    }

}
