package com.capitalone.dashboard.client.project;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.FeatureSettings;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This is the primary implemented/extended data collector for the feature
 * collector. This will get data from the source system, but will grab the
 * majority of needed data and aggregate it in a single, flat MongoDB collection
 * for consumption.
 * 
 * @author kfk884
 * 
 */
public class ProjectDataClientImpl extends ProjectDataClientSetupImpl implements ProjectDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDataClientImpl.class);

	private final FeatureSettings featureSettings;
	private final ScopeRepository projectRepo;
	private final static ClientUtil TOOLS = ClientUtil.getInstance();

	/**
	 * Extends the constructor from the super class.
	 *
	 */
	public ProjectDataClientImpl(FeatureSettings featureSettings,
			ScopeRepository projectRepository, FeatureCollectorRepository featureCollectorRepository) {
		super(featureSettings, projectRepository, featureCollectorRepository);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Constructing data collection for the feature widget, project-level data...");
		}

		this.featureSettings = featureSettings;
		this.projectRepo = projectRepository;
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 * 
	 * @param currentPagedJiraRs
	 *            A list response of Jira issues from the source system
	 */
	@Override
	protected void updateMongoInfo(List<BasicProject> currentPagedJiraRs) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Size of paged Jira response: " + (currentPagedJiraRs == null? 0 : currentPagedJiraRs.size()));
		}
		
		if (currentPagedJiraRs != null) {
			ObjectId jiraCollectorId = featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA).getId();
			for (BasicProject jiraScope : currentPagedJiraRs) {
				String scopeId = TOOLS.sanitizeResponse(jiraScope.getId());
				
				/*
				 * Initialize DOMs
				 */
				Scope scope = findOneScope(scopeId);
				
				if (scope == null) {
					scope = new Scope();
				}

				/*
				 * Project Data
				 */
				// collectorId
				scope.setCollectorId(jiraCollectorId);

				// ID;
				scope.setpId(TOOLS.sanitizeResponse(scopeId));

				// name;
				scope.setName(TOOLS.sanitizeResponse(jiraScope.getName()));

				// beginDate - does not exist for jira
				scope.setBeginDate("");

				// endDate - does not exist for jira
				scope.setEndDate("");

				// changeDate - does not exist for jira
				scope.setChangeDate("");

				// assetState - does not exist for jira
				scope.setAssetState("Active");

				// isDeleted - does not exist for jira
				scope.setIsDeleted("False");

				// path - does not exist for Jira
				scope.setProjectPath(TOOLS.sanitizeResponse(jiraScope.getName()));

				// Saving back to MongoDB
				projectRepo.save(scope);
				
			}
		}
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public int updateProjectInformation() {
		super.objClass = Scope.class;
		super.returnDate = this.featureSettings.getDeltaStartDate();
		if (super.getMaxChangeDate() != null) {
			super.returnDate = super.getMaxChangeDate();
		}
		super.returnDate = getChangeDateMinutePrior(super.returnDate);
		return updateObjectInformation();
	}
	
	/**
	 * Find the current collector item for the jira team id
	 * 
	 * @param teamId	the team id
	 * @return			the collector item if it exists or null
	 */
	protected Scope findOneScope(String scopeId) {
		List<Scope> scopes = projectRepo.getScopeIdById(scopeId);
		
		// Not sure of the state of the data
		if (scopes.size() > 1) {
			LOGGER.warn("More than one collector item found for scopeId " + scopeId);
		}
		
		if (!scopes.isEmpty()) {
			return scopes.get(0);
		}
		
		return null;
	}
}
