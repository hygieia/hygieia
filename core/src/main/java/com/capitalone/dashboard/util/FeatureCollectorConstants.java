
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
        public static final String KANBAN_START_DATE = "1900-01-01T00:00:00.0000000";
        public static final String KANBAN_END_DATE = "9999-12-31T59:59:59.9999999";
        public static final String KANBAN_SPRINT_ID = "KANBAN";
		public static final String SCRUM_SPRINT_ID = "SCRUM";

        private FeatureCollectorConstants() {
                // This class should not be instantiable
        }
}