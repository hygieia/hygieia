package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.SCM;

public class CommitCreateRequest extends SCM {
    private String hygieiaId;

    private long timestamp;

    public String getHygieiaId() {
        return hygieiaId;
    }

    public void setHygieiaId(String hygieiaId) {
        this.hygieiaId = hygieiaId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
