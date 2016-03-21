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
public class TeamDataClientImpl extends TeamDataClientSetupImpl implements TeamDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamDataClientImpl.class);
	private static final ClientUtil TOOLS = new ClientUtil();

	private final FeatureSettings featureSettings;
	private final ScopeOwnerRepository teamRepo;
	private final FeatureCollectorRepository featureCollectorRepository;

	private ObjectId oldTeamId;
	private boolean oldTeamEnabledState;

	/**
	 * Extends the constructor from the super class.
	 * 
	 * @param teamRepository
	 */
	public TeamDataClientImpl(FeatureCollectorRepository featureCollectorRepository,
			FeatureSettings featureSettings, ScopeOwnerRepository teamRepository) {
		super(featureSettings, teamRepository, featureCollectorRepository);
		LOGGER.debug("Constructing data collection for the feature widget, team-level data...");

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
		LOGGER.debug("Size of paged Jira response: ", currentPagedJiraRs.size());
		if ((currentPagedJiraRs != null) && !(currentPagedJiraRs.isEmpty())) {
			Iterator<BasicProject> globalResponseItr = currentPagedJiraRs.iterator();
			while (globalResponseItr.hasNext()) {
				try {
					/*
					 * Initialize DOMs
					 */
					ScopeOwnerCollectorItem team = new ScopeOwnerCollectorItem();
					BasicProject jiraTeam = globalResponseItr.next();

					/*
					 * Removing any existing entities where they exist in the
					 * local DB store...
					 */
					boolean deleted = this.removeExistingEntity(TOOLS.sanitizeResponse(jiraTeam
							.getId()));

					/*
					 * Team Data
					 */
					// Id
					if (deleted) {
						team.setId(this.getOldTeamId());
						team.setEnabled(this.isOldTeamEnabledState());
					}

					// collectorId
					team.setCollectorId(featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA)
							.getId());

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
	public void updateTeamInformation() {
		super.objClass = ScopeOwnerCollectorItem.class;
		super.returnDate = this.featureSettings.getDeltaCollectorItemStartDate();
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
			ObjectId tempEntId = teamRepo.getTeamIdById(localId).get(0).getId();
			if (localId.equalsIgnoreCase(teamRepo.getTeamIdById(localId).get(0).getTeamId())) {
				this.setOldTeamId(tempEntId);
				this.setOldTeamEnabledState(teamRepo.getTeamIdById(localId).get(0).isEnabled());

				teamRepo.delete(tempEntId);
				deleted = true;
			}
		} catch (IndexOutOfBoundsException ioobe) {
			LOGGER.debug("Nothing matched the redundancy checking from the database", ioobe);
		} catch (Exception e) {
			LOGGER.error("There was a problem validating the redundancy of the data model", e);
		}

		return deleted;
	}

	private ObjectId getOldTeamId() {
		return oldTeamId;
	}

	private void setOldTeamId(ObjectId oldTeamId) {
		this.oldTeamId = oldTeamId;
	}

	private boolean isOldTeamEnabledState() {
		return oldTeamEnabledState;
	}

	private void setOldTeamEnabledState(boolean oldTeamEnabledState) {
		this.oldTeamEnabledState = oldTeamEnabledState;
	}
}
