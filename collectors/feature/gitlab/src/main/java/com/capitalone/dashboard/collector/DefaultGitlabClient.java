package com.capitalone.dashboard.collector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
public class DefaultGitlabClient implements GitlabClient {
	private static final Log LOG = LogFactory.getLog(DefaultGitlabClient.class);
	
	private final RestOperations restOperations;
	
	@Autowired
	public DefaultGitlabClient(RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	@Override
	public JSONArray getTeams() {
		String response = restOperations.getForObject("url to gitlab", String.class);
		JSONArray teams = parseAsArray(response);

		return teams;
	}
	
	private JSONArray parseAsArray(String response) {
		try {
			return (JSONArray) new JSONParser().parse(response);
		} catch (ParseException pe) {
			LOG.error(pe.getMessage());
		}
		return new JSONArray();
	}
	
	private String str(JSONObject json, String key) {
		Object value = json.get(key);
		return value == null ? null : value.toString();
	}

}
