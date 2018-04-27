package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="rally_burndown")

public class RallyBurnDownData extends BaseModel{
	
	public static final String ITERATION_TO_DO_HOURS = "iterationToDoHours";
	public static final String ACCEPTED_POINTS = "acceptedPoints";
	public static final String ITERATION_DATE = "iterationDate";
	
	private List<Map<String,String>> burnDownData; 
	private String iterationId;
	private String projectName;
	private String iterationName;
	private String projectId;
	private Double totalEstimate;
	private long lastUpdated;
	
	
	public long getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(long timestamp) {
		this.lastUpdated = timestamp;
	}
	
	public List<Map<String, String>> getBurnDownData() {
		if(burnDownData==null){
			burnDownData = new ArrayList<>();
		}
		return burnDownData;
	}
	public void setBurnDownData(List<Map<String, String>> burnDownData) {
		this.burnDownData = burnDownData;
	}
	public String getIterationId() {
		return iterationId;
	}
	public void setIterationId(String iterationId) {
		this.iterationId = iterationId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getIterationName() {
		return iterationName;
	}
	public void setIterationName(String iterationName) {
		this.iterationName = iterationName;
	}

	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Double getTotalEstimate() {
		return totalEstimate;
	}
	public void setTotalEstimate(Double totalEstimate) {
		this.totalEstimate = totalEstimate;
	}

}
