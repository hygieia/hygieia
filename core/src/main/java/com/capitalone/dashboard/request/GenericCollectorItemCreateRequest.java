package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

/**
 * A request to create a Generic Collector Item.
 *
 */
public class GenericCollectorItemCreateRequest {
    private String buildId;
    @NotNull
    private String relatedCollectorItemId;
    @NotNull
    private String toolName;
    @NotNull
    private String rawData;
    @NotNull
    private String source;


    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
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

    public String getRelatedCollectorItemId() {
        return relatedCollectorItemId;
    }

    public void setRelatedCollectorItemId(String relatedCollectorItemId) {
        this.relatedCollectorItemId = relatedCollectorItemId;
    }
}
