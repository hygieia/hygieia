package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Binary artifacts produced by build jobs and stored in an artifact repository.
 *
 * Possible collectors:
 *  Nexus (in scope)
 *  Artifactory
 *  npm
 *  nuget
 *  rubygems
 *
 */
@Document(collection = "artifacts")
public class BinaryArtifact extends BaseModel {
	
	// list of known metadata properties
	// Note: these may be hard coded in other modules and external tools
	private static final String METADATA_BUILD_URL = "buildUrl";
	private static final String METADATA_BUILD_NUMBER = "buildNumber";
	private static final String METADATA_JOB_URL = "jobUrl";
	private static final String METADATA_JOB_NAME = "jobName";
	private static final String METADATA_INSTANCE_URL = "instanceUrl";
	
    /**
     * CollectorItemId for the {@link Build} that produced the artifact
     */
    private ObjectId collectorItemId;
    private long timestamp;

    private String artifactName;
    private String canonicalName;
    private String artifactGroupId;
    private String artifactVersion;
    
    private Map<String, String> metadata = new HashMap<>();

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getArtifactGroupId() {
        return artifactGroupId;
    }

    public void setArtifactGroupId(String artifactGroupId) {
        this.artifactGroupId = artifactGroupId;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
    
    public String getBuildUrl() {
    	return getMetadata().get(METADATA_BUILD_URL);
    }
    
    public void setBuildUrl(String buildUrl) {
    	getMetadata().put(METADATA_BUILD_URL, buildUrl);
    }
    
    public String getBuildNumber() {
    	return getMetadata().get(METADATA_BUILD_NUMBER);
    }
    
    public void setBuildNumber(String buildNumber) {
    	getMetadata().put(METADATA_BUILD_NUMBER, buildNumber);
    }
    
    public String getJobUrl() {
    	return getMetadata().get(METADATA_JOB_URL);
    }
    
    public void setJobUrl(String jobUrl) {
    	getMetadata().put(METADATA_JOB_URL, jobUrl);
    }
    
    public String getJobName() {
    	return getMetadata().get(METADATA_JOB_NAME);
    }
    
    public void setJobName(String jobName) {
    	getMetadata().put(METADATA_JOB_NAME, jobName);
    }
    
    public String getInstanceUrl() {
    	return getMetadata().get(METADATA_INSTANCE_URL);
    }
    
    public void setInstanceUrl(String instanceUrl) {
    	getMetadata().put(METADATA_INSTANCE_URL, instanceUrl);
    }
    
    public Map<String, String> getMetadata() {
    	return metadata;
    }

    public static final Comparator<BinaryArtifact> TIMESTAMP_COMPARATOR = new Comparator<BinaryArtifact>() {
        @Override
        public int compare(BinaryArtifact o1, BinaryArtifact o2) {
            return Long.compare(o1.getTimestamp(), o2.getTimestamp());
        }
    };
}
