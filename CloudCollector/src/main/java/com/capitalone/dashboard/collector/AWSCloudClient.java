package com.capitalone.dashboard.collector; 

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.ec2.model.Instance;
import com.capitalone.dashboard.model.CloudRawData;
import com.mongodb.DBCollection;

/**
 * Client for fetching commit history from GitHub
 */
public interface AWSCloudClient {

    /**
     * Fetch all of the commits for the provided SubversionRepo.
     * @param cwClient 
     * @param accessKey 
     *
     * @param client, the client for EC2
     * @param cwClient, the client for CloudWatch
     * @return a collection of objects with metrics for each instance
     */

  CloudRawData getMetrics(Instance currInstance, AmazonCloudWatchClient cwClient, String accessKey);

}

