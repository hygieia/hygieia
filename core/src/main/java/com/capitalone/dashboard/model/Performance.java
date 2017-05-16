package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection="performance")
public class Performance extends BaseModel{

    private ObjectId collectorItemId;
    private long timestamp;
    private String url; // url of the tool's server.
    private PerformanceType type;
    private String version; // application version, environment version
    private  ObjectId executionId; // optional: in case we have to tie it to a Jenkins build, for example

    // private Map<String, PerformanceMetric> metrics = new HashMap<>();
    private Map<String, Set<PerformanceMetric>> instances = new HashMap<>();
    //private Set<PerformanceMetric> metrics = new HashSet<>();



    public Map<String, Set<PerformanceMetric>> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, Set<PerformanceMetric>> instances) {
        this.instances = instances;
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

    public Set<PerformanceMetric> getMetrics(String instanceURL ) {
        return instances.get(instanceURL);
    }

}
