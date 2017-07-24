package com.capitalone.dashboard.model;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extension of Collector that stores current build server configuration.
 */
public class BambooCollector extends Collector {
    private List<String> buildServers = new ArrayList<>();
    private List<String> niceNames = new ArrayList<>();

    public List<String> getBuildServers() {
        return buildServers;
    }

    public List<String> getNiceNames() {
        return niceNames;
    }

    public void setNiceNames(List<String> niceNames) {
        this.niceNames = niceNames;
    }

    public void setBuildServers(List<String> buildServers) {
        this.buildServers = buildServers;
    }

    public static BambooCollector prototype(List<String> buildServers, List<String> niceNames) {
        BambooCollector protoType = new BambooCollector();
        protoType.setName("Bamboo");
        protoType.setCollectorType(CollectorType.Build);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getBuildServers().addAll(buildServers);
        if (!CollectionUtils.isEmpty(niceNames)) {
            protoType.getNiceNames().addAll(niceNames);
        }
        Map<String, Object> options = new HashMap<>();
        options.put(BambooJob.INSTANCE_URL,"");
        options.put(BambooJob.JOB_URL,"");
        options.put(BambooJob.JOB_NAME,"");
        protoType.setAllFields(options);
        protoType.setUniqueFields(options);
        return protoType;
    }
}
