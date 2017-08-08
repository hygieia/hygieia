package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SonarCollector extends Collector {
    private static final Log LOG = LogFactory.getLog(SonarCollector.class);
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
        try {
            protoType.getSonarServers().addAll(servers);
            protoType.getSonarVersions().addAll(versions);
            protoType.getSonarMetrics().addAll(metrics);
        } catch(Exception exception) {
            LOG.error("Could not parse 'sonar' properties [servers/versions/metrics] : "+ exception.getMessage());
        }
        return protoType;
    }
}
