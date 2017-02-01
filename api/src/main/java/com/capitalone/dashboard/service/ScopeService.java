package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Scope;

import java.util.List;

import org.bson.types.ObjectId;

public interface ScopeService {
	/**
	 * Retrieves all unique scopes
	 * 
	 * @return A data response list of type Scope containing all unique scopes
	 */
	List<Scope> getAllScopes();

	/**
	 * Retrieves the scope information for a given scope source system ID
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param scopeId
	 *            A given scope's source-system ID
	 * 
	 * @return A data response list of type Scope containing all data for a
	 *         given scope source-system ID
	 */
	DataResponse<List<Scope>> getScope(ObjectId componentId, String scopeId);

	/**
	 *
	 * @param collectorId
	 * @return scopes
	 */
	List<Scope> getScopesByCollector(ObjectId collectorId);
}
