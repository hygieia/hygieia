package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.SCM;

/**
 * Created by ltz038 on 4/26/16.
 */

public class GitrepoCreateRequest extends SCM {
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

