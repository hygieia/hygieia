package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

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
        protoType.getSonarServers().addAll(servers);
        protoType.getSonarVersions().addAll(versions);
        protoType.getSonarMetrics().addAll(metrics);
        return protoType;
    }
}
