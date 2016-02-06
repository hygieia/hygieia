package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.TestSuiteType;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class TestResultRequest {
    @NotNull
    private ObjectId componentId;
    private Integer max;
    private Long startDateBegins;
    private Long startDateEnds;
    private Long endDateBegins;
    private Long endDateEnds;
    private Long durationGreaterThan;
    private Long durationLessThan;
    @Range(min = 0, max = 4)
    private Integer depth;
    private List<TestSuiteType> types = new ArrayList<>();

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

    public Long getStartDateBegins() {
        return startDateBegins;
    }

    public void setStartDateBegins(Long startDateBegins) {
        this.startDateBegins = startDateBegins;
    }

    public Long getStartDateEnds() {
        return startDateEnds;
    }

    public void setStartDateEnds(Long startDateEnds) {
        this.startDateEnds = startDateEnds;
    }

    public Long getEndDateBegins() {
        return endDateBegins;
    }

    public void setEndDateBegins(Long endDateBegins) {
        this.endDateBegins = endDateBegins;
    }

    public Long getEndDateEnds() {
        return endDateEnds;
    }

    public void setEndDateEnds(Long endDateEnds) {
        this.endDateEnds = endDateEnds;
    }

    public Long getDurationGreaterThan() {
        return durationGreaterThan;
    }

    public void setDurationGreaterThan(Long durationGreaterThan) {
        this.durationGreaterThan = durationGreaterThan;
    }

    public Long getDurationLessThan() {
        return durationLessThan;
    }

    public void setDurationLessThan(Long durationLessThan) {
        this.durationLessThan = durationLessThan;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public List<TestSuiteType> getTypes() {
        return types;
    }

    public void setTypes(List<TestSuiteType> types) {
        this.types = types;
    }

    public boolean validStartDateRange() {
        return startDateBegins != null || startDateEnds != null;
    }

    public boolean validEndDateRange() {
        return endDateBegins != null || endDateEnds != null;
    }

    public boolean validDurationRange() {
        return durationGreaterThan != null || durationLessThan != null;
    }
}
