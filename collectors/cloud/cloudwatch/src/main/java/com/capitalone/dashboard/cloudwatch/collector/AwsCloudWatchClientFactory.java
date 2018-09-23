package com.capitalone.dashboard.cloudwatch.collector;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.logs.AWSLogsClient;

/**
 * Created by stevegal on 17/06/2018.
 */
public class AwsCloudWatchClientFactory {

    private AWSLogsClient awsLogsClient;

    public void setup(AwsCloudwatchLogAnalyzerSettings settings) {
        System.getProperties().put("http.proxyHost", settings.getProxyHost());
        System.getProperties().put("http.proxyPort", settings.getProxyPort());
        System.getProperties().put("https.proxyHost", settings.getProxyHost());
        System.getProperties().put("https.proxyPort", settings.getProxyPort());
        System.getProperties().put("http.nonProxyHosts", settings.getNonProxy());

        this.awsLogsClient = new AWSLogsClient(new AWSCredentialsProviderChain(new ProfileCredentialsProvider(settings.getProfile()),
            new InstanceProfileCredentialsProvider()));
        if (null != settings.getRegion()) {
            awsLogsClient.withRegion(settings.getRegion());
        }
    }

    public AWSLogsClient getInstance(){
        return awsLogsClient;
    }
}
