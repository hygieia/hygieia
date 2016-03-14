package com.capitalone.dashboard.client;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.DateUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the primary implemented/extended data collector for the feature
 * collector. This will get data from the source system, but will grab the
 * majority of needed data and aggregate it in a single, flat MongoDB collection
 * for consumption.
 *
 * @author kfk884
 */
public class StoryDataClient extends BaseClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoryDataClient.class);

	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	private final FeatureCollectorRepository featureCollectorRepository;
	private final FeatureRepository featureRepo;
	private final VersionOneDataFactoryImpl vOneApi;

	/**
	 * Extends the constructor from the super class.
	 */
	public StoryDataClient(FeatureSettings featureSettings, FeatureRepository featureRepository,
			FeatureCollectorRepository featureCollectorRepository,
			VersionOneDataFactoryImpl vOneApi) {
		LOGGER.debug("Constructing data collection for the feature widget, story-level data...");

		this.featureSettings = featureSettings;
		this.featureRepo = featureRepository;
		this.featureCollectorRepository = featureCollectorRepository;
		this.featureWidgetQueries = new FeatureWidgetQueries(this.featureSettings);
		this.vOneApi = vOneApi;
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 *
	 * @param tmpMongoDetailArray
	 *            A JSON response in JSONArray format from the source system
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		for (Object obj : tmpMongoDetailArray) {
			JSONObject dataMainObj = (JSONObject) obj;
			Feature feature = new Feature();

			removeExistingEntity(getJSONString(dataMainObj, "_oid"));

			// collectorId
			feature.setCollectorId(
					featureCollectorRepository.findByName(FeatureCollectorConstants.VERSIONONE).getId());

			// ID
			feature.setsId(getJSONString(dataMainObj, "_oid"));

			// sNumber
			feature.setsNumber(getJSONString(dataMainObj, "Number"));

			// sName
			feature.setsName(getJSONString(dataMainObj, "Name"));

			// sStatus
			feature.setsStatus(getJSONString(dataMainObj, "Status.Name"));

			// sState
			feature.setsState(getJSONString(dataMainObj, "AssetState"));

			// sEstimate
			feature.setsEstimate(getJSONString(dataMainObj, "Estimate"));

			// sChangeDate
			feature.setChangeDate(getJSONDateString(dataMainObj, "ChangeDate"));

			// IsDeleted
			feature.setIsDeleted(getJSONString(dataMainObj, "IsDeleted"));

			// sProjectID
			JSONObject tmpObj = (JSONObject) dataMainObj.get("Scope.ID");
			if (tmpObj.containsKey("_oid")) {
				feature.setsProjectID(getJSONString(tmpObj, "_oid"));
			}

			// sProjectName
			feature.setsProjectName(getJSONString(dataMainObj, "Scope.Name"));

			// sProjectBeginDate
			feature.setsProjectBeginDate(getJSONDateString(dataMainObj, "Scope.BeginDate"));

			// sProjectEndDate
			feature.setsProjectEndDate(getJSONDateString(dataMainObj, "Scope.EndDate"));

			// sProjectChangeDate
			feature.setsProjectChangeDate(getJSONDateString(dataMainObj, "Scope.ChangeDate"));

			// sProjectState
			feature.setsProjectState(getJSONString(dataMainObj, "Scope.AssetState"));

			// sProjectIsDeleted
			feature.setsProjectIsDeleted(getJSONString(dataMainObj, "Scope.IsDeleted"));

			// sProjectPath
			String projPath = feature.getsProjectName();
			List<String> projList = (List<String>) dataMainObj.get("Scope.ParentAndUp.Name");
			if (!CollectionUtils.isEmpty(projList)) {
				for (String proj : projList) {
					projPath = proj + "-->" + projPath;
				}
				projPath = "All-->" + projPath;
			} else {
				projPath = "All-->" + projPath;
			}
			feature.setsProjectPath(ClientUtil.sanitizeResponse(projPath));

			// sEpicID
			tmpObj = (JSONObject) dataMainObj.get("Super.ID");
			if (tmpObj.containsKey("_oid")) {
				feature.setsEpicID(getJSONString(tmpObj, "_oid"));
			}

			// sEpicNumber
			feature.setsEpicNumber(getJSONString(dataMainObj, "Super.Number"));

			// sEpicName
			feature.setsEpicName(getJSONString(dataMainObj, "Super.Name"));

			// sEpicBeginDate
			feature.setsEpicBeginDate(getJSONDateString(dataMainObj, "Super.PlannedStart"));

			// sEpicEndDate
			feature.setsEpicEndDate(getJSONDateString(dataMainObj, "Super.PlannedEnd"));

			// sEpicType
			feature.setsEpicType(getJSONString(dataMainObj, "Super.Category.Name"));

			// sEpicAssetState
			feature.setsEpicAssetState(getJSONString(dataMainObj, "Super.AssetState"));

			// sEpicChangeDate
			feature.setsEpicChangeDate(getJSONDateString(dataMainObj, "Super.ChangeDate"));

			// sEpicIsDeleted
			feature.setsEpicIsDeleted(getJSONString(dataMainObj, "Super.IsDeleted"));

			// sSprintID
			tmpObj = (JSONObject) dataMainObj.get("Timebox.ID");
			feature.setsSprintID(getJSONString(tmpObj, "_oid"));

			// sSprintName
			feature.setsSprintName(getJSONString(dataMainObj, "Timebox.Name"));

			// sSprintBeginDate
			feature.setsSprintBeginDate(getJSONDateString(dataMainObj, "Timebox.BeginDate"));

			// sSprintEndDate
			feature.setsSprintEndDate(getJSONDateString(dataMainObj, "Timebox.EndDate"));

			// sSprintAssetState
			feature.setsSprintAssetState(getJSONString(dataMainObj, "Timebox.AssetState"));

			// sSprintChangeDate
			feature.setsSprintChangeDate(getJSONDateString(dataMainObj, "Timebox.ChangeDate"));

			// sSprintIsDeleted
			feature.setsSprintIsDeleted(getJSONString(dataMainObj, "Timebox.IsDeleted"));

			// sTeamID
			tmpObj = (JSONObject) dataMainObj.get("Team.ID");
			feature.setsTeamID(getJSONString(tmpObj, "_oid"));

			// sTeamName
			feature.setsTeamName(getJSONString(dataMainObj, "Team.Name"));

			// sTeamChangeDate
			feature.setsTeamChangeDate(getJSONDateString(dataMainObj, "Team.ChangeDate"));

			// sTeamAssetState
			feature.setsTeamAssetState(getJSONString(dataMainObj, "Team.AssetState"));

			// sTeamIsDeleted
			feature.setsTeamIsDeleted(getJSONString(dataMainObj, "Team.IsDeleted"));

			// sOwnersID
			List<String> ownersIdList = new ArrayList<>();
			for (Object ownersID : (JSONArray) dataMainObj.get("Owners.ID")) {
				ownersIdList.add(getJSONString((JSONObject) ownersID, "_oid"));
			}
			feature.setsOwnersID(ownersIdList);

			// sOwnersShortName
			feature.setsOwnersShortName(
					ClientUtil.toCanonicalList((List<String>) dataMainObj.get("Owners.Nickname")));

			// sOwnersFullName
			feature.setsOwnersFullName(
					ClientUtil.toCanonicalList((List<String>) dataMainObj.get("Owners.Name")));

			// sOwnersUsername
			feature.setsOwnersUsername(
					ClientUtil.toCanonicalList((List<String>) dataMainObj.get("Owners.Username")));

			// sOwnersState
			feature.setsOwnersState(ClientUtil
					.toCanonicalList((List<String>) dataMainObj.get("Owners.AssetState")));

			// sOwnersChangeDate
			feature.setsOwnersChangeDate(ClientUtil
					.toCanonicalList((List<String>) dataMainObj.get("Owners.ChangeDate")));

			// sOwnersIsDeleted
			feature.setsOwnersIsDeleted(
					ClientUtil.toCanonicalList((List<String>) dataMainObj.get("Owners.IsDeleted")));

			featureRepo.save(feature);
		}
	}

	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public void updateStoryInformation() throws HygieiaException {
		String returnDate = this.featureSettings.getDeltaStartDate();
		if (!StringUtils.isEmpty(getMaxChangeDate())) {
			returnDate = getMaxChangeDate();
		}
		returnDate = DateUtil.getChangeDateMinutePrior(returnDate,
				this.featureSettings.getScheduledPriorMin()); // getChangeDateMinutePrior(returnDate);
		String queryName = this.featureSettings.getStoryQuery();
		updateObjectInformation(featureWidgetQueries.getQuery(returnDate, queryName));
	}

	/**
	 * Validates current entry and removes new entry if an older item exists in
	 * the repo
	 *
	 * @param localId
	 *            local repository item ID (not the precise mongoID)
	 */
	protected void removeExistingEntity(String localId) {
		if (StringUtils.isEmpty(localId))
			return;
		List<Feature> listOfFeature = featureRepo.getFeatureIdById(localId);

		if (CollectionUtils.isEmpty(listOfFeature))
			return;
		featureRepo.delete(listOfFeature);
	}

	public String getMaxChangeDate() {
		Collector col = featureCollectorRepository.findByName(FeatureCollectorConstants.VERSIONONE);
		if (col == null)
			return "";
		if (StringUtils.isEmpty(featureSettings.getDeltaStartDate()))
			return "";

		List<Feature> response = featureRepo
				.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(col.getId(),
						featureSettings.getDeltaStartDate());
		if (!CollectionUtils.isEmpty(response))
			return response.get(0).getChangeDate();
		return "";
	}

	public void updateObjectInformation(String query) throws HygieiaException {
		long start = System.nanoTime();
		int pageIndex = 0;
		int pageSize = this.featureSettings.getPageSize();
		vOneApi.setPageSize(pageSize);
		vOneApi.buildBasicQuery(query);
		vOneApi.buildPagingQuery(0);
		JSONArray outPutMainArray = vOneApi.getPagingQueryResponse();
		if (!CollectionUtils.isEmpty(outPutMainArray)) {
			JSONArray tmpDetailArray = (JSONArray) outPutMainArray.get(0);
			while (!CollectionUtils.isEmpty(tmpDetailArray)) {
				updateMongoInfo(tmpDetailArray);
				pageIndex = pageIndex + pageSize;
				vOneApi.buildPagingQuery(pageIndex);
				outPutMainArray = vOneApi.getPagingQueryResponse();
				if (outPutMainArray == null) {
					LOGGER.info("FAILED: Script Completed with Error");
					throw new HygieiaException(
							"FAILED: Nothing to update from VersionOne's response",
							HygieiaException.NOTHING_TO_UPDATE);
				}
				tmpDetailArray = (JSONArray) outPutMainArray.get(0);
			}
		} else {
			throw new HygieiaException(
					"FAILED: FAILED: VersionOne response included unexpected JSON format",
					HygieiaException.JSON_FORMAT_ERROR);
		}

		double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
		LOGGER.info("Process took :" + elapsedTime + " seconds to update");
	}
}
