package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * A generic structure to hold a generic collector item info for a given tool that only that tool collector can
 * decipher. This will be used in creating collector item via API for non-standard tools.
 */
@Document(collection = "generic_collector_items")
public class GenericCollectorItem extends BaseModel {
    @NotNull
    private String toolName;
    @NotNull
    private ObjectId collectorId;
    @NotNull
    private String rawData;
    @NotNull
    private long creationTime;
    private long processTime;
    @NotNull
    private String source;
    private ObjectId relatedCollectorItem; //To build relations if needed
    private ObjectId buildId; //if started  off by some build - connects to build collection



    public ObjectId getBuildId() {
        return buildId;
    }

    public void setBuildId(ObjectId buildId) {
        this.buildId = buildId;
    }

    public ObjectId getRelatedCollectorItem() {
        return relatedCollectorItem;
    }

    public void setRelatedCollectorItem(ObjectId relatedCollectorItem) {
        this.relatedCollectorItem = relatedCollectorItem;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ObjectId getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(ObjectId collectorId) {
        this.collectorId = collectorId;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }
}
