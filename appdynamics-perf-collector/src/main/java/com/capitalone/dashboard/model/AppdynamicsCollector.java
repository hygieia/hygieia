package com.capitalone.dashboard.model;

import org.appdynamics.appdrestapi.RESTAccess;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 *
 * @author pxd338
 */
public class AppdynamicsCollector extends Collector {


    private static RESTAccess access = null;


    public static RESTAccess getAccess() {
        return access;
    }

    public static void setAccess(RESTAccess access) {
        AppdynamicsCollector.access = access;
    }

    /**
     * Creates a static prototype of the Feature Collector, which includes any
     * specific settings or configuration required for the use of this
     * collector, including settings for connecting to any source systems.
     *
     * @return A configured Feature Collector prototype
     */

    public static AppdynamicsCollector prototype(RESTAccess access) {
        AppdynamicsCollector protoType = new AppdynamicsCollector();
        protoType.setName("Appdynamics");
        protoType.setCollectorType(CollectorType.AppPerformance);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.setLastExecuted(System.currentTimeMillis());
        protoType.setAccess(access);

        return protoType;
    }
}
