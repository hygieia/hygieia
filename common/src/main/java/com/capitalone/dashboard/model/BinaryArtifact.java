package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private ObjectId collectorItemId;
    private long timestamp;

    private String artifactName;
    private String groupId;
    private String version;

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
