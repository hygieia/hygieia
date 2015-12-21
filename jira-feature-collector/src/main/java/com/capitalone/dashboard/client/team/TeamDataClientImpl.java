package com.capitalone.dashboard.client.team;

import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the primary implemented/extended data collector for the feature
 * collector. This will get data from the source system, but will grab the
 * majority of needed data and aggregate it in a single, flat MongoDB collection
 * for consumption.
 *
 * @author kfk884
 *
 */
public class TeamDataClientImpl extends TeamDataClientSetupImpl implements
		TeamDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamDataClientImpl.class);
	private static final ClientUtil TOOLS = new ClientUtil();

	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	private final ScopeOwnerRepository teamRepo;
	private final FeatureCollectorRepository featureCollectorRepository;

	/**
	 * Extends the constructor from the super class.
	 *
	 * @param teamRepository
	 */
	public TeamDataClientImpl(
			FeatureCollectorRepository featureCollectorRepository,
			FeatureSettings featureSettings, ScopeOwnerRepository teamRepository) {
		super(featureSettings, teamRepository, featureCollectorRepository);
		LOGGER.debug("Constructing data collection for the feature widget, team-level data...");

		this.featureSettings = featureSettings;
		this.featureCollectorRepository = featureCollectorRepository;
		this.teamRepo = teamRepository;
		this.featureWidgetQueries = new FeatureWidgetQueries(this.featureSettings);
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 *
	 * @param tmpMongoDetailArray
	 *            A JSON response in JSONArray format from the source system
	 * @param featureCollector
	 */
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		try {
			for (int i = 0; i < tmpMongoDetailArray.size(); i++) {
				JSONObject dataMainObj = (JSONObject) tmpMongoDetailArray.get(i);
				ScopeOwnerCollectorItem team = new ScopeOwnerCollectorItem();

				@SuppressWarnings("unused") //?
				boolean deleted = this.removeExistingEntity(TOOLS
						.sanitizeResponse(dataMainObj.get("id")));

				// collectorId
				team.setCollectorId(featureCollectorRepository.findByName(
						"Jira").getId());

				// teamId
				team.setTeamId(TOOLS.sanitizeResponse(dataMainObj.get("id")));

				// name
				team.setName(TOOLS.sanitizeResponse(dataMainObj.get("name")));

				// changeDate - does not exist for jira
				team.setChangeDate("");

				// assetState - does not exist for jira
				team.setAssetState("Active");

				// isDeleted - does not exist for jira
				team.setIsDeleted("False");

				try {
					teamRepo.save(team);
				} catch (Exception e) {
					LOGGER.error("Unexpected error caused when attempting to save data\nCaused by: "
							+ e.getMessage() + " : " + e.getCause(), e);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected error caused while mapping data from source system to local data store:\n"
					+ e.getMessage() + " : " + e.getCause(), e);
		}
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public void updateTeamInformation() {
		super.objClass = ScopeOwnerCollectorItem.class;
		super.returnDate = this.featureSettings
				.getDeltaCollectorItemStartDate();
		if (super.getMaxChangeDate() != null) {
			super.returnDate = super.getMaxChangeDate();
		}
		super.returnDate = getChangeDateMinutePrior(super.returnDate);
		String queryName = this.featureSettings.getTeamQuery();
		super.query = this.featureWidgetQueries.getQuery(queryName);
		LOGGER.debug("updateStoryInformation: queryName = " + query + "; query = " + query);
		updateObjectInformation();
	}

	/**
	 * Validates current entry and removes new entry if an older item exists in
	 * the repo
	 *
	 * @param A
	 *            local repository item ID (not the precise mongoID)
	 */
	protected Boolean removeExistingEntity(String localId) {
		boolean deleted = false;

		try {
			ObjectId tempEntId = teamRepo.getTeamIdById(localId).get(0).getId();
			if (localId.equalsIgnoreCase(teamRepo.getTeamIdById(localId).get(0)
					.getTeamId())) {
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
}
