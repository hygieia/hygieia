package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ConfigurationRepository;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	private final ConfigurationRepository configurationRepository;
	private final CollectorRepository collectorRepository;

	@Autowired
	public ConfigurationServiceImpl(ConfigurationRepository configurationRepository,
			CollectorRepository collectorRepository) {
		this.configurationRepository = configurationRepository;
		this.collectorRepository = collectorRepository;

	}

	@Override
	public List<Configuration> insertConfigurationData(List<Configuration> configList) {
		for (Configuration config : configList) {
			config.decryptOrEncrptInfo();
		}
		return (List<Configuration>) configurationRepository.save(configList);
	}

	@Override
	public List<Configuration> getConfigurationData() {
		Map<String, String> emptyField = new HashMap<>();
		List<Configuration> configList = new ArrayList<>();
		List<Collector> collectors = (List<Collector>) collectorRepository.findAll();
		for (Collector collector : collectors) {
			if (!collector.getName().equalsIgnoreCase("Product")) {
				Configuration config = configurationRepository.findByCollectorName(collector.getName());

				if (config == null) {
					config = new Configuration();
					config.setCollectorName(collector.getName());
					config.getInfo().add(emptyField);
				}
				
				config.decryptOrEncrptInfo();
				configList.add(config);
			}
		}
		return configList;
	}
}
