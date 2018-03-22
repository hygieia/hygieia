package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;

public class BuildSearch {
  @NotNull
  private ObjectId componentId;
  private Integer numberOfDays;
  private Long startDateBegins;
  private Long startDateEnds;
  private Long endDateBegins;
  private Long endDateEnds;
  private Long durationGreaterThan;
  private Long durationLessThan;
  private Integer max;
  private List<BuildStatus> buildStatuses = new ArrayList<>();

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

  public List<BuildStatus> getBuildStatuses() {
    return buildStatuses;
  }

  public void setBuildStatuses(List<BuildStatus> buildStatuses) {
    this.buildStatuses = buildStatuses;
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

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }
}
