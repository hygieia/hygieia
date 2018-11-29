package com.capitalone.dashboard.model;

import com.capitalone.dashboard.util.FeatureCollectorConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 *
 * @author KFK884
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
		protoType.setName(FeatureCollectorConstants.JIRA);
		protoType.setOnline(true);
        protoType.setEnabled(true);
		protoType.setCollectorType(CollectorType.AgileTool);
		protoType.setLastExecuted(System.currentTimeMillis());

		Map<String, Object> allOptions = new HashMap<>();
		allOptions.put(FeatureCollectorConstants.TOOL_TYPE, "");
		allOptions.put(FeatureCollectorConstants.PROJECT_NAME, "");
		allOptions.put(FeatureCollectorConstants.PROJECT_ID, "");
		allOptions.put(FeatureCollectorConstants.TEAM_NAME, "");
		allOptions.put(FeatureCollectorConstants.TEAM_ID, "");
		allOptions.put(FeatureCollectorConstants.ESTIMATE_METRIC_TYPE, "");
		allOptions.put(FeatureCollectorConstants.SPRINT_TYPE, "");
		allOptions.put(FeatureCollectorConstants.LIST_TYPE, "");
		allOptions.put(FeatureCollectorConstants.SHOW_STATUS, "");
		protoType.setAllFields(allOptions);

		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put(FeatureCollectorConstants.TOOL_TYPE, "");
		uniqueOptions.put(FeatureCollectorConstants.PROJECT_NAME, "");
		uniqueOptions.put(FeatureCollectorConstants.PROJECT_ID, "");
		uniqueOptions.put(FeatureCollectorConstants.TEAM_NAME, "");
		uniqueOptions.put(FeatureCollectorConstants.TEAM_ID, "");

		protoType.setUniqueFields(uniqueOptions);

		return protoType;
	}
}
