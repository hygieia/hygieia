package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.CollectorItem;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class PipelineSearchRequest {
    /** {@link CollectorItem} teamdashboard collector item id */
    @NotEmpty
    private List<ObjectId> collectorItemId;

    /** Dates to filter prod bucket's commits by **/
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
