package com.capitalone.dashboard.collector;

import java.util.Date;
import java.util.List;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.XLDeployApplication;
import com.capitalone.dashboard.model.XLDeployApplicationHistoryItem;

public interface XLDeployClient {
	/**
	 * 
	 * @param instanceUrl
	 * @return a list of applications for the given XLD instance
	 */
	List<XLDeployApplication> getApplications(String instanceUrl);
	
	/**
	 * 
	 * @param instanceURL
	 * @return a list of environments for the given XLD instance
	 */
	List<Environment> getEnvironments(String instanceURL);

	/**
	 * Obtain deployment history for the provided application.
	 * 
	 * @param application	the application
	 * @param startDate		the start date
	 * @param endDate		the end date
	 * @return	a list of deployment history
	 */
	List<XLDeployApplicationHistoryItem> getApplicationHistory(XLDeployApplication application, Date startDate, Date endDate);
	
	/**
	 * Obtain deployment history for the list of applications. All applications must belong to the same XLD instance.
	 * 
	 * @param applications	a list of applications that come from the same XLD instance
	 * @param startDate		the start date
	 * @param endDate		the end date
	 * @return	a list of deployment history
	 */
	List<XLDeployApplicationHistoryItem> getApplicationHistory(List<XLDeployApplication> applications, Date startDate, Date endDate);
}
