package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Collectors get registered with the dashboard and their configuration ends up here.
 */
public class JenkinsCodeQualityCollector extends Collector {


    public static JenkinsCodeQualityCollector prototype(List<String> servers) {
        JenkinsCodeQualityCollector protoType = new JenkinsCodeQualityCollector();
        protoType.setName("JenkinsCodeQuality");
        protoType.setCollectorType(CollectorType.CodeQuality);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.buildServers.addAll(servers);
        return protoType;
    }

    private List<String> buildServers = new ArrayList<>();

    public List<String> getBuildServers() {
        return buildServers;
    }
}
