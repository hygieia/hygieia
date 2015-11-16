/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.client.team;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
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
			FeatureSettings featureSettings, TeamRepository teamRepository,
			VersionOneDataFactoryImpl vOneApi) {
		super(featureSettings, teamRepository, featureCollectorRepository,
				vOneApi);
		logger.debug("Constructing data collection for the feature widget, story-level data...");

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
	 * @return
	 * @return
	 */
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		try {
			JSONObject dataMainObj = new JSONObject();
			System.out.println("Size of PagingJSONArray: "
					+ tmpMongoDetailArray.size());
			for (int i = 0; i < tmpMongoDetailArray.size(); i++) {
				if (dataMainObj != null) {
					dataMainObj.clear();
				}
				dataMainObj = (JSONObject) tmpMongoDetailArray.get(i);
				TeamCollectorItem team = new TeamCollectorItem();

				@SuppressWarnings("unused")
				boolean deleted = this.removeExistingEntity(tools
						.sanitizeResponse((String) dataMainObj.get("_oid")));

				// collectorId
				team.setCollectorId(featureCollectorRepository.findByName(
						"VersionOne").getId());

				// teamId
				team.setTeamId(tools.sanitizeResponse((String) dataMainObj
						.get("_oid")));

				// name
				team.setName(tools.sanitizeResponse((String) dataMainObj
						.get("Name")));

				// changeDate;
				team.setChangeDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("ChangeDate"))));

				// assetState
				team.setAssetState(tools.sanitizeResponse((String) dataMainObj
						.get("AssetState")));

				// isDeleted;
				team.setIsDeleted(tools.sanitizeResponse((String) dataMainObj
						.get("IsDeleted")));

				try {
					teamRepo.save(team);
				} catch (Exception e) {
					logger.error("Unexpected error caused when attempting to save data\nCaused by: "
							+ e.getCause());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.error("FAILED: " + e.getMessage() + ", " + e.getClass());
			e.printStackTrace();
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
		super.query = this.featureWidgetQueries.getQuery(returnDate, queryName);
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
