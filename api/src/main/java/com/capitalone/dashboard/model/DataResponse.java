package com.capitalone.dashboard.model;

public class DataResponse<T> {
    private final T result;
    private final long lastUpdated;
    private String reportUrl;

    public DataResponse(T result, long lastUpdated) {
        this.result = result;
        this.lastUpdated = lastUpdated;
    }

    public DataResponse(T result, long lastUpdated,String reportUrl ) {
        this.result = result;
        this.lastUpdated = lastUpdated;
        this.reportUrl = reportUrl;
    }

    public T getResult() {
        return result;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public String getReportUrl() {
        return reportUrl;
    }

}
