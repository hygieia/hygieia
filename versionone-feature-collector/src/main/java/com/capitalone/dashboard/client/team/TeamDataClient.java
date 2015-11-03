package com.capitalone.dashboard.client.team;

/**
 * Interface through which a team data collector object can be implemented.
 *
 * @author kfk884
 *
 */
public interface TeamDataClient {
	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	void updateTeamInformation();
}
