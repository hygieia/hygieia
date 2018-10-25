package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;

import java.util.ArrayList;
import java.util.List;

public class AuditCollector extends Collector {

    private List<String> auditServers = new ArrayList<>();

    /**
    * Audit Collector Instance built with required config settings
    */
    public static AuditCollector prototype(List<String> servers) {
        AuditCollector protoType = new AuditCollector();
        protoType.setName("AuditCollector");
        protoType.setCollectorType(CollectorType.Audit);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.auditServers.addAll(servers);
        return protoType;
    }
    public List<String> getAuditServers() {
        return auditServers;
    }
}