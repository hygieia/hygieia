package com.capitalone.dashboard.client.project;

/**
 * Interface through which a project data collector object can be implemented.
 *
 * @author kfk884
 *
 */
public interface ProjectDataClient {
	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	void updateProjectInformation();
}
