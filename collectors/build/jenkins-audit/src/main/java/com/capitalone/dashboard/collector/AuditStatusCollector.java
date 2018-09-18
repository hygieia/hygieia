package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuditStatusCollector extends Collector {

    public static final String INSTANCE_URL = "instanceUrl";
    public static final String JOB_NAME = "jobName";
    public static final String JOB_URL = "jobUrl";

    public static AuditStatusCollector prototype(List<String> servers) {
        AuditStatusCollector protoType = new AuditStatusCollector();
        protoType.setName("JenkinsAuditCollector");
        protoType.setCollectorType(CollectorType.Audit);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.buildServers.addAll(servers);
        Map<String, Object> options = new HashMap<>();
        options.put(INSTANCE_URL,"");
        options.put(JOB_URL,"");
        options.put(JOB_NAME,"");
        protoType.setAllFields(options);
        protoType.setUniqueFields(options);
        return protoType;
    }

    private List<String> buildServers = new ArrayList<>();

    public List<String> getBuildServers() {
        return buildServers;
    }
}