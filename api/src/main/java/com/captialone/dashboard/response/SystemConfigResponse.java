package com.captialone.dashboard.response;

import java.util.Map;

public class SystemConfigResponse {
	private Map<String, Object> globalProperties;

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
	
	
}
