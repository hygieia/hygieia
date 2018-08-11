package com.capitalone.dashboard.request;

import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

/**
 * Created by stevegal on 22/06/2018.
 */
public class LogAnalysisSearchRequest {

    @NotNull
    private ObjectId componentId;

    public ObjectId getComponentId() {
        return componentId;
    }

    public void setComponentId(ObjectId componentId) {
        this.componentId = componentId;
    }
}
