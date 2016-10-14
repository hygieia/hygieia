
package com.capitalone.dashboard.util;

/**
 * This class is established to house any globally-referenced constant values.
 * Most values that can or should be modifiable at deploy-time should be kept in
 * a properties file, but valid use cases for constant values should be added
 * here.
 *
 * @author kfk884
 *
 */
public final class FeatureCollectorConstants {
        public static final String JIRA = "Jira";
        public static final String VERSIONONE = "VersionOne";
        @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
        // not an IP
        public static final String AGENT_VER = "01.00.00.01";
        public static final String AGENT_NAME = "Hygieia Dashboard - VersionOne Feature Collector";
		
		public static final String STORY_HOURS_ESTIMATE = "hours";
		public static final String STORY_POINTS_ESTIMATE = "storypoints";
		
		public static final String SPRINT_SCRUM = "scrum";
		public static final String SPRINT_KANBAN = "kanban";

        private FeatureCollectorConstants() {
                // This class should not be instantiable
        }
}