/*************************
 * DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************DA-BOARD-LICENSE-END
 *********************************/

package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
import com.capitalone.dashboard.util.DateUtil;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * This is the primary implemented/extended data collector for the feature
 * collector. This will get data from the source system, but will grab the
 * majority of needed data and aggregate it in a single, flat MongoDB collection
 * for consumption.
 *
 * @author kfk884
 */
public class TeamDataClient extends BaseClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamDataClient.class);

	private final ScopeOwnerRepository teamRepo;
	private final FeatureCollectorRepository featureCollectorRepository;
	private ObjectId oldTeamId;
	private boolean oldTeamEnabledState;

	/**
	 * Extends the constructor from the super class.
	 *
	 *
	 */
	public TeamDataClient(FeatureCollectorRepository featureCollectorRepository,
			FeatureSettings featureSettings, ScopeOwnerRepository teamRepository,
			VersionOneDataFactoryImpl vOneApi) {
        super(vOneApi, featureSettings);
        LOGGER.debug("Constructing data collection for the feature widget, story-level data...");
		this.featureCollectorRepository = featureCollectorRepository;
		this.teamRepo = teamRepository;
		teamRepo.delete("Closed");
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 *
	 * @param tmpMongoDetailArray
	 *            A JSON response in JSONArray format from the source system
	 *
	 */
    @Override
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		for (Object obj : tmpMongoDetailArray) {
			JSONObject dataMainObj = (JSONObject) obj;
			ScopeOwnerCollectorItem team = new ScopeOwnerCollectorItem();
			/*
			 * Checks to see if the available asset state is not active from the
			 * V1 Response and removes it if it exists and not active:
			 */
			if (!getJSONString(dataMainObj, "AssetState").equalsIgnoreCase("Active")) {
				this.removeInactiveScopeOwnerByTeamId(getJSONString(dataMainObj, "_oid"));
			} else {
				if (removeExistingEntity(getJSONString(dataMainObj, "_oid"))) {
					team.setId(this.getOldTeamId());
					team.setEnabled(this.isOldTeamEnabledState());
				}
				// collectorId
				team.setCollectorId(
						featureCollectorRepository.findByName(FeatureCollectorConstants.VERSIONONE).getId());
				// teamId
				team.setTeamId(getJSONString(dataMainObj, "_oid"));
				// name
				team.setName(getJSONString(dataMainObj, "Name"));
				// changeDate;
				team.setChangeDate(
						getJSONString(dataMainObj, "ChangeDate"));
				// assetState
				team.setAssetState(getJSONString(dataMainObj, "AssetState"));
				// isDeleted;
				team.setIsDeleted(getJSONString(dataMainObj, "IsDeleted"));
				teamRepo.save(team);
			}
		}
	}

	/**
	 * Removes scope-owners (teams) from the collection which have went to an
	 * non-active state
	 *
	 * @param teamId
	 *            A given Team ID that went inactive and should be removed from
	 *            the data collection
	 */

	private void removeInactiveScopeOwnerByTeamId(String teamId) {
		if (!StringUtils.isEmpty(teamId)
				&& !CollectionUtils.isEmpty(teamRepo.getTeamIdById(teamId))) {
			ObjectId inactiveTeamId = teamRepo.getTeamIdById(teamId).get(0).getId();
			if (inactiveTeamId != null) {
				teamRepo.delete(inactiveTeamId);
			}
		}
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public void updateTeamInformation() throws HygieiaException {
		// super.objClass = ScopeOwnerCollectorItem.class;
		String returnDate = getFeatureSettings().getDeltaCollectorItemStartDate();
		if (!StringUtils.isEmpty(getMaxChangeDate())) {
			returnDate = getMaxChangeDate();
		}
		returnDate = DateUtil.getChangeDateMinutePrior(returnDate,
				getFeatureSettings().getScheduledPriorMin());
		String queryName = getFeatureSettings().getTeamQuery();
		String query = getQuery(returnDate, queryName);
		updateObjectInformation(query);
	}

	/**
	 * Validates current entry and removes new entry if an older item exists in
	 * the repo
	 *
	 * @param localId
	 *            local repository item ID (not the precise mongoID)
	 */
	protected Boolean removeExistingEntity(String localId) {
		if (StringUtils.isEmpty(localId))
			return false;
		List<ScopeOwnerCollectorItem> teamIdList = teamRepo.getTeamIdById(localId);
		if (CollectionUtils.isEmpty(teamIdList))
			return false;
		ScopeOwnerCollectorItem socItem = teamIdList.get(0);
		if (!localId.equalsIgnoreCase(socItem.getTeamId()))
			return false;

		this.setOldTeamId(socItem.getId());
		this.setOldTeamEnabledState(socItem.isEnabled());
		teamRepo.delete(socItem.getId());
		return true;

	}


	/**
	 * Retrieves the maximum change date for a given query.
	 *
	 * @return A list object of the maximum change date
	 */
    @Override
	public String getMaxChangeDate() {
		Collector col = featureCollectorRepository.findByName(FeatureCollectorConstants.VERSIONONE);
		if (col == null)
			return "";
		if (StringUtils.isEmpty(getFeatureSettings().getDeltaCollectorItemStartDate()))
			return "";

		List<ScopeOwnerCollectorItem> response = teamRepo.findTopByChangeDateDesc(col.getId(),
				getFeatureSettings().getDeltaCollectorItemStartDate());
		if (!CollectionUtils.isEmpty(response))
			return response.get(0).getChangeDate();
		return "";
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
