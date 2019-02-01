package com.capitalone.dashboard.collector;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.FortifyProject;
import com.capitalone.dashboard.model.FortifyScanReport;
import com.capitalone.dashboard.util.Supplier;

@Component
public class DefaultFortifyClient implements FortifyClient {

	private static final String ID = "id";
	private static final String NAME = "name";

	private final RestOperations rest;
	private Set<Map<String, String>> fortifyServers = new HashSet<>();

	@Autowired
	public DefaultFortifyClient(Supplier<RestOperations> restOperationsSupplier) {
		this.rest = restOperationsSupplier.get();

	}

	@Override
	public Map<String, JSONObject> getApplicationArray(String instanceUrl) throws ParseException {
		Map<String, JSONObject> applicationsWithVersion = new HashMap<>();
		boolean isLastPage = false;
		JSONObject linksObj = null;
		JSONObject projObj = null;
		String url = instanceUrl + "api/v1/projectVersions";
		while (!isLastPage) {
			JSONObject object = parseAsObject(url);
			if (object != null) {
				for(Object project : (JSONArray)object.get("data")) {
					projObj = (JSONObject) project;
					applicationsWithVersion.put(str(projObj, ID), projObj);
				}
				linksObj = (JSONObject) object.get("links");
				if (linksObj.get("next") == null) {
					isLastPage = true;
				} else {
					JSONObject nextPageObj = (JSONObject) linksObj.get("next");
					url = nextPageObj.get("href").toString();
				}
			} else {
				isLastPage = true;
			}
		}
		return applicationsWithVersion;
	}

	@Override
	public List<FortifyProject> getApplications(String instanceUrl, Collection<JSONObject> versionDataArray) throws ParseException {
		List<FortifyProject> applications = new ArrayList<>();
		for (Object obj : versionDataArray) {
			JSONObject versionData = (JSONObject) obj;
			JSONObject projectData = (JSONObject) versionData.get("project");
			FortifyProject application = new FortifyProject();
			application.setInstanceUrl(instanceUrl);
			application.setProjectId(str(projectData, ID));
			application.setProjectName(str(projectData, NAME));
			application.setVersionId(str(versionData, ID));
			application.setProjectVersion(str(versionData, NAME));
			applications.add(application);
		}
		return applications;
	}

	@Override
	public FortifyScanReport getFortifyReport(FortifyProject application, JSONObject latestVersionObject)
			throws ParseException, java.text.ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+'");
		JSONObject issueObj = null;
		JSONObject linksObj = null;
		FortifyScanReport fortifyReport = new FortifyScanReport();
		boolean isLastPage = false;

		JSONObject currentStateObj = (JSONObject) latestVersionObject.get("currentState");
		Date date = format.parse(str(currentStateObj, "metricEvaluationDate"));
		fortifyReport.setTimestamp(date.getTime());
		fortifyReport.setVersion(str(latestVersionObject, NAME));
		fortifyReport.setCollectorItemId(application.getId());
		String url = application.getInstanceUrl() + "api/v1/projectVersions/" + str(latestVersionObject, ID)
				+ "/issues?limit=200";
		while (!isLastPage) {
			JSONObject object = parseAsObject(url);
			if (object != null) {
				JSONArray issuesArray = (JSONArray) object.get("data");
				for (Object issue : issuesArray) {
					issueObj = (JSONObject) issue;
					fortifyReport.addThreats(str(issueObj, "friority"), str(issueObj, "primaryLocation"));
				}
				linksObj = (JSONObject) object.get("links");
				if (linksObj.get("next") == null) {
					isLastPage = true;
				} else {
					JSONObject nextPageObj = (JSONObject) linksObj.get("next");
					url = nextPageObj.get("href").toString();
				}
			} else {
				isLastPage = true;
			}
		}
		return fortifyReport;
	}

	private JSONObject parseAsObject(String url) throws ParseException {
		ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET,
				new HttpEntity<String>(this.createHeaders(getUserName(url), getPassword(url))), String.class);
		if (response == null || response.getBody() == null) {
			throw new RestClientException("No response for URL "+url);
		} else {
			return (JSONObject) new JSONParser().parse(response.getBody());
		}
	}

	private HttpHeaders createHeaders(String userName, String password) {
		HttpHeaders headers = new HttpHeaders();
		if (userName != null && !userName.isEmpty() && password != null && !password.isEmpty()) {
			String auth = userName + ":" + password;
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + new String(encodedAuth);
			headers.set("Authorization", authHeader);
		}
		return headers;
	}

	private String getPassword(String url) {
		for (Map<String, String> sonaTypeServer : fortifyServers) {
			if (url.contains(sonaTypeServer.get("url"))) {
				return sonaTypeServer.get("password");
			}
		}
		return null;
	}

	private String getUserName(String url) {
		for (Map<String, String> sonaTypeServer : fortifyServers) {
			if (url.contains(sonaTypeServer.get("url"))) {
				return sonaTypeServer.get("userName");
			}
		}
		return null;
	}

	private String str(JSONObject json, String key) {
		Object obj = json.get(key);
		return obj == null ? null : obj.toString();
	}

}
