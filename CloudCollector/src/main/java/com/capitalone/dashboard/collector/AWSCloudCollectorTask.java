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
import com.capitalone.dashboard.model.AWSCloudCollector;
import com.capitalone.dashboard.model.CloudAggregatedData;
import com.capitalone.dashboard.model.CloudRawData;
import com.capitalone.dashboard.repository.AWSRawDataRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CloudAggregatedDataRepository;
import com.capitalone.dashboard.util.AWSCloudSettings;



/**
 * Collects {@link AWSCloudCollector} data from feature content source system.
 * 
 * @author 
 * @param <ObjectId>
 */
@Component
public class AWSCloudCollectorTask extends CollectorTask<AWSCloudCollector>{
	private static final Log logger = LogFactory
			.getLog(AWSCloudCollectorTask.class);

	private final AWSRawDataRepository awsRawDataRepository;
	private final CloudAggregatedDataRepository cloudAccountRepository;
	private final AWSCloudSettings cloudSettings;
	private final AWSCloudClient cloudClient;
	private final BaseCollectorRepository<AWSCloudCollector> collectorRepository;

	
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
								AWSRawDataRepository awsObjectRepository,
								AWSCloudSettings cloudSettings, 
								AWSCloudClient cloudClient, 
								CloudAggregatedDataRepository cloudAccountRepository) {
		super(taskScheduler, "AWSCloud");
		this.collectorRepository = collectorRepository;
		this.cloudClient = cloudClient;
		this.awsRawDataRepository = awsObjectRepository;
		this.cloudSettings = cloudSettings;
		this.cloudAccountRepository = cloudAccountRepository;
	}

    
	public AWSCloudCollector getCollector() {
		return AWSCloudCollector.prototype();
	}

	/**
	 * Accessor method for the collector repository
	 */
	public AWSRawDataRepository getAWSObjectRepository() {
		return awsRawDataRepository;
	}

	/**
	 * Accessor method for the current chronology setting, for the scheduler
	 */
	public String getCron() {
		return cloudSettings.getCron();
	}

	/**
	 * The collection action. This is the task which will run on a schedule to
	 * gather data from the feature content source system and update the
	 * repository with retrieved data.
	 */
	public void collect(AWSCloudCollector collector) {
		logger.info("Starting Cloud collection...");
		
		ClientConfiguration clientConfig = new ClientConfiguration()
			.withProxyHost("proxy.kdc.capitalone.com")
			.withProxyPort(8099)
			.withPreemptiveBasicProxyAuth(true)
			.withProxyUsername("xxx")
			.withProxyPassword("xxxxxxx");
		
		
		String accessKey = "xxx";
		String secretKey = "xxx";

		AWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
		AmazonEC2Client ec2Client = new AmazonEC2Client(creds, clientConfig);
		AmazonCloudWatchClient cwClient = new AmazonCloudWatchClient(creds, clientConfig);
        DescribeInstancesResult result = ec2Client.describeInstances();
        
        //create list of instances
        List<Instance> instanceList = new ArrayList<Instance>();
		List<Reservation> reservations = result.getReservations();
		for (Reservation currRes : reservations) {
			List<Instance> currInstanceList = currRes.getInstances();
			instanceList.addAll(currInstanceList);
		}
		
		//purge the repo of old instance data
		if (awsRawDataRepository.count()>0)
			awsRawDataRepository.deleteAll();
		
		//for every instance determine all metrics
		logger.info("Collecting Raw Data...");
        for (Instance currInstance : instanceList) {
        	CloudRawData object = cloudClient.getMetrics(currInstance, cwClient, accessKey);
    		awsRawDataRepository.save(object);
        }
        

        //purge the repo of old account data
        if (cloudAccountRepository.count()>0)
        	cloudAccountRepository.deleteAll();
        		
        logger.info("Agregating Data...");
		CloudAggregatedData aggregatedData = new CloudAggregatedData();
		aggregatedData.setAgeWarning(awsRawDataRepository.runAgeWarning("cof-sandbox-dev").size());
		aggregatedData.setAgeExpired(awsRawDataRepository.runAgeExpired("cof-sandbox-dev").size());
		aggregatedData.setAgeGood(awsRawDataRepository.runAgeGood("cof-sandbox-dev").size());
		aggregatedData.setCpuHigh(awsRawDataRepository.runCpuUtilizationHigh("cof-sandbox-dev").size());
		aggregatedData.setCpuMid(awsRawDataRepository.runCpuUtilizationMid("cof-sandbox-dev").size());
		aggregatedData.setCpuLow(awsRawDataRepository.runCpuUtilizationLow("cof-sandbox-dev").size());
		aggregatedData.setNonEncryptedCount(awsRawDataRepository.runNonEncrypted("cof-sandbox-dev").size());
		aggregatedData.setNonTaggedCount(awsRawDataRepository.runNonTagged("cof-sandbox-dev").size());
		aggregatedData.setStoppedCount(awsRawDataRepository.runStopped("cof-sandbox-dev").size());
		aggregatedData.setAccountName("cof-sandbox-dev");
		aggregatedData.setTotalInstanceCount(awsRawDataRepository.runAllInstanceCount("cof-sandbox-dev").size());

		cloudAccountRepository.save(aggregatedData);
		logger.info("Finished Cloud collection.");
		
	}
	
    @Override
    public BaseCollectorRepository<AWSCloudCollector> getCollectorRepository() {
        return collectorRepository;
    }
    

}
