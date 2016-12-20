package com.capitalone.dashboard.service;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.ApiSettings;
import com.captialone.dashboard.response.SystemConfigResponse;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
	private ApiSettings apiSettings;
	
	@Autowired
	public SystemConfigServiceImpl(ApiSettings apiSettings) {
		this.apiSettings = apiSettings;
	}

	@Override
	public SystemConfigResponse getSystemConfig() {
		SystemConfigResponse response = new SystemConfigResponse();
		
		Map<String, Object> globalProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		globalProperties.put("multipleDeploymentServers", apiSettings.isMultipleDeploymentServers());
		
		response.setGlobalProperties(globalProperties);
		
		return response;
	}
	
}
