
package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
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
 */
public class ProjectDataClient extends BaseClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDataClient.class);

	private final FeatureSettings featureSettings;
	private final ScopeRepository projectRepo;
	private final FeatureCollectorRepository featureCollectorRepository;

	/**
	 * Extends the constructor from the super class.
	 */
	public ProjectDataClient(FeatureSettings featureSettings, ScopeRepository projectRepository,
			FeatureCollectorRepository featureCollectorRepository,
			VersionOneDataFactoryImpl vOneApi) {
        super(vOneApi, featureSettings);
        LOGGER.debug("Constructing data collection for the feature widget, story-level data...");

		this.featureSettings = featureSettings;
		this.projectRepo = projectRepository;
		this.featureCollectorRepository = featureCollectorRepository;
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 *
	 * @param tmpMongoDetailArray
	 *            A JSON response in JSONArray format from the source system
	 */
    @Override
	@SuppressWarnings("unchecked")
	protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
		for (Object obj : tmpMongoDetailArray) {
			JSONObject dataMainObj = (JSONObject) obj;

			Scope scope = new Scope();

			removeExistingEntity(getJSONString(dataMainObj, "_oid"));

			// collectorId
			scope.setCollectorId(
					featureCollectorRepository.findByName(FeatureCollectorConstants.VERSIONONE).getId());

			// ID;
			scope.setpId(getJSONString(dataMainObj, "_oid"));

			// name;
			scope.setName(getJSONString(dataMainObj, "Name"));

			// beginDate;
			scope.setBeginDate(getJSONString(dataMainObj, "BeginDate"));

			// endDate;
			scope.setEndDate(getJSONString(dataMainObj, "EndDate"));

			// changeDate;
			scope.setChangeDate(getJSONString(dataMainObj, "ChangeDate"));

			// assetState;
			scope.setAssetState(getJSONString(dataMainObj, "AssetState"));

			// isDeleted;
			scope.setIsDeleted(getJSONString(dataMainObj, "IsDeleted"));

			// path;
			String projPath = scope.getName();
			List<String> projList = (List<String>) dataMainObj.get("ParentAndUp.Name");
			if (!CollectionUtils.isEmpty(projList)) {
				for (String proj : projList) {
					projPath = proj + "-->" + projPath;
				}
				projPath = "All-->" + projPath;
			} else {
				projPath = "All-->" + projPath;
			}
			scope.setProjectPath(sanitizeResponse(projPath));

			projectRepo.save(scope);
		}
	}

    @Override
	public String getMaxChangeDate() {
		Collector col = featureCollectorRepository.findByName(FeatureCollectorConstants.VERSIONONE);
		if (col == null)
			return "";
		if (StringUtils.isEmpty(featureSettings.getDeltaStartDate()))
			return "";

		List<Scope> response = projectRepo
				.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(col.getId(),
                        featureSettings.getDeltaStartDate());
		if (!CollectionUtils.isEmpty(response))
			return response.get(0).getChangeDate();
		return "";
	}

	public void updateProjectInformation() throws HygieiaException {
		String returnDate = this.featureSettings.getDeltaStartDate();
		if (!StringUtils.isEmpty(getMaxChangeDate())) {
			returnDate = getMaxChangeDate();
		}
		returnDate = DateUtil.getChangeDateMinutePrior(returnDate,
				this.featureSettings.getScheduledPriorMin()); // getChangeDateMinutePrior(returnDate);
		String queryName = this.featureSettings.getProjectQuery();
		updateObjectInformation(getQuery(returnDate, queryName));
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
		List<Scope> scopes = projectRepo.getScopeIdById(localId);

		if (CollectionUtils.isEmpty(scopes))
			return;

		ObjectId tempEntId = scopes.get(0).getId();
		if (localId.equalsIgnoreCase(scopes.get(0).getpId())) {
			projectRepo.delete(tempEntId);
		}
	}

}
