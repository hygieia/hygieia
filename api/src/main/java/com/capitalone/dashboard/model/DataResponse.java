package com.capitalone.dashboard.model;

public class DataResponse<T> {
    private final T result;
    private final long lastUpdated;

    public DataResponse(T result, long lastUpdated) {
        this.result = result;
        this.lastUpdated = lastUpdated;
    }

    public T getResult() {
        return result;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
