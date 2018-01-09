package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
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
@Document(collection="commits")
@CompoundIndexes({
        @CompoundIndex(name = "unique_scm_key", def = "{'collectorItemId' : 1, 'scmRevisionNumber': 1}")
})
public class Commit extends SCM {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId collectorItemId;
    private long timestamp;

    private boolean firstEverCommit;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public boolean isFirstEverCommit() {
        return firstEverCommit;
    }

    public void setFirstEverCommit(boolean firstEverCommit) {
        this.firstEverCommit = firstEverCommit;
    }
}
