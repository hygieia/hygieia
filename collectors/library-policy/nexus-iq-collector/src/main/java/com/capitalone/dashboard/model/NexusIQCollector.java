package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(NexusIQApplication.INSTANCE_URL,"");
        allOptions.put(NexusIQApplication.APPLICATION_NAME,"");
        allOptions.put(NexusIQApplication.APPLICATION_ID, "");
        allOptions.put(NexusIQApplication.APPLICATION_PUBLIC_ID, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(NexusIQApplication.INSTANCE_URL,"");
        uniqueOptions.put(NexusIQApplication.APPLICATION_NAME,"");
        protoType.setUniqueFields(uniqueOptions);
        return protoType;
    }
}
