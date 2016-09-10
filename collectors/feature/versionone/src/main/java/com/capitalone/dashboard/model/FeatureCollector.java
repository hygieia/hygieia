
package com.capitalone.dashboard.model;

import com.capitalone.dashboard.util.FeatureCollectorConstants;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 */
public class FeatureCollector extends Collector {
	/**
	 * Creates a static prototype of the Feature Collector, which includes any
	 * specific settings or configuration required for the use of this
	 * collector, including settings for connecting to any source systems.
	 * 
	 * @return A configured Feature Collector prototype
	 */
	public static FeatureCollector prototype() {
		FeatureCollector protoType = new FeatureCollector();
		protoType.setName(FeatureCollectorConstants.VERSIONONE);
		protoType.setOnline(true);
        protoType.setEnabled(true);
		protoType.setCollectorType(CollectorType.ScopeOwner);
		protoType.setLastExecuted(System.currentTimeMillis());

		return protoType;
	}
}
