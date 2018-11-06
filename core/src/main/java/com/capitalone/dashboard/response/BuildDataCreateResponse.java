package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.BuildStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.types.ObjectId;

public class BuildDataCreateResponse {
    private ObjectId id;
    private ObjectId collectorItemId;
    private ObjectId dashboardId;
    private long timestamp;
    private String number;
    private String buildUrl;
    private long startTime;
    private long endTime;
    private long duration;
    private BuildStatus buildStatus;
    private String startedBy;
    private String log;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public BuildStatus getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(BuildStatus buildStatus) {
        this.buildStatus = buildStatus;
    }

    public String getStartedBy() {
        return startedBy;
    }

    public void setStartedBy(String startedBy) {
        this.startedBy = startedBy;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BuildDataCreateResponse that = (BuildDataCreateResponse) o;

        return new EqualsBuilder()
                .append(timestamp, that.timestamp)
                .append(id, that.id)
                .append(collectorItemId, that.collectorItemId)
                .append(dashboardId, that.dashboardId)
                .append(number, that.number)
                .append(buildUrl, that.buildUrl)
                .append(buildStatus, that.buildStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(collectorItemId)
                .append(dashboardId)
                .append(timestamp)
                .append(number)
                .append(buildUrl)
                .append(buildStatus)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("collectorItemId", collectorItemId)
                .append("dashboardId", dashboardId)
                .append("timestamp", timestamp)
                .append("number", number)
                .append("buildUrl", buildUrl)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("duration", duration)
                .append("buildStatus", buildStatus)
                .append("startedBy", startedBy)
                .append("log", log)
                .toString();
    }
}
