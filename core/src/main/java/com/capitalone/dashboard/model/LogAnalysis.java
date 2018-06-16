package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by stevegal on 16/06/2018.
 */
public class LogAnalysis extends BaseModel {

    private ObjectId collectorItemId;
    private long timestamp;

    private String name;
    private String url;
    private String version;
    private ObjectId buildId;
    private List<LogAnalysisMetric> metrics;
}
