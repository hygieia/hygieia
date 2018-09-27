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
    //To build relations if needed
    private ObjectId relatedCollectorItem;
    @NotNull
    private String toolName;
    @NotNull
    private String rawData;

    @NotNull
    private long creationTime;

    @NotNull
    private String source;

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
}
