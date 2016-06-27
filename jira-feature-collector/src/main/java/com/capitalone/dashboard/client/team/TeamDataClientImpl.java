package com.capitalone.dashboard.client.team;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
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
public class TeamDataClientImpl extends TeamDataClientSetupImpl implements TeamDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamDataClientImpl.class);
	private static final ClientUtil TOOLS = ClientUtil.getInstance();

	private final FeatureSettings featureSettings;
	private final ScopeOwnerRepository teamRepo;
	private final FeatureCollectorRepository featureCollectorRepository;

	/**
	 * Extends the constructor from the super class.
	 * 
	 * @param teamRepository
	 */
	public TeamDataClientImpl(FeatureCollectorRepository featureCollectorRepository,
			FeatureSettings featureSettings, ScopeOwnerRepository teamRepository) {
		super(featureSettings, teamRepository, featureCollectorRepository);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Constructing data collection for the feature widget, team-level data...");
		}

		this.featureSettings = featureSettings;
		this.featureCollectorRepository = featureCollectorRepository;
		this.teamRepo = teamRepository;
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
			
			for (BasicProject jiraTeam : currentPagedJiraRs) {
				String teamId = TOOLS.sanitizeResponse(jiraTeam.getId());
				
				/*
				 * Initialize DOMs
				 */
				ScopeOwnerCollectorItem team = findOneScopeOwnerCollectorItem(teamId);
				
				if (team == null) {
					team = new ScopeOwnerCollectorItem();
				}

				// collectorId
				team.setCollectorId(jiraCollectorId);

				// teamId
				team.setTeamId(TOOLS.sanitizeResponse(jiraTeam.getId()));

				// name
				team.setName(TOOLS.sanitizeResponse(jiraTeam.getName()));

				// changeDate - does not exist for jira
				team.setChangeDate("");

				// assetState - does not exist for jira
				team.setAssetState("Active");

				// isDeleted - does not exist for jira
				team.setIsDeleted("False");

				// Saving back to MongoDB
				teamRepo.save(team);
			}
		}
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public int updateTeamInformation() {
		super.objClass = ScopeOwnerCollectorItem.class;
		super.returnDate = this.featureSettings.getDeltaCollectorItemStartDate();
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
	protected ScopeOwnerCollectorItem findOneScopeOwnerCollectorItem(String teamId) {
		List<ScopeOwnerCollectorItem> scopeOwnerCollectorItems = teamRepo.getTeamIdById(teamId);
		
		// Not sure of the state of the data
		if (scopeOwnerCollectorItems.size() > 1) {
			LOGGER.warn("More than one collector item found for teamId " + teamId);
		}
		
		if (!scopeOwnerCollectorItems.isEmpty()) {
			return scopeOwnerCollectorItems.get(0);
		}
		
		return null;
	}
}