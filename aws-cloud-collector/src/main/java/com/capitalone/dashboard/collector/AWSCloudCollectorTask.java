/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AWSConfig;
import com.capitalone.dashboard.model.Cloud;
import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.repository.AWSConfigRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CloudRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Collects {@link AWSCloudCollector} data from feature content source system.
 *
 */
@Component
public class AWSCloudCollectorTask extends CollectorTask<AWSCloudCollector> {
	private static final Log logger = LogFactory
			.getLog(AWSCloudCollectorTask.class);

	private final CloudRepository awsAggregatedDataRepository;
	private final AWSCloudSettings awsSetting;
	private final AWSCloudClient awsClient;
	private final AWSConfigRepository awsConfigRepository;
	private final BaseCollectorRepository<AWSCloudCollector> collectorRepository;
	private final Iterable<Cloud> allAggregatedData;

	/**
	 *
	 * @param taskScheduler
	 * @param collectorRepository
	 * @param cloudSettings
	 * @param cloudClient
	 * @param awsConfigRepository
	 * @param awsAggregatedRepository
	 */
	@Autowired
	public AWSCloudCollectorTask(TaskScheduler taskScheduler,
			BaseCollectorRepository<AWSCloudCollector> collectorRepository,
			AWSCloudSettings cloudSettings, AWSCloudClient cloudClient,
			AWSConfigRepository awsConfigRepository,
			CloudRepository awsAggregatedRepository) {
		super(taskScheduler, "AWSCloud");
		this.collectorRepository = collectorRepository;
		this.awsClient = cloudClient;
		this.awsSetting = cloudSettings;
		this.awsConfigRepository = awsConfigRepository;
		this.awsAggregatedDataRepository = awsAggregatedRepository;
		this.allAggregatedData = awsAggregatedDataRepository.findAll();
	}

	public AWSCloudCollector getCollector() {
		return AWSCloudCollector.prototype();
	}

	/**
	 * Accessor method for the current chronology setting, for the scheduler
	 */
	public String getCron() {
		return awsSetting.getCron();
	}

	/**
	 * The collection action. This is the task which will run on a schedule to
	 * gather data from the feature content source system and update the
	 * repository with retrieved .
	 */
	public void collect(AWSCloudCollector collector) {
		logger.info("Starting AWS collection...");
		logger.info("Collecting AWS Cloud Data...");
		List<AWSConfig> enabledList = enabledConfigs(collector);
		int i = 0;
		for (AWSConfig config : enabledList) {
			i = i + 1;
			logger.info("Collecting AWS Data for item " + i + "of " + enabledList.size());
			CloudComputeData computeData = awsClient.getCloudComputeData(config);
//			CloudStorageData storageData = awsClient.getCloudStorageData(config);
			Cloud cloud = new Cloud();
			cloud.setCollectorItemId(config.getId());
			cloud.setCompute(computeData);
//			cloud.setStorage(storageData);
			config.setLastUpdateTime(System.currentTimeMillis());
			awsAggregatedDataRepository.save(cloud);
		}

		logger.info("Finished Cloud collection.");
	}

	@Override
	public BaseCollectorRepository<AWSCloudCollector> getCollectorRepository() {
		return collectorRepository;
	}

	private List<AWSConfig> enabledConfigs(Collector collector) {
		return awsConfigRepository.findEnabledAWSConfig(collector.getId());
	}
}
