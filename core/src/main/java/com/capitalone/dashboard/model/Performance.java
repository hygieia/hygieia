package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection="performance")
public class Performance extends BaseModel{

    private ObjectId collectorItemId;
    private long timestamp;
    private String url;
    private PerformanceType type;
    private String version;
    private  ObjectId executionId;
    private String targetAppName;
    private String targetEnvName;
    private Map<String,Object> metrics = new HashMap<>();

    public Map<String,Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String,Object> metrics) {
        this.metrics = metrics;
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

    public String getTargetAppName() {
        return targetAppName;
    }

    public void setTargetAppName(String targetAppName) {
        this.targetAppName = targetAppName;
    }

    public String getTargetEnvName() {
        return targetEnvName;
    }

    public void setTargetEnvName(String targetEnvName) {
        this.targetEnvName = targetEnvName;
    }
}
