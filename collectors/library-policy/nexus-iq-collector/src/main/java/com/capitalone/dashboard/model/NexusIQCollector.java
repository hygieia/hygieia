package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class NexusIQCollector extends Collector {
    private List<String> nexusIQServers = new ArrayList<>();

    public List<String> getNexusIQServers() {
        return nexusIQServers;
    }

    public static NexusIQCollector prototype(List<String> servers) {
        NexusIQCollector protoType = new NexusIQCollector();
        protoType.setName("NexusIQ");
        protoType.setCollectorType(CollectorType.LibraryPolicy);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getNexusIQServers().addAll(servers);
        return protoType;
    }
}
