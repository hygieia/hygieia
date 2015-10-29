package com.capitalone.dashboard.client.story;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
	private static Log logger = LogFactory.getLog(StoryDataClientImpl.class);

	private final FeatureSettings featureSettings;
	private final FeatureWidgetQueries featureWidgetQueries;
	private final FeatureCollectorRepository featureCollectorRepository;
	private final FeatureRepository featureRepo;
	private final ClientUtil tools;

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
		logger.debug("Constructing data collection for the feature widget, story-level data...");

		this.featureSettings = featureSettings;
		this.featureRepo = featureRepository;
		this.featureCollectorRepository = featureCollectorRepository;
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
	@SuppressWarnings("unchecked")
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		try {
			JSONObject dataMainObj = new JSONObject();
			JSONObject tmpObj = new JSONObject();
			System.out.println("Size of PagingJSONArray: "
					+ tmpMongoDetailArray.size());
			for (int i = 0; i < tmpMongoDetailArray.size(); i++) {
				if (dataMainObj != null) {
					dataMainObj.clear();
				}
				dataMainObj = (JSONObject) tmpMongoDetailArray.get(i);
				Feature feature = new Feature();

				@SuppressWarnings("unused")
				boolean deleted = this.removeExistingEntity(tools
						.sanitizeResponse((String) dataMainObj.get("_oid")));

				// collectorId
				feature.setCollectorId(featureCollectorRepository.findByName(
						"VersionOne").getId());

				// ID
				feature.setsId(tools.sanitizeResponse((String) dataMainObj
						.get("_oid")));

				// sNumber
				feature.setsNumber(tools.sanitizeResponse((String) dataMainObj
						.get("Number")));

				// sName
				feature.setsName(tools.sanitizeResponse((String) dataMainObj
						.get("Name")));

				// sStatus
				feature.setsStatus(tools.sanitizeResponse((String) dataMainObj
						.get("Status.Name")));

				// sState
				feature.setsState(tools.sanitizeResponse((String) dataMainObj
						.get("AssetState")));

				// sSoftwareTesting
				try {
					if ((String) dataMainObj.get("Custom_SoftwareTesting.Name") != null) {
						feature.setsSoftwareTesting(tools
								.sanitizeResponse((String) dataMainObj
										.get("Custom_SoftwareTesting.Name")));
					} else {
						feature.setsSoftwareTesting("");
					}
				} catch (NullPointerException npe) {
					feature.setsSoftwareTesting("");
				}

				// sEstimate
				feature.setsEstimate(tools
						.sanitizeResponse((String) dataMainObj.get("Estimate")));

				// sChangeDate
				feature.setChangeDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("ChangeDate"))));

				// IsDeleted
				feature.setIsDeleted(tools
						.sanitizeResponse((String) dataMainObj.get("IsDeleted")));

				// sProjectID
				tmpObj = (JSONObject) dataMainObj.get("Scope.ID");
				if (tmpObj.containsKey("_oid")) {
					feature.setsProjectID(tools
							.sanitizeResponse((String) (tmpObj.get("_oid"))));
				}

				// sProjectName
				feature.setsProjectName(tools
						.sanitizeResponse((String) dataMainObj
								.get("Scope.Name")));

				// sProjectBeginDate
				feature.setsProjectBeginDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Scope.BeginDate"))));

				// sProjectEndDate
				feature.setsProjectEndDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Scope.EndDate"))));

				// sProjectChangeDate
				feature.setsProjectChangeDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Scope.ChangeDate"))));

				// sProjectState
				feature.setsProjectState(tools
						.sanitizeResponse((String) dataMainObj
								.get("Scope.AssetState")));

				// sProjectIsDeleted
				feature.setsProjectIsDeleted(tools
						.sanitizeResponse((String) dataMainObj
								.get("Scope.IsDeleted")));

				// sProjectPath
				List<String> projList = new ArrayList<String>();
				String projPath = new String(feature.getsProjectName());
				projList = (List<String>) dataMainObj
						.get("Scope.ParentAndUp.Name");
				if (projList.size() > 0) {
					for (String proj : projList) {
						projPath = proj + "-->" + projPath;
					}
					projPath = "All-->" + projPath;
				} else {
					projPath = "All-->" + projPath;
				}
				feature.setsProjectPath(tools.sanitizeResponse(projPath));

				// sEpicID
				tmpObj = (JSONObject) dataMainObj.get("Super.ID");
				if (tmpObj.containsKey("_oid")) {
					feature.setsEpicID(tools.sanitizeResponse((String) (tmpObj)
							.get("_oid")));
				}

				// sEpicNumber
				feature.setsEpicNumber(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.Number")));

				// sEpicName
				feature.setsEpicName(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.Name")));

				// sEpicPDD
				feature.setsEpicPDD(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.Custom_ProductionDeploymentDate"))));

				// sEpicHPSMReleaseID
				feature.setsEpicHPSMReleaseID(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.Custom_HPSMReleaseID")));

				// sEpicBeginDate
				feature.setsEpicBeginDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.PlannedStart"))));

				// sEpicEndDate
				feature.setsEpicEndDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.PlannedEnd"))));

				// sEpicType
				feature.setsEpicType(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.Category.Name")));

				// sEpicAssetState
				feature.setsEpicAssetState(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.AssetState")));

				// sEpicChangeDate
				feature.setsEpicChangeDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.ChangeDate"))));

				// sEpicIsDeleted
				feature.setsEpicIsDeleted(tools
						.sanitizeResponse((String) dataMainObj
								.get("Super.IsDeleted")));

				// sSprintID
				tmpObj = (JSONObject) dataMainObj.get("Timebox.ID");
				if (tmpObj.containsKey("_oid")) {
					feature.setsSprintID(tools
							.sanitizeResponse((String) (tmpObj).get("_oid")));
				}

				// sSprintName
				feature.setsSprintName(tools
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.Name")));

				// sSprintBeginDate
				feature.setsSprintBeginDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.BeginDate"))));

				// sSprintEndDate
				feature.setsSprintEndDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.EndDate"))));

				// sSprintAssetState
				feature.setsSprintAssetState(tools
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.AssetState")));

				// sSprintChangeDate
				feature.setsSprintChangeDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.ChangeDate"))));

				// sSprintIsDeleted
				feature.setsSprintIsDeleted(tools
						.sanitizeResponse((String) dataMainObj
								.get("Timebox.IsDeleted")));

				// sTeamID
				tmpObj = (JSONObject) dataMainObj.get("Team.ID");
				if (tmpObj.containsKey("_oid")) {
					feature.setsTeamID(tools.sanitizeResponse((String) (tmpObj)
							.get("_oid")));
				}

				// sTeamName
				feature.setsTeamName(tools
						.sanitizeResponse((String) dataMainObj.get("Team.Name")));

				// sTeamChangeDate
				feature.setsTeamChangeDate(tools.toCanonicalDate(tools
						.sanitizeResponse((String) dataMainObj
								.get("Team.ChangeDate"))));

				// sTeamAssetState
				feature.setsTeamAssetState(tools
						.sanitizeResponse((String) dataMainObj
								.get("Team.AssetState")));

				// sTeamIsDeleted
				feature.setsTeamIsDeleted(tools
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
					logger.debug("JSON Object field was empty, so it was filled accordingly");
				} finally {
					feature.setsOwnersID(ownersIdList);
				}

				// sOwnersShortName
				feature.setsOwnersShortName(tools
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.Nickname")));

				// sOwnersFullName
				feature.setsOwnersFullName(tools
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.Name")));

				// sOwnersUsername
				feature.setsOwnersUsername(tools
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.Username")));

				// sOwnersState
				feature.setsOwnersState(tools
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.AssetState")));

				// sOwnersChangeDate
				feature.setsOwnersChangeDate(tools
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.ChangeDate")));

				// sOwnersIsDeleted
				feature.setsOwnersIsDeleted(tools
						.toCanonicalList((List<String>) dataMainObj
								.get("Owners.IsDeleted")));

				try {
					featureRepo.save(feature);
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
				logger.debug("Removed existing entities that will be replaced by newer instances");
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
