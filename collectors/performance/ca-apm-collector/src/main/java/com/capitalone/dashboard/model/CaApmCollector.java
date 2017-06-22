package com.capitalone.dashboard.model;

public class CaApmCollector extends Collector {
	public static CaApmCollector prototype() {
		CaApmCollector protoType = new CaApmCollector();
		protoType.setName("CaApm");
		protoType.setCollectorType(CollectorType.CaApm);
		protoType.setOnline(true);
		protoType.setEnabled(true);
		return protoType;
	}
}
