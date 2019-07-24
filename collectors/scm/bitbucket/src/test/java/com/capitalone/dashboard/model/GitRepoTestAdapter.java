package com.capitalone.dashboard.model;

class GitRepoTestAdapter extends GitRepo {
    void setLastUpdateTimeBypass(Object value) {
        super.getOptions().put(LAST_UPDATE_TIME, value);
    }
}
