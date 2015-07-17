package com.capitalone.dashboard.request;

import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommitRequest {
    @NotNull
    private ObjectId componentId;
    private Integer numberOfDays;
    private Long commitDateBegins;
    private Long commitDateEnds;
    private Long changesGreaterThan;
    private Long changesLessThan;
    private List<String> revisionNumbers = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private String messageContains;

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

    public Long getCommitDateBegins() {
        return commitDateBegins;
    }

    public void setCommitDateBegins(Long commitDateBegins) {
        this.commitDateBegins = commitDateBegins;
    }

    public Long getCommitDateEnds() {
        return commitDateEnds;
    }

    public void setCommitDateEnds(Long commitDateEnds) {
        this.commitDateEnds = commitDateEnds;
    }

    public Long getChangesGreaterThan() {
        return changesGreaterThan;
    }

    public void setChangesGreaterThan(Long changesGreaterThan) {
        this.changesGreaterThan = changesGreaterThan;
    }

    public Long getChangesLessThan() {
        return changesLessThan;
    }

    public void setChangesLessThan(Long changesLessThan) {
        this.changesLessThan = changesLessThan;
    }

    public List<String> getRevisionNumbers() {
        return revisionNumbers;
    }

    public void setRevisionNumbers(List<String> revisionNumbers) {
        this.revisionNumbers = revisionNumbers;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getMessageContains() {
        return messageContains;
    }

    public void setMessageContains(String messageContains) {
        this.messageContains = messageContains;
    }

    public boolean validCommitDateRange() {
        return commitDateBegins != null || commitDateEnds != null;
    }

    public boolean validChangesRange() {
        return changesGreaterThan != null || changesLessThan != null;
    }
}
