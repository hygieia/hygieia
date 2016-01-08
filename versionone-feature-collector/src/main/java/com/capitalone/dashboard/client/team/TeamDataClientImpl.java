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

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.Constants;
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
public class TeamDataClientImpl extends TeamDataClientSetupImpl implements TeamDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamDataClientImpl.class);
	private static final ClientUtil TOOLS = new ClientUtil();

	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
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
			FeatureSettings featureSettings, ScopeOwnerRepository teamRepository,
			VersionOneDataFactoryImpl vOneApi) {
		super(featureSettings, teamRepository, featureCollectorRepository, vOneApi);
		LOGGER.debug("Constructing data collection for the feature widget, story-level data...");

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
	 * @return
	 * @return
	 */
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		try {
			JSONObject dataMainObj = new JSONObject();
			for (int i = 0; i < tmpMongoDetailArray.size(); i++) {
				if (dataMainObj != null) {
					dataMainObj.clear();
				}
				dataMainObj = (JSONObject) tmpMongoDetailArray.get(i);
				ScopeOwnerCollectorItem team = new ScopeOwnerCollectorItem();

				boolean deleted = this.removeExistingEntity(TOOLS
						.sanitizeResponse((String) dataMainObj.get("_oid")));
				// Id
				if (deleted) {
					team.setId(this.getOldTeamId());
					team.setEnabled(this.isOldTeamEnabledState());
				}

				// collectorId
				team.setCollectorId(featureCollectorRepository.findByName(Constants.VERSIONONE)
						.getId());

				// teamId
				team.setTeamId(TOOLS.sanitizeResponse((String) dataMainObj.get("_oid")));

				// name
				team.setName(TOOLS.sanitizeResponse((String) dataMainObj.get("Name")));

				// changeDate;
				team.setChangeDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj.get("ChangeDate"))));

				// assetState
				team.setAssetState(TOOLS.sanitizeResponse((String) dataMainObj.get("AssetState")));

				// isDeleted;
				team.setIsDeleted(TOOLS.sanitizeResponse((String) dataMainObj.get("IsDeleted")));

				try {
					teamRepo.save(team);
				} catch (Exception e) {
					LOGGER.error(
							"Unexpected error caused when attempting to save data\nCaused by: "
									+ e.getCause(), e);
				}
			}
		} catch (Exception e) {
			LOGGER.error("FAILED: " + e.getMessage() + ", " + e.getClass(), e);
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
