package com.capitalone.dashboard.model;

/**
 * Extension of Collector that stores current build server configuration.
 */
public class HpsmCollector extends Collector {

    public static HpsmCollector prototype() {
        return prototype("Hpsm");
    }

    public static HpsmCollector prototype(String name) {
        HpsmCollector protoType = new HpsmCollector();
        protoType.setName(name);
        protoType.setCollectorType(CollectorType.CMDB);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }

}
