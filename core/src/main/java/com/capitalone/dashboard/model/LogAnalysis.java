package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevegal on 16/06/2018.
 */
@Document(collection="log_analysis")
public class LogAnalysis extends BaseModel {

    private ObjectId collectorItemId;
    private long timestamp;

    private String name;

    private List<LogAnalysisMetric> metrics = new ArrayList<>();

    public List<LogAnalysisMetric> getMetrics() {
        return metrics;
    }

    public void addMetrics(List<LogAnalysisMetric> metrics) {
        this.metrics.addAll(metrics);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
