package com.capitalone.dashboard.request;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class PipelineSearchRequest {
    @NotEmpty
    private List<ObjectId> collectorItemId;
    private Long beginDate;
    private Long endDate;

    public List<ObjectId> getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(List<ObjectId> collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public Long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Long beginDate) {
        this.beginDate = beginDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public boolean hasDateRange() {
        return beginDate != null && endDate != null;
    }
}
