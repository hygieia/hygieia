package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

/**
 * A request to create a Generic Collector Item.
 *
 */
public class GenericCollectorItemCreateRequest {
    @NotNull
    private String hygieiaCollectionId; //A collector item id in hygieia for linking things
    @NotNull
    private String hygieiaCollectorItemId;
    @NotNull
    private String toolName;
    @NotNull
    private String rawData;
    @NotNull
    private String source;


    public String getHygieiaCollectionId() {
        return hygieiaCollectionId;
    }

    public void setHygieiaCollectionId(String hygieiaCollectionId) {
        this.hygieiaCollectionId = hygieiaCollectionId;
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

    public String getHygieiaCollectorItemId() {
        return hygieiaCollectorItemId;
    }

    public void setHygieiaCollectorItemId(String hygieiaCollectorItemId) {
        this.hygieiaCollectorItemId = hygieiaCollectorItemId;
    }
}
