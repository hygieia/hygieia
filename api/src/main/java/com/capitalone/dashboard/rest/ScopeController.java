package com.capitalone.dashboard.rest;


import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.service.ScopeService;
import com.capitalone.dashboard.util.PaginationHeaderUtility;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
	private PaginationHeaderUtility paginationHeaderUtility;

	@Autowired
	public ScopeController(ScopeService featureService,PaginationHeaderUtility paginationHeaderUtility) {
		this.scopeService = featureService;
		this.paginationHeaderUtility = paginationHeaderUtility;
	}

	/**
	 * REST endpoint for retrieving all features for a given sprint and team
	 * (the sprint is derived)
	 *
	 * @param scopeId
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
	 *
	 * @param collectorId
	 *
	 * @return projects
	 */
	@RequestMapping(value = "/scopecollector/{collectorId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public List<Scope> teamsByCollector(
			@PathVariable String collectorId) {
		return this.scopeService.getScopesByCollector(new ObjectId(collectorId));
	}


	/**
	 * REST endpoint for retrieving all features for a given sprint and team
	 * (the sprint is derived)
	 *
	 * @return scopes
	 */
	@RequestMapping(value = "/scope", method = GET, produces = APPLICATION_JSON_VALUE)
	public List<Scope> allScopes() {
		return this.scopeService.getAllScopes();
	}

	/**
	 *
	 * @param collectorId, search criteria, pageable
	 *
	 * @return List of scope
	 */
	@RequestMapping(value = "/scopecollector/page/{collectorId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Scope>> teamsByCollectorPage(
			@PathVariable String collectorId,@RequestParam(value = "search", required = false, defaultValue = "") String descriptionFilter, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
		Page<Scope> pageOfScopeItems = scopeService.getScopeByCollectorWithFilter(new ObjectId(collectorId),descriptionFilter,pageable);
		return ResponseEntity
				.ok()
				.headers(paginationHeaderUtility.buildPaginationHeaders(pageOfScopeItems))
				.body(pageOfScopeItems.getContent());
	}

}
