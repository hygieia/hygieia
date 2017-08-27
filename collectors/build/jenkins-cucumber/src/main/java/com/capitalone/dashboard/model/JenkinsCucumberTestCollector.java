package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extension of Collector that stores current build server configuration.
 */
public class JenkinsCucumberTestCollector extends Collector {
    private List<String> buildServers = new ArrayList<>();

    public List<String> getBuildServers() {
        return buildServers;
    }

    public void setBuildServers(List<String> buildServers) {
        this.buildServers = buildServers;
    }

    public static JenkinsCucumberTestCollector prototype(List<String> buildServers) {
        JenkinsCucumberTestCollector protoType = new JenkinsCucumberTestCollector();
        protoType.setName("JenkinsCucumberTest");
        protoType.setCollectorType(CollectorType.Test);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getBuildServers().addAll(buildServers);
        Map<String, Object> options = new HashMap<>();
        options.put(JenkinsJob.INSTANCE_URL,"");
        options.put(JenkinsJob.JOB_URL,"");
        options.put(JenkinsJob.JOB_NAME,"");
        protoType.setAllFields(options);
        protoType.setUniqueFields(options);
        return protoType;
    }
}
