package com.capitalone.dashboard.model;

import com.capitalone.dashboard.util.FeatureCollectorConstants;
import org.springframework.stereotype.Component;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 */
@Component
public class TestResultCollector extends Collector {
    /**
     * Creates a static prototype of the Feature Collector, which includes any
     * specific settings or configuration required for the use of this
     * collector, including settings for connecting to any source systems.
     *
     * @return A configured TestResult Collector prototype
     */
    public static TestResultCollector prototype() {
        TestResultCollector protoType = new TestResultCollector();
        protoType.setName(FeatureCollectorConstants.JIRA_XRAY);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.setCollectorType(CollectorType.Test);
        protoType.setLastExecuted(System.currentTimeMillis());

        return protoType;
    }
}