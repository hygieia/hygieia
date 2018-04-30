package com.capitalone.dashboard.request;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.capitalone.dashboard.model.Configuration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationCreateRequest {
	
	private String collectorName;
	private Set<Map<String,String>> info = new HashSet<>();
	
	public String getCollectorName() {
		return collectorName;
	}
	public void setCollectorName(String collectorName) {
		this.collectorName = collectorName;
	}
	public Set<Map<String, String>> getInfo() {
		return info;
	}
	public void setInfo(Set<Map<String, String>> info) {
		this.info = info;
	}
	
	public Configuration toConfiguration() {
		return new Configuration(collectorName,info);
	}
}
