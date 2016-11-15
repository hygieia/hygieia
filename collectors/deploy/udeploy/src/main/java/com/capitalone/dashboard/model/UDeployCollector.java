package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * Collector implementation for UDeploy that stores UDeploy server URLs.
 */
public class UDeployCollector extends Collector {
    private List<String> udeployServers = new ArrayList<>();
    private List<String> niceNames = new ArrayList<>();

    public List<String> getUdeployServers() {
        return udeployServers;
    }
    
    public List<String> getNiceNames() {
    	return niceNames;
    }

    public static UDeployCollector prototype(List<String> servers, List<String> niceNames) {
        UDeployCollector protoType = new UDeployCollector();
        protoType.setName("UDeploy");
        protoType.setCollectorType(CollectorType.Deployment);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getUdeployServers().addAll(servers);
        if (!CollectionUtils.isEmpty(niceNames)) {
            protoType.getNiceNames().addAll(niceNames);
        }
        return protoType;
    }
}
