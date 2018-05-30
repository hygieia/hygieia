package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
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

	List<Cmdb> getApps() throws HygieiaException;

	List<Incident> getIncidents() throws HygieiaException;

	List<ChangeOrder> getChangeOrders() throws HygieiaException;

    void setLastExecuted(long lastExecuted);

    long getLastExecuted();

	void setIncidentCount(long incidentCount);

	long getIncidentCount();

	void setChangeCount(long changeCount);

	long getChangeCount();

}
