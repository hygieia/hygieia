package com.capitalone.dashboard.model;

import com.capitalone.dashboard.collector.AppdynamicsSettings;

import java.util.List;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 *
 * @author pxd338
 */
public class AppdynamicsCollector extends Collector {
    private List<String> instanceUrls;

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
        protoType.setInstanceUrl(settings.getInstanceUrlList());
        return protoType;
    }

    public List<String> getInstanceUrls() {
        return instanceUrls;
    }

    public void setInstanceUrl(List<String> instanceUrls) {
        this.instanceUrls = instanceUrls;
    }
}
