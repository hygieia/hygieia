package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A specific commit in a version control repository.
 *
 * Possible collectors:
 *  Subversion (in scope)
 *  Git (in scope)
 *  GitHub
 *  TFS
 *  BitBucket
 *  Unfuddle
 *
 */
@Document(collection="gitrepos")
public class GitRepoData extends SCM {
    @Id
    private ObjectId id;
    private String repoName;
    private ObjectId collectorItemId;
    private long timestamp;

    public ObjectId getId() {
        return id;
    }

    void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return repoName;
    }

    public void setName(String name) {
        this.repoName = name;
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
}
