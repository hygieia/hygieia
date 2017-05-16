package com.capitalone.dashboard.model;


public class WLMonitorCollector extends Collector {
    public static WLMonitorCollector prototype() {
    	WLMonitorCollector protoType = new WLMonitorCollector();
        protoType.setName("WLMonitor");
        protoType.setCollectorType(CollectorType.WLMonitor);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }
}
