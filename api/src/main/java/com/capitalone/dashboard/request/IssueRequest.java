package com.capitalone.dashboard.request;

import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class IssueRequest {
    @NotNull
    private ObjectId componentId;
    private Integer numberOfDays;
    private Long IssueDateBegins;
    private Long IssueDateEnds;
    private List<String> revisionNumbers = new ArrayList<>();

    public ObjectId getComponentId() {
        return componentId;
    }

    public void setComponentId(ObjectId componentId) {
        this.componentId = componentId;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public Long getIssueDateBegins() {
        return IssueDateBegins;
    }

    public void setIssueDateBegins(Long IssueDateBegins) {
        this.IssueDateBegins = IssueDateBegins;
    }

    public Long getIssueDateEnds() {
        return IssueDateEnds;
    }

    public void setIssueDateEnds(Long IssueDateEnds) {
        this.IssueDateEnds = IssueDateEnds;
    }
    public List<String> getRevisionNumbers() {
        return revisionNumbers;
    }

    public void setRevisionNumbers(List<String> revisionNumbers) {
        this.revisionNumbers = revisionNumbers;
    }

}
