package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Cmdb;

import java.util.List;

/**
 * Client for fetching commit history from GitHub
 */
public interface HpsmClient {

    /**
     * Fetch all of the Apps

     * @return all Apps in HPSM
     */

	List<Cmdb> getApps();

}
