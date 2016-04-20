package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of a Continuous Integration build execution. Typically produces binary artifacts.
 * Often triggered by one or more SCM commits.
 *
 * Possible collectors:
 *  Hudson (in scope)
 *  Team City
 *  TFS
 *  Go
 *  Bamboo
 *  TravisCI
 *
 */
@Data
@Document(collection="builds")
public class Build extends BaseModel {
    private ObjectId collectorItemId;
    private long timestamp;

    private String number;
    private String buildUrl;
    private long startTime;
    private long endTime;
    private long duration;
    private BuildStatus buildStatus;
    private String startedBy;
    private String log;
    private List<SCM> sourceChangeSet = new ArrayList<>();

    public void addSourceChangeSet(SCM scm) {
        getSourceChangeSet().add(scm);
    }
}
