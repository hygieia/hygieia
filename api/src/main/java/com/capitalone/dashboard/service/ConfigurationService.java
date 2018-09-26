package com.capitalone.dashboard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Configuration;

@Service
public interface ConfigurationService {
	List<Configuration> insertConfigurationData(List<Configuration> config);

	List<Configuration> getConfigurationData();
}
