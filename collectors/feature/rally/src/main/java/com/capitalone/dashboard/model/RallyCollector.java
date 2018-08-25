package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class RallyCollector extends Collector {
    private List<String> rallyServers = new ArrayList<>();

    public List<String> getRallyServers() {
        return rallyServers;
    }

    public static RallyCollector prototype(List<String> servers) {
        RallyCollector protoType = new RallyCollector();
        protoType.setName("Rally");
        protoType.setCollectorType(CollectorType.AgileTool);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getRallyServers().addAll(servers);
        return protoType;
    }
}
