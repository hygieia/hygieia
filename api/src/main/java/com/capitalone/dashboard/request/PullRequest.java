package com.capitalone.dashboard.request;

import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PullRequest {
    @NotNull
    private ObjectId componentId;
    private Integer numberOfDays;
    private Long PullDateBegins;
    private Long PullDateEnds;
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

    public Long getPullDateBegins() {
        return PullDateBegins;
    }

    public void setPullDateBegins(Long PullDateBegins) {
        this.PullDateBegins = PullDateBegins;
    }

    public Long getPullDateEnds() {
        return PullDateEnds;
    }

    public void setPullDateEnds(Long PullDateEnds) {
        this.PullDateEnds = PullDateEnds;
    }
    public List<String> getRevisionNumbers() {
        return revisionNumbers;
    }

    public void setRevisionNumbers(List<String> revisionNumbers) {
        this.revisionNumbers = revisionNumbers;
    }

}
