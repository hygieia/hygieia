package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

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
@Data
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

    public static final Comparator<BinaryArtifact> TIMESTAMP_COMPARATOR = new Comparator<BinaryArtifact>() {
        @Override
        public int compare(BinaryArtifact o1, BinaryArtifact o2) {
            return Long.compare(o1.getTimestamp(), o2.getTimestamp());
        }
    };
}
