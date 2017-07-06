package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.PerformanceType;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

public class PerformanceSearchRequest {
    @NotNull
    private ObjectId componentId;
    private Integer max;
    private Integer numberOfDays;
    private Long dateBegins;
    private Long dateEnds;
    private PerformanceType type;

    public ObjectId getComponentId() {
        return componentId;
    }

    public void setComponentId(ObjectId componentId) {
        this.componentId = componentId;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public Long getDateBegins() {
        return dateBegins;
    }

    public void setDateBegins(Long dateBegins) {
        this.dateBegins = dateBegins;
    }

    public Long getDateEnds() {
        return dateEnds;
    }

    public void setDateEnds(Long dateEnds) {
        this.dateEnds = dateEnds;
    }

    public PerformanceType getType() {
        return type;
    }

    public void setType(PerformanceType type) {
        this.type = type;
    }

    public boolean validDateRange() {
        return dateBegins != null || dateEnds != null;
    }
}
