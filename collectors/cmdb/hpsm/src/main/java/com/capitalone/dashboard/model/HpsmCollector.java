package com.capitalone.dashboard.model;

/**
 * Extension of Collector that stores current build server configuration.
 */
public class HpsmCollector extends Collector {
    public static HpsmCollector prototype() {
        HpsmCollector protoType = new HpsmCollector();
        protoType.setName("Hpsm");
        protoType.setCollectorType(CollectorType.CMDB);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }
}
