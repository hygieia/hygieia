package com.capitalone.dashboard.collector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.GitlabTeam;

@Component
public class DefaultGitlabClient implements GitlabClient {
	private static final Log LOG = LogFactory.getLog(DefaultGitlabClient.class);
	
	private final RestOperations restOperations;
	
	@Autowired
	public DefaultGitlabClient(RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	@Override
	public GitlabTeam[] getTeams() {
		return restOperations.getForObject("", GitlabTeam[].class);
	}

}
