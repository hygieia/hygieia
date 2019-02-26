package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class CoverityCollector extends Collector {
	
	public static final String NICE_NAME = "Coverity";
	
    private List<String> coverityServers = new ArrayList<>();

    public List<String> getCoverityServers() {
        return coverityServers;
    }

    public static CoverityCollector prototype(List<String> servers) {
    	CoverityCollector protoType = new CoverityCollector();
        protoType.setName(NICE_NAME);
        protoType.setCollectorType(CollectorType.StaticSecurityScan);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getCoverityServers().addAll(servers);
        return protoType;
    }
}
