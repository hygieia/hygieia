package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

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
@Data
@Document(collection="commits")
public class Commit extends SCM {
    @Id
    private ObjectId id;
    private ObjectId collectorItemId;
    private long timestamp;
}
