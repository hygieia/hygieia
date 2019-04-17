package com.capitalone.dashboard.collector;

import static com.capitalone.dashboard.utils.Utilities.getLong;
import static com.capitalone.dashboard.utils.Utilities.getString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Epic;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureEpicResult;
import com.capitalone.dashboard.model.IssueResult;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.util.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.json.simple.parser.JSONParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestOperations;


import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class PivotalTrackerApi {
	//private String apiToken;
	private static JSONParser parser = new JSONParser();
    private final RestOperations restOperations;

    private static final Logger LOGGER = LoggerFactory.getLogger(PivotalTrackerApi.class);
    @Autowired
	public PivotalTrackerApi(Supplier<RestOperations> restOperationsSupplier) {
		// TODO Auto-generated constructor stub
		//this.apiToken=apiToken;
		this.restOperations=restOperationsSupplier.get();
	}
    public Set<Scope> getProjects() {
		// TODO Auto-generated method stub
		
		try {
            String url = "https://www.pivotaltracker.com/services/v5/projects";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-TrackerToken", "2529a7eb5a70fb6f9a1abafbeb822963");
            ResponseEntity<String> responseEntity =restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String responseBody = responseEntity.getBody();

            JSONArray projects = (JSONArray) parser.parse(responseBody);

            return parseAsScopes(projects);

        } catch (ParseException pe) {
            LOGGER.error("Parser exception when parsing teams", pe);
            return Collections.emptySet();
        }
        
    }

	 protected static Set<Scope> parseAsScopes(JSONArray projects) {
	        Set<Scope> result = new HashSet<>();
	        if (CollectionUtils.isEmpty(projects)) {
	        	LOGGER.info("Empty Projects");
	            return Collections.emptySet();
	        }
	        LOGGER.info("Projects Present",result);
	        for (Object obj : projects) {
	            JSONObject jo = (JSONObject) obj;
	            String pId = getString(jo, "id");
	            String pName = getString(jo, "name").trim();
	            if (!StringUtils.isEmpty(pName)) {
	                Scope scope = new Scope();
	                scope.setpId(pId);
	                scope.setName(pName);
	                scope.setProjectPath(pName);
	                scope.setBeginDate("");
	                // endDate - does not exist for jira
	                scope.setEndDate("");
	                // changeDate - does not exist for jira
	                scope.setChangeDate("");
	                // assetState - does not exist for jira
	                // isDeleted - does not exist for jira
	                scope.setIsDeleted("False");
	                result.add(scope);
	            }
	        }
			return result;



}
	public FeatureEpicResult getIssues(Scope project) {
       
        FeatureEpicResult featureEpicResult = new FeatureEpicResult();
        List<Feature> features = new ArrayList<>();

        boolean isLast = false;
        long startAt = 0;

        while (!isLast) {
            try {
                String url = "https://www.pivotaltracker.com/services/v5/projects/"+project.getpId()+"/stories";
             
               IssueResult temp = getFeaturesFromQueryURL(url);

                features.addAll(temp.getFeatures());
                isLast = temp.getTotal() == features.size() || CollectionUtils.isEmpty(temp.getFeatures());
                startAt += temp.getPageSize();
            } catch (ParseException pe) {
                LOGGER.error("Parser exception when parsing issue", pe);
            } catch (HygieiaException e) {
                LOGGER.error("Error in calling JIRA API", e);
            }
        }
        featureEpicResult.setFeatureList(features);
        
        return featureEpicResult;
    }
	 private IssueResult getFeaturesFromQueryURL(String url) throws HygieiaException, ParseException {
	        IssueResult result = new IssueResult();
	        try {
	        	HttpHeaders headers = new HttpHeaders();
	            headers.set("X-TrackerToken", "2529a7eb5a70fb6f9a1abafbeb822963");
	            ResponseEntity<String> responseEntity =restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
	            String responseBody = responseEntity.getBody();
	           
	            JSONArray stories = (JSONArray) parser.parse(responseBody);

	            if (stories != null) {
					LOGGER.info("Response check working fine");
	                long pageSize =stories.size();
	                long total = stories.size();
					LOGGER.info("total"+total);
	                result.setPageSize(pageSize);
	                result.setTotal(total);
	               // JSONArray issueArray = (JSONArray) bodyObject.get("issues");
					
	               

	                stories.forEach(issue -> {
	                    JSONObject issueJson = (JSONObject) issue;

	                    if (!StringUtils.isEmpty(featureSettings.getJiraEpicId()) && featureSettings.getJiraEpicId().equals(type)) {
	                        saveEpic(issueJson, epicMap, true);
	                        return;
	                    }

	                    
	                        result.getFeatures().add(feature);
	                    }
	                });
	            }
				else {
					LOGGER.info("Result check no response");
				}
	        } catch (HttpClientErrorException | HttpServerErrorException he) {
	            LOGGER.error("ERROR collecting issues. " + he.getResponseBodyAsString() + ". Url = " + url);
	        }
			//LOGGER.info("Result check" + result.getFeatures() + ". Url = " + url);
			
	        return result;
	    }
}
