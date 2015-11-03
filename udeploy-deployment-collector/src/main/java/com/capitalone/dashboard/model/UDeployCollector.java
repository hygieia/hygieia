package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Collector implementation for UDeploy that stores UDeploy server URLs.
 */
public class UDeployCollector extends Collector {
    private List<String> udeployServers = new ArrayList<>();

    public List<String> getUdeployServers() {
        return udeployServers;
    }

    public static UDeployCollector prototype(List<String> servers) {
        UDeployCollector protoType = new UDeployCollector();
        protoType.setName("UDeploy");
        protoType.setCollectorType(CollectorType.Deployment);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getUdeployServers().addAll(servers);
        return protoType;
    }
}
