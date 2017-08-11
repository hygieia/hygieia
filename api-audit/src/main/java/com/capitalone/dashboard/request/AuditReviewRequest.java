package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class AuditReviewRequest {

    @NotNull
    private long beginDate;
    @NotNull
    private long endDate;

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

}
