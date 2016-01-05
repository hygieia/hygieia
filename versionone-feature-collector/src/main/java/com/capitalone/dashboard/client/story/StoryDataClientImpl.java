package com.capitalone.dashboard.client.story;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.Constants;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
public class StoryDataClientImpl extends FeatureDataClientSetupImpl implements
		StoryDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoryDataClientImpl.class);

	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	private final FeatureCollectorRepository featureCollectorRepository;
	private final FeatureRepository featureRepo;
	private static final ClientUtil TOOLS = new ClientUtil();

	/**
	 * Extends the constructor from the super class.
	 *
	 * @param teamRepository
	 */
	public StoryDataClientImpl(FeatureSettings featureSettings,
			FeatureRepository featureRepository,
			FeatureCollectorRepository featureCollectorRepository,
			VersionOneDataFactoryImpl vOneApi) {
		super(featureSettings, featureRepository, featureCollectorRepository,
				vOneApi);
		LOGGER.debug("Constructing data collection for the feature widget, story-level data...");

		this.featureSettings = featureSettings;
		this.featureRepo = featureRepository;
		this.featureCollectorRepository = featureCollectorRepository;
		this.featureWidgetQueries = new FeatureWidgetQueries(
				this.featureSettings);
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
	@SuppressWarnings({"unchecked", "PMD.ExcessiveMethodLength", "PMD.AvoidCatchingNPE",
			"PMD.NPathComplexity"})
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		try {
			JSONObject dataMainObj = new JSONObject();
			JSONObject tmpObj = new JSONObject();

			for (int i = 0; i < tmpMongoDetailArray.size(); i++) {
				if (dataMainObj != null) {
					dataMainObj.clear();
				}
				dataMainObj = (JSONObject) tmpMongoDetailArray.get(i);
				Feature feature = new Feature();

				@SuppressWarnings("unused")
				boolean deleted = this.removeExistingEntity(TOOLS
						.sanitizeResponse((String) dataMainObj.get("_oid")));

				// collectorId
				feature.setCollectorId(featureCollectorRepository.findByName(
						Constants.VERSIONONE).getId());

				// ID
				feature.setsId(TOOLS.sanitizeResponse((String) dataMainObj
						.get("_oid")));

				// sNumber
				feature.setsNumber(TOOLS.sanitizeResponse((String) dataMainObj
						.get("Number")));

				// sName
				feature.setsName(TOOLS.sanitizeResponse((String) dataMainObj
						.get("Name")));

				// sStatus
				feature.setsStatus(TOOLS.sanitizeResponse((String) dataMainObj
						.get("Status.Name")));

				// sState
				feature.setsState(TOOLS.sanitizeResponse((String) dataMainObj
						.get("AssetState")));

				// sSoftwareTesting
				try {
					if ((String) dataMainObj.get("Custom_SoftwareTesting.Name") != null) {
						feature.setsSoftwareTesting(TOOLS
								.sanitizeResponse((String) dataMainObj
										.get("Custom_SoftwareTesting.Name")));
					} else {
						feature.setsSoftwareTesting("");
					}
				} catch (NullPointerException npe) {
					feature.setsSoftwareTesting("");
				}

				// sEstimate
				feature.setsEstimate(TOOLS
						.sanitizeResponse((String) dataMainObj.get("Estimate")));

				// sChangeDate
				feature.setChangeDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("ChangeDate"))));

				// IsDeleted
				feature.setIsDeleted(TOOLS
						.sanitizeResponse((String) dataMainObj.get("IsDeleted")));

				// sProjectID
				tmpObj = (JSONObject) dataMainObj.get("Scope.ID");
				if (tmpObj.containsKey("_oid")) {
					feature.setsProjectID(TOOLS
							.sanitizeResponse((String) (tmpObj.get("_oid"))));
				}

				// sProjectName
				feature.setsProjectName(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Scope.Name")));

				// sProjectBeginDate
				feature.setsProjectBeginDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Scope.BeginDate"))));

				// sProjectEndDate
				feature.setsProjectEndDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Scope.EndDate"))));

				// sProjectChangeDate
				feature.setsProjectChangeDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Scope.ChangeDate"))));

				// sProjectState
				feature.setsProjectState(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Scope.AssetState")));

				// sProjectIsDeleted
				feature.setsProjectIsDeleted(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Scope.IsDeleted")));

				// sProjectPath
				String projPath = new String(feature.getsProjectName());
				List<String> projList = (List<String>) dataMainObj.get("Scope.ParentAndUp.Name");
				if (projList != null) {
					for (String proj : projList) {
						projPath = proj + "-->" + projPath;
					}
					projPath = "All-->" + projPath;
				} else {
					projPath = "All-->" + projPath;
				}
				feature.setsProjectPath(TOOLS.sanitizeResponse(projPath));

				// sEpicID
				tmpObj = (JSONObject) dataMainObj.get("Super.ID");
				if (tmpObj.containsKey("_oid")) {
					feature.setsEpicID(TOOLS.sanitizeResponse((String) (tmpObj)
							.get("_oid")));
				}

				// sEpicNumber
				feature.setsEpicNumber(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.Number")));

				// sEpicName
				feature.setsEpicName(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.Name")));

				// sEpicPDD
				feature.setsEpicPDD(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.Custom_ProductionDeploymentDate"))));

				// sEpicHPSMReleaseID
				feature.setsEpicHPSMReleaseID(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.Custom_HPSMReleaseID")));

				// sEpicBeginDate
				feature.setsEpicBeginDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.PlannedStart"))));

				// sEpicEndDate
				feature.setsEpicEndDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.PlannedEnd"))));

				// sEpicType
				feature.setsEpicType(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.Category.Name")));

				// sEpicAssetState
				feature.setsEpicAssetState(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.AssetState")));

				// sEpicChangeDate
				feature.setsEpicChangeDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.ChangeDate"))));

				// sEpicIsDeleted
				feature.setsEpicIsDeleted(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Super.IsDeleted")));

				// sSprintID
				tmpObj = (JSONObject) dataMainObj.get("Timebox.ID");
				if (tmpObj.containsKey("_oid")) {
					feature.setsSprintID(TOOLS
							.sanitizeResponse((String) (tmpObj).get("_oid")));
				}

				// sSprintName
				feature.setsSprintName(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.Name")));

				// sSprintBeginDate
				feature.setsSprintBeginDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.BeginDate"))));

				// sSprintEndDate
				feature.setsSprintEndDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.EndDate"))));

				// sSprintAssetState
				feature.setsSprintAssetState(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.AssetState")));

				// sSprintChangeDate
				feature.setsSprintChangeDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.ChangeDate"))));

				// sSprintIsDeleted
				feature.setsSprintIsDeleted(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.IsDeleted")));

				// sTeamID
				tmpObj = (JSONObject) dataMainObj.get("Team.ID");
				if (tmpObj.containsKey("_oid")) {
					feature.setsTeamID(TOOLS.sanitizeResponse((String) (tmpObj)
							.get("_oid")));
				}

				// sTeamName
				feature.setsTeamName(TOOLS
						.sanitizeResponse((String) dataMainObj.get("Team.Name")));

				// sTeamChangeDate
				feature.setsTeamChangeDate(TOOLS.toCanonicalDate(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Team.ChangeDate"))));

				// sTeamAssetState
				feature.setsTeamAssetState(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Team.AssetState")));

				// sTeamIsDeleted
				feature.setsTeamIsDeleted(TOOLS
						.sanitizeResponse((String) dataMainObj
								.get("Team.IsDeleted")));

				// sOwnersID
				List<String> ownersIdList = new ArrayList<String>();
				List<JSONObject> ownersList = new ArrayList<JSONObject>();
				try {
					ownersList = (JSONArray) dataMainObj.get("Owners.ID");
					for (JSONObject ownersID : ownersList) {
						ownersIdList.add((String) ownersID.get("_oid"));
					}
				} catch (NullPointerException npe) {
					LOGGER.debug("JSON Object field was empty, so it was filled accordingly");
				} finally {
					feature.setsOwnersID(ownersIdList);
				}

				// sOwnersShortName
				feature.setsOwnersShortName(TOOLS
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.Nickname")));

				// sOwnersFullName
				feature.setsOwnersFullName(TOOLS
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.Name")));

				// sOwnersUsername
				feature.setsOwnersUsername(TOOLS
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.Username")));

				// sOwnersState
				feature.setsOwnersState(TOOLS
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.AssetState")));

				// sOwnersChangeDate
				feature.setsOwnersChangeDate(TOOLS
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.ChangeDate")));

				// sOwnersIsDeleted
				feature.setsOwnersIsDeleted(TOOLS
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.IsDeleted")));

				try {
					featureRepo.save(feature);
				} catch (Exception e) {
					LOGGER.error("Unexpected error caused when attempting to save data\nCaused by: "
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
	public void updateStoryInformation() {
		super.objClass = Feature.class;
		super.returnDate = this.featureSettings.getDeltaStartDate();
		if (super.getMaxChangeDate() != null) {
			super.returnDate = super.getMaxChangeDate();
		}
		super.returnDate = getChangeDateMinutePrior(super.returnDate);
		String queryName = this.featureSettings.getStoryQuery();
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
			List<Feature> listOfFeature = featureRepo.getFeatureIdById(localId);
			for (Feature f : listOfFeature) {
				featureRepo.delete(f.getId());
				deleted = true;
				LOGGER.debug("Removed existing entities that will be replaced by newer instances");
			}
		} catch (IndexOutOfBoundsException ioobe) {
			LOGGER.debug("Nothing matched the redundancy checking from the database", ioobe);
		} catch (Exception e) {
			LOGGER.error("There was a problem validating the redundancy of the data model", e);
		}

		return deleted;
	}
}
