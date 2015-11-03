package com.capitalone.dashboard.client.team;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.TeamCollectorItem;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;

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
	private static Log logger = LogFactory.getLog(TeamDataClientImpl.class);

	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	private final TeamRepository teamRepo;
	private final ClientUtil tools;
	private final FeatureCollectorRepository featureCollectorRepository;

	/**
	 * Extends the constructor from the super class.
	 *
	 * @param teamRepository
	 */
	public TeamDataClientImpl(
			FeatureCollectorRepository featureCollectorRepository,
			FeatureSettings featureSettings, TeamRepository teamRepository) {
		super(featureSettings, teamRepository, featureCollectorRepository);
		logger.debug("Constructing data collection for the feature widget, team-level data...");

		this.featureSettings = featureSettings;
		this.featureCollectorRepository = featureCollectorRepository;
		this.teamRepo = teamRepository;
		this.featureWidgetQueries = new FeatureWidgetQueries(
				this.featureSettings);
		tools = new ClientUtil();
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
			JSONObject dataMainObj = new JSONObject();
			for (int i = 0; i < tmpMongoDetailArray.size(); i++) {
				if (dataMainObj != null) {
					dataMainObj.clear();
				}
				dataMainObj = (JSONObject) tmpMongoDetailArray.get(i);
				TeamCollectorItem team = new TeamCollectorItem();

				@SuppressWarnings("unused") //?
				boolean deleted = this.removeExistingEntity(tools
						.sanitizeResponse(dataMainObj.get("id")));

				// collectorId
				team.setCollectorId(featureCollectorRepository.findByName(
						"Jira").getId());

				// teamId
				team.setTeamId(tools.sanitizeResponse(dataMainObj.get("id")));

				// name
				team.setName(tools.sanitizeResponse(dataMainObj.get("name")));

				// changeDate - does not exist for jira
				team.setChangeDate("");

				// assetState - does not exist for jira
				team.setAssetState("Active");

				// isDeleted - does not exist for jira
				team.setIsDeleted("False");

				try {
					teamRepo.save(team);
				} catch (Exception e) {
					logger.error("Unexpected error caused when attempting to save data\nCaused by:\n"
							+ e.getMessage()
							+ " : "
							+ e.getCause()
							+ "\n"
							+ Arrays.toString(e.getStackTrace()));
				}
			}
		} catch (Exception e) {
			logger.error("Unexpected error caused while mapping data from source system to local data store:\n"
					+ e.getMessage()
					+ " : "
					+ e.getCause()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public void updateTeamInformation() {
		super.objClass = TeamCollectorItem.class;
		super.returnDate = this.featureSettings
				.getDeltaCollectorItemStartDate();
		if (super.getMaxChangeDate() != null) {
			super.returnDate = super.getMaxChangeDate();
		}
		super.returnDate = getChangeDateMinutePrior(super.returnDate);
		String queryName = this.featureSettings.getTeamQuery();
		super.query = this.featureWidgetQueries.getQuery(queryName);
		logger.debug("updateStoryInformation: queryName = " + query + "; query = " + query);
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
			logger.debug("Nothing matched the redundancy checking from the database");
		} catch (Exception e) {
			logger.error("There was a problem validating the redundancy of the data model");
			e.printStackTrace();
		}

		return deleted;
	}
}
