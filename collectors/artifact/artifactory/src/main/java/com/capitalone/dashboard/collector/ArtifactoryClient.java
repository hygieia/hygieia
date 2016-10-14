package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.ArtifactoryRepo;
import com.capitalone.dashboard.model.BinaryArtifact;

/**
 * Client for fetching artifacts information from Artifactory
 */
public interface ArtifactoryClient {
	
	List<ArtifactoryRepo> getRepos(String instanceUrl, String artifactoryEndpoint);
	
	List<BinaryArtifact> getArtifacts(String instanceUrl, String repoName, long lastUpdated);

}
