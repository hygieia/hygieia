package com.capitalone.dashboard.model;

import com.capitalone.dashboard.collector.AppdynamicsSettings;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 *
 * @author pxd338
 */
public class AppdynamicsCollector extends Collector {
    private String controller;

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    /**
     * Creates a static prototype of the Feature Collector, which includes any
     * specific settings or configuration required for the use of this
     * collector, including settings for connecting to any source systems.
     *
     * @return A configured Feature Collector prototype
     */

    public static AppdynamicsCollector prototype(AppdynamicsSettings settings) {
        AppdynamicsCollector protoType = new AppdynamicsCollector();
        protoType.setName("Appdynamics");
        protoType.setCollectorType(CollectorType.AppPerformance);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.setLastExecuted(System.currentTimeMillis());
        protoType.setController(settings.getController());
        return protoType;
    }
}
