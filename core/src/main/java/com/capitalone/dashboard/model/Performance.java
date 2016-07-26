package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection="performance")
public class Performance extends BaseModel{

    private ObjectId collectorItemId;
    private long timestamp;
    private String url; // url of the tool's server.
    private PerformanceType type;
    private String version; // application version, environment version
    private  ObjectId executionId; // optional: in case we have to tie it to a Jenkins build, for example
    private Map<String, PerformanceMetric> metrics = new HashMap<>();


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PerformanceType getType() {
        return type;
    }

    public void setType(PerformanceType type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ObjectId getExecutionId() {
        return executionId;
    }

    public void setExecutionId(ObjectId executionId) {
        this.executionId = executionId;
    }

    public Map<String, PerformanceMetric> getMetrics() {
        return metrics;
    }

}
