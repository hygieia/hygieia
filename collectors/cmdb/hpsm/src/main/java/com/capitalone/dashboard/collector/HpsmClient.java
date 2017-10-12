package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.ChangeOrder;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Incident;

import java.util.List;

/**
 * Client for fetching configuration item data from HPSM
 */
public interface HpsmClient {

    /**
     * Fetch all of the Apps

     * @return all Apps in HPSM
     */

	List<Cmdb> getApps();

	List<Incident> getIncidents(String assignmentGroup);

	List<ChangeOrder> getChangeOrders(String assignmentGroup);

    void setLastExecuted(long lastExecuted);

    long getLastExecuted();

	void setIncidentCount(long incidentCount);

	long getIncidentCount();
}
