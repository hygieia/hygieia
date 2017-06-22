package com.capitalone.dashboard.collector;

import alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot;
import metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo;

public interface CaApmClient {
	DAllAlertsSnapshot[] getAllAlertsSnapshotForManagementModule(CaApmSettings caApmPullSettings, String mngModelName) throws Exception;
	ManagementModuleInfo[] getListOfManagementModules(CaApmSettings caApmPullSettings) throws Exception;
}
