package com.capitalone.dashboard.collector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.client.RestClientException;

import com.capitalone.dashboard.model.FortifyProject;
import com.capitalone.dashboard.model.FortifyScanReport;

public interface FortifyClient {

	List<FortifyProject> getApplications(String instanceUrl, Collection<JSONObject> collection) throws ParseException;

	FortifyScanReport getFortifyReport(FortifyProject application, JSONObject latestVersionObject) throws ParseException, java.text.ParseException;
	
	Map<String, JSONObject> getApplicationArray(String instanceUrl) throws ParseException, RestClientException;
}
