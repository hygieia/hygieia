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

import java.util.Iterator;
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
	private final static ClientUtil TOOLS = new ClientUtil();

	/**
	 * Extends the constructor from the super class.
	 *
	 */
	public ProjectDataClientImpl(FeatureSettings featureSettings,
			ScopeRepository projectRepository, FeatureCollectorRepository featureCollectorRepository) {
		super(featureSettings, projectRepository, featureCollectorRepository);
		LOGGER.debug("Constructing data collection for the feature widget, project-level data...");

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
		LOGGER.debug("Size of paged Jira response: ", currentPagedJiraRs.size());
		if ((currentPagedJiraRs != null) && !(currentPagedJiraRs.isEmpty())) {
			Iterator<BasicProject> globalResponseItr = currentPagedJiraRs.iterator();
			while (globalResponseItr.hasNext()) {
				try {
					/*
					 * Initialize DOMs
					 */
					Scope scope = new Scope();
					BasicProject jiraScope = globalResponseItr.next();

					/*
					 * Removing any existing entities where they exist in the
					 * local DB store...
					 */
					@SuppressWarnings("unused")
					boolean deleted = this.removeExistingEntity(TOOLS.sanitizeResponse(jiraScope
							.getId()));

					/*
					 * Project Data
					 */
					// collectorId
					scope.setCollectorId(featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA)
							.getId());

					// ID;
					scope.setpId(TOOLS.sanitizeResponse(jiraScope.getId()));

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

				} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
					LOGGER.error(
							"Unexpected error caused while mapping data from source system to local data store:\n"
									+ e.getMessage() + " : " + e.getCause(), e);
				}
			}
		}
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public void updateProjectInformation() {
		super.objClass = Scope.class;
		super.returnDate = this.featureSettings.getDeltaStartDate();
		if (super.getMaxChangeDate() != null) {
			super.returnDate = super.getMaxChangeDate();
		}
		super.returnDate = getChangeDateMinutePrior(super.returnDate);
		updateObjectInformation();
	}

	/**
	 * Validates current entry and removes new entry if an older item exists in
	 * the repo
	 * 
	 * @param localId repository item ID (not the precise mongoID)
	 */
	protected Boolean removeExistingEntity(String localId) {
		boolean deleted = false;

		try {
			ObjectId tempEntId = projectRepo.getScopeIdById(localId).get(0).getId();
			if (localId.equalsIgnoreCase(projectRepo.getScopeIdById(localId).get(0).getpId())) {
				projectRepo.delete(tempEntId);
				deleted = true;
			}
		} catch (IndexOutOfBoundsException ioobe) {
			LOGGER.debug("Nothing matched the redundancy checking from the database", ioobe);
		} catch (Exception e) {
			LOGGER.error("There was a problem validating the redundancy of the data model", e);
		}

		return deleted;
	}
}
