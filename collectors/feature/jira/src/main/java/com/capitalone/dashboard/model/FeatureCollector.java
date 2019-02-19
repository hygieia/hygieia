package com.capitalone.dashboard.model;

import com.capitalone.dashboard.util.FeatureCollectorConstants;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 *
 * @author KFK884
 */
public class FeatureCollector extends Collector {
	private long lastRefreshTime;

	public long getLastRefreshTime() {
		return lastRefreshTime;
	}

	public void setLastRefreshTime(long lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}

	public JiraMode getMode() {
		return (JiraMode) MapUtils.getObject(this.getProperties(),"mode", JiraMode.Board);
	}
	/**
	 * Creates a static prototype of the Feature Collector, which includes any
	 * specific settings or configuration required for the use of this
	 * collector, including settings for connecting to any source systems.
	 *
	 * @return A configured Feature Collector prototype
	 */
	public static FeatureCollector prototype(JiraMode mode) {
		FeatureCollector protoType = new FeatureCollector();

		protoType.setName(FeatureCollectorConstants.JIRA);
		protoType.setOnline(true);
        protoType.setEnabled(true);
		protoType.setCollectorType(CollectorType.AgileTool);

		Map<String, Object> allOptions = new HashMap<>();
		allOptions.put(FeatureBoard.TOOL_TYPE, "");
		allOptions.put(FeatureBoard.PROJECT_NAME, "");
		allOptions.put(FeatureBoard.PROJECT_ID, "");
		allOptions.put(FeatureBoard.TEAM_NAME, "");
		allOptions.put(FeatureBoard.TEAM_ID, "");
		allOptions.put(FeatureBoard.ESTIMATE_METRIC_TYPE, "");
		allOptions.put(FeatureBoard.SPRINT_TYPE, "");
		allOptions.put(FeatureBoard.LIST_TYPE, "");
		allOptions.put(FeatureBoard.SHOW_STATUS, "");
		protoType.setAllFields(allOptions);

		Map<String, Object> uniqueOptions = new HashMap<>();
		uniqueOptions.put(FeatureBoard.TOOL_TYPE, "");
		uniqueOptions.put(FeatureBoard.PROJECT_NAME, "");
		uniqueOptions.put(FeatureBoard.PROJECT_ID, "");
		uniqueOptions.put(FeatureBoard.TEAM_NAME, "");
		uniqueOptions.put(FeatureBoard.TEAM_ID, "");

		protoType.setUniqueFields(uniqueOptions);

		Map<String, Object> properties = new HashMap<>();
		properties.put("mode", mode);
		protoType.setProperties(properties);

		return protoType;
	}
}
