package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Collector implementation for XLDeploy that stores XLDeploy server URLs.
 */
public class XLDeployCollector extends Collector {
    private List<String> xldeployServers = new ArrayList<>();

    public List<String> getXLdeployServers() {
        return xldeployServers;
    }

    public static XLDeployCollector prototype(List<String> servers) {
    	XLDeployCollector protoType = new XLDeployCollector();
        protoType.setName("XLDeploy");
        protoType.setCollectorType(CollectorType.Deployment);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getXLdeployServers().addAll(servers);
        return protoType;
    }
}
