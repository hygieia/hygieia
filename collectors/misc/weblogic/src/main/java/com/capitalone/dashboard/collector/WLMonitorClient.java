package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.WLMonitorCollector;
import com.capitalone.dashboard.model.WLMonitorCollectorItem;
import com.capitalone.dashboard.model.WebLogicMonitor;

public interface WLMonitorClient {
	
	 List<WebLogicMonitor>  getHealthInfoFromVpriceDb(String envName, String appName);
	    
     List<WLMonitorCollectorItem>  getApplications(WLMonitorCollector collector);

	List<WebLogicMonitor> getWLMonitorEnvironments(String instanceUrl, String envName);
}
