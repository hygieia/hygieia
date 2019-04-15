package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CollectorItem;

public class PivotalTrackerCollectorItem extends CollectorItem {
	private long projectId;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
