package com.captialone.dashboard.response;

import java.util.List;
import java.util.Map;

import com.capitalone.dashboard.model.PipelineStage;

public class SystemConfigResponse {
	private Map<String, Object> globalProperties;
	private List<PipelineStage> systemStages;

	/**
	 * @return the globalProperties
	 */
	public Map<String, Object> getGlobalProperties() {
		return globalProperties;
	}

	/**
	 * @param globalProperties the globalProperties to set
	 */
	public void setGlobalProperties(Map<String, Object> globalProperties) {
		this.globalProperties = globalProperties;
	}
	
	public List<PipelineStage> getSystemStages() {
		return systemStages;
	}
	
	public void setSystemStages(List<PipelineStage> systemStages) {
		this.systemStages = systemStages;
	}
	
}
