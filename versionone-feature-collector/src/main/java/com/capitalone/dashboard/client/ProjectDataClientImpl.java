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

package com.capitalone.dashboard.client;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.Constants;
import com.capitalone.dashboard.util.DateUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.FeatureWidgetQueries;
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
public class ProjectDataClientImpl  {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDataClientImpl.class);

    private final FeatureSettings featureSettings;
    private final FeatureWidgetQueries featureWidgetQueries;
    private final ScopeRepository projectRepo;
    private final FeatureCollectorRepository featureCollectorRepository;
    private final VersionOneDataFactoryImpl vOneApi;

    /**
     * Extends the constructor from the super class.
     */
    public ProjectDataClientImpl(FeatureSettings featureSettings,
                                 ScopeRepository projectRepository,
                                 FeatureCollectorRepository featureCollectorRepository,
                                 VersionOneDataFactoryImpl vOneApi) {
        LOGGER.debug("Constructing data collection for the feature widget, story-level data...");

        this.featureSettings = featureSettings;
        this.projectRepo = projectRepository;
        this.featureCollectorRepository = featureCollectorRepository;
        this.featureWidgetQueries = new FeatureWidgetQueries(
                this.featureSettings);
        this.vOneApi = vOneApi;
    }

    /**
     * Updates the MongoDB with a JSONArray received from the source system
     * back-end with story-based data.
     *
     * @param tmpMongoDetailArray A JSON response in JSONArray format from the source system
     *
     */
    @SuppressWarnings("unchecked")
    protected void updateMongoInfo(JSONArray tmpMongoDetailArray) {
        for (Object obj : tmpMongoDetailArray) {
            JSONObject dataMainObj = (JSONObject) obj;

            Scope scope = new Scope();

            removeExistingEntity(getJSONDateString(dataMainObj, "_oid"));

            // collectorId
            scope.setCollectorId(featureCollectorRepository.findByName(
                    Constants.VERSIONONE).getId());

            // ID;
            scope.setpId(getJSONDateString(dataMainObj, "_oid"));

            // name;
            scope.setName(getJSONDateString(dataMainObj, "Name"));

            // beginDate;
            scope.setBeginDate(getJSONDateString(dataMainObj, "BeginDate"));

            // endDate;
            scope.setEndDate(getJSONDateString(dataMainObj, "EndDate"));

            // changeDate;
            scope.setChangeDate(getJSONDateString(dataMainObj, "ChangeDate"));

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
            scope.setProjectPath(ClientUtil.sanitizeResponse(projPath));

            projectRepo.save(scope);
        }
    }

    private String getJSONString(JSONObject obj, String field) {
        return ClientUtil.sanitizeResponse((String) obj.get(field));
    }

    private String getJSONDateString(JSONObject obj, String field) {
        return ClientUtil.toCanonicalDate(getJSONString(obj, field));
    }




    public String getMaxChangeDate() {
        Collector col = featureCollectorRepository.findByName(Constants.VERSIONONE);
        if (col == null) return "";
        if (StringUtils.isEmpty(featureSettings.getDeltaStartDate())) return "";

            List<Scope> response = projectRepo.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(col.getId(), featureSettings
                    .getDeltaStartDate());
        if (!CollectionUtils.isEmpty(response)) return response.get(0).getChangeDate();
        return "";
    }


    public void updateProjectInformation() throws HygieiaException {
        String returnDate = this.featureSettings.getDeltaStartDate();
        if (getMaxChangeDate() != null) {
            returnDate = getMaxChangeDate();
        }
        returnDate = DateUtil.getChangeDateMinutePrior(returnDate, this.featureSettings.getScheduledPriorMin()); //getChangeDateMinutePrior(returnDate);
        String queryName = this.featureSettings.getProjectQuery();
        updateObjectInformation(featureWidgetQueries.getQuery(returnDate, queryName));
    }

    public void updateObjectInformation(String query) throws HygieiaException {
        long start = System.nanoTime();
        int pageIndex = 0;
        int pageSize = this.featureSettings.getPageSize();
        vOneApi.setPageSize(pageSize);

        vOneApi.buildBasicQuery(query);
        vOneApi.buildPagingQuery(0);
        JSONArray outPutMainArray = vOneApi.getPagingQueryResponse();
        if (outPutMainArray == null) {
            //Fixme: defined new exception code in @HygieiaException
            throw new HygieiaException("FAILED: Script Completed with Error", 0);
        }
        JSONArray tmpDetailArray = (JSONArray) outPutMainArray.get(0);
        while (!CollectionUtils.isEmpty(tmpDetailArray)) {
            updateMongoInfo(tmpDetailArray);
            pageIndex = pageIndex + pageSize;
            vOneApi.buildPagingQuery(pageIndex);
            outPutMainArray = vOneApi.getPagingQueryResponse();
            if (outPutMainArray == null) {
                LOGGER.info("FAILED: Script Completed with Error");
                throw new HygieiaException("FAILED: Script Completed with Error", 0);
            }
            tmpDetailArray = (JSONArray) outPutMainArray.get(0);
        }
        double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
        LOGGER.info("Process took :" + elapsedTime + " seconds to update");
    }

    /**
     * Validates current entry and removes new entry if an older item exists in
     * the repo
     *
     * @param localId local repository item ID (not the precise mongoID)
     */
    protected void removeExistingEntity(String localId) {
        if (StringUtils.isEmpty(localId)) return;
        List<Scope> scopes = projectRepo.getScopeIdById(localId);

        if (CollectionUtils.isEmpty(scopes)) return;

        ObjectId tempEntId = scopes.get(0).getId();
        if (localId.equalsIgnoreCase(scopes.get(0).getpId())) {
            projectRepo.delete(tempEntId);
        }
    }

}
