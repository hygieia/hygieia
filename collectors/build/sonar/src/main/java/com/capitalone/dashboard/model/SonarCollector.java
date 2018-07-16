package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarCollector extends Collector {
    private List<String> sonarServers = new ArrayList<>();
    private List<Double> sonarVersions = new ArrayList<>();
    private List<String> sonarMetrics = new ArrayList<>();

    public List<String> getSonarServers() {
        return sonarServers;
    }
    public List<Double> getSonarVersions() {
        return sonarVersions;
    }

    public List<String> getSonarMetrics() {
        return sonarMetrics;
    }

    public static SonarCollector prototype(List<String> servers, List<Double> versions, List<String> metrics) {
        SonarCollector protoType = new SonarCollector();
        protoType.setName("Sonar");
        protoType.setCollectorType(CollectorType.CodeQuality);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        if(servers!=null) {
            protoType.getSonarServers().addAll(servers);
        }
        if(versions!=null) {
            protoType.getSonarVersions().addAll(versions);
        }
        if(metrics!=null) {
            protoType.getSonarMetrics().addAll(metrics);
        }

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(SonarProject.INSTANCE_URL,"");
        allOptions.put(SonarProject.PROJECT_NAME,"");
        allOptions.put(SonarProject.PROJECT_ID, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(SonarProject.INSTANCE_URL,"");
        uniqueOptions.put(SonarProject.PROJECT_NAME,"");
        protoType.setUniqueFields(uniqueOptions);
        return protoType;
    }
}
