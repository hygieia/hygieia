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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.capitalone.dashboard.model.AWSConfig;
import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.repository.AWSConfigRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CloudComputeDataRepository;

/**
 * Collects {@link AWSCloudCollector} data from feature content source system.
 * 
 * @author
 * @param <ObjectId>
 */
@Component
public class AWSCloudCollectorTask extends CollectorTask<AWSCloudCollector> {
	private static final Log logger = LogFactory
			.getLog(AWSCloudCollectorTask.class);

	private final CloudComputeDataRepository awsAggregatedDataRepository;
	private final AWSCloudSettings awsSetting;
	private final AWSCloudClient awsClient;
	private final AWSConfigRepository awsConfigRepository;
	private final BaseCollectorRepository<AWSCloudCollector> collectorRepository;
	private final Iterable<CloudComputeData> allAggregatedData;

	/**
	 * Default constructor for the collector task. This will construct this
	 * collector task with all repository, scheduling, and settings
	 * configurations custom to this collector.
	 * 
	 * @param taskScheduler
	 *            A task scheduler artifact
	 * @param teamRepository
	 *            The repository being use for feature collection
	 * @param featureSettings
	 *            The settings being used for feature collection from the source
	 *            system
	 */
	@Autowired
	public AWSCloudCollectorTask(TaskScheduler taskScheduler,
			BaseCollectorRepository<AWSCloudCollector> collectorRepository,
			AWSCloudSettings cloudSettings, AWSCloudClient cloudClient,
			AWSConfigRepository awsConfigRepository,
			CloudComputeDataRepository awsAggregatedRepository) {
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
	 * repository with retrieved data.
	 */
	public void collect(AWSCloudCollector collector) {
		logger.info("Starting AWS collection...");

		ClientConfiguration clientConfig = new ClientConfiguration()
				.withProxyHost(awsSetting.getProxyURL())
				.withProxyPort(awsSetting.getProxyPort())
				.withPreemptiveBasicProxyAuth(true)
				.withProxyUsername(awsSetting.getProxyUser())
				.withProxyPassword(awsSetting.getProxyPassword());

		logger.info("Collecting AWS Cloud Data...");
		List<AWSConfig> enabledList = enabledConfigs(collector);
		for (AWSConfig config : enabledList) {
			String accessKey = config.getAccessKey();
			String secretKey = config.getSecretKey();

			AWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
			AmazonEC2Client ec2Client = new AmazonEC2Client(creds, clientConfig);
			AmazonCloudWatchClient cwClient = new AmazonCloudWatchClient(creds,
					clientConfig);
			DescribeInstancesResult result = ec2Client.describeInstances();
			// create list of instances
			List<Instance> instanceList = new ArrayList<Instance>();
			List<Reservation> reservations = result.getReservations();
			for (Reservation currRes : reservations) {
				List<Instance> currInstanceList = currRes.getInstances();
				instanceList.addAll(currInstanceList);
			}

			ArrayList<CloudComputeInstanceData> rawDataList = new ArrayList<CloudComputeInstanceData>();
			for (Instance currInstance : instanceList) {
				CloudComputeInstanceData object = awsClient.getMetrics(currInstance,
						cwClient, accessKey);
				object.setCollectorItemId(config.getId());
				System.out.println("Collector Item ID:"
						+ object.getCollectorItemId());
				rawDataList.add(object);
			}

			logger.info("Agregating Data...");
			CloudComputeData computeData = new CloudComputeData();
			AWSCloudStatistics stat = new AWSCloudStatistics(rawDataList);
			ObjectId id = config.getId();
			computeData.setAgeWarning(stat.getAgeWarningCount());
			computeData.setAgeExpired(stat.getAgeExpireCount());
			computeData.setAgeGood(stat.getAgeGoodCount());
			computeData.setCpuHigh(stat.getCpuHighCount());
			computeData.setCpuMid(stat.getCpuMidCount());
			computeData.setCpuLow(stat.getCpuLowCount());
			computeData.setNonEncryptedCount(stat.getUnEcryptedCount());
			computeData.setNonTaggedCount(stat.getUnTaggedCount());
			computeData.setStoppedCount(stat.getStoppedCount());
			computeData.setTotalInstanceCount(stat.getTotalCount());
			computeData.setCollectorItemId(id);
			computeData.setDetailList(rawDataList);
			computeData.setLastUpdated(System.currentTimeMillis());
			awsAggregatedDataRepository.save(computeData);

		}
		logger.info("Finished Cloud collection.");
	}

	public CloudComputeData findAggregatedDataByConfig(AWSConfig config) {
		CloudComputeData returnData = null;
		for (CloudComputeData data : this.allAggregatedData) {
			returnData = data;
		}
		return returnData;
	}

	@Override
	public BaseCollectorRepository<AWSCloudCollector> getCollectorRepository() {
		return collectorRepository;
	}

	private List<AWSConfig> enabledConfigs(Collector collector) {
		return awsConfigRepository.findEnabledAWSConfig(collector.getId());
	}
}
