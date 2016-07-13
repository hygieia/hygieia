package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class AppdynamicsCollector extends Collector {
    private List<String> appdynamicsServers = new ArrayList<>();

    public List<String> getAppdynamicsServers() {
        return appdynamicsServers;
    }

    public static AppdynamicsCollector prototype(List<String> servers) {
        AppdynamicsCollector protoType = new AppdynamicsCollector();
        protoType.setName("Appdynamics");
        protoType.setCollectorType(CollectorType.AppPerformance);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getAppdynamicsServers().addAll(servers);
        return protoType;
    }
}
