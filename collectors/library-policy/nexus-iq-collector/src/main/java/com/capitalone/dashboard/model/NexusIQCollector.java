package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Arrays;
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
        protoType.getUniqueFields().addAll(Arrays.asList(NexusIQApplication.INSTANCE_URL,NexusIQApplication.APPLICATION_NAME));
        protoType.getAllFields().addAll(Arrays.asList(NexusIQApplication.INSTANCE_URL,NexusIQApplication.APPLICATION_NAME, NexusIQApplication.APPLICATION_ID, NexusIQApplication.APPLICATION_PUBLIC_ID));
        return protoType;
    }
}
