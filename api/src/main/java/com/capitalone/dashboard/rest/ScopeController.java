package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.service.ScopeService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * REST service managing all requests to the feature repository.
 *
 * @author KFK884
 *
 */
@RestController
public class ScopeController {
	private final ScopeService scopeService;

	@Autowired
	public ScopeController(ScopeService featureService) {
		this.scopeService = featureService;
	}

	/**
	 * REST endpoint for retrieving all features for a given sprint and team
	 * (the sprint is derived)
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing all features for
	 *         the given team and current sprint
	 */
	@RequestMapping(value = "/scope/{scopeId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<List<Scope>> scope(
			@RequestParam(value = "component", required = true) String cId,
			@PathVariable String scopeId) {
		ObjectId componentId = new ObjectId(cId);
		return this.scopeService.getScope(componentId, scopeId);
	}

	/**
	 * REST endpoint for retrieving all features for a given sprint and team
	 * (the sprint is derived)
	 *
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing all features for
	 *         the given team and current sprint
	 */
	@RequestMapping(value = "/scope", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<List<Scope>> allScopes(
			@RequestParam(value = "component", required = true) String cId) {
		ObjectId componentId = new ObjectId(cId);
		return this.scopeService.getAllScopes(componentId);
	}
}
