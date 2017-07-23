package com.capitalone.dashboard.model;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extension of Collector that stores current build server configuration.
 */
public class HudsonCollector extends Collector {
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

    public static HudsonCollector prototype(List<String> buildServers, List<String> niceNames) {
        HudsonCollector protoType = new HudsonCollector();
        protoType.setName("Hudson");
        protoType.setCollectorType(CollectorType.Build);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getBuildServers().addAll(buildServers);
        if (!CollectionUtils.isEmpty(niceNames)) {
            protoType.getNiceNames().addAll(niceNames);
        }
        protoType.getUniqueFields().addAll(Arrays.asList(HudsonJob.INSTANCE_URL, HudsonJob.JOB_URL, HudsonJob.JOB_NAME));
        protoType.getAllFields().addAll(protoType.getUniqueFields());
        return protoType;
    }
}
