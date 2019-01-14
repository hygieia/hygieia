package com.capitalone.dashboard.model;

import java.util.Objects;

public class Epic {
    private String id;
    private String number;
    private String name;
    private String beginDate;
    private String changeDate;
    private String endDate;
    private String status;
    private String url;
    private boolean recentUpdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRecentUpdate() {
        return recentUpdate;
    }

    public void setRecentUpdate(boolean recentUpdate) {
        this.recentUpdate = recentUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(id, epic.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
