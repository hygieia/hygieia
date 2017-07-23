package com.capitalone.dashboard.model;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        protoType.getRequiredFields().addAll(Arrays.asList(BambooJob.INSTANCE_URL, BambooJob.JOB_URL, BambooJob.JOB_NAME));
        return protoType;
    }
}
