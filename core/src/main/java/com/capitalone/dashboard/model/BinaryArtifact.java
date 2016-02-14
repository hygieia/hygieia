package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Comparator;

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

    /**
     * CollectorItemId for the {@link Build} that produced the artifact
     */
    private ObjectId collectorItemId;
    private long timestamp;

    private String artifactName;
    private String canonicalName;
    private String artifactGroupId;
    private String artifactVersion;
    private Build buildInfo;

    public Build getBuildInfo() {
        return buildInfo;
    }

    public void setBuildInfo(Build buildInfo) {
        this.buildInfo = buildInfo;
    }

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

    public static final Comparator<BinaryArtifact> TIMESTAMP_COMPARATOR = new Comparator<BinaryArtifact>() {
        @Override
        public int compare(BinaryArtifact o1, BinaryArtifact o2) {
            return Long.compare(o1.getTimestamp(), o2.getTimestamp());
        }
    };
}
