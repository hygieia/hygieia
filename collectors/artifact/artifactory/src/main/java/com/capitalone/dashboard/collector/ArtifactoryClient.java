package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.ArtifactoryRepo;
import com.capitalone.dashboard.model.BinaryArtifact;

/**
 * Client for fetching artifacts information from Artifactory
 */
public interface ArtifactoryClient {
	
	/**
	 * Obtain list of repos in the given artifactory
	 * 	
	 * @param instanceUrl					server url
	 * @param artifactoryEndpoint			endpoint of the artifactory in the instance url
	 * @return
	 */
	List<ArtifactoryRepo> getRepos(String instanceUrl);
	
	/**
	 * Obtain all the artifacts in the given artifactory repo
	 * 
	 * @param instanceUrl		server url
	 * @param repoName			repo name
	 * @param lastUpdated		timestamp when the repo was last updated
	 * @return
	 */
	List<BinaryArtifact> getArtifacts(String instanceUrl, String repoName, long lastUpdated);

}
