package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

/**
 * Created by gyf420 on 11/1/17.
 */
public class PerfReviewRequest {

    @NotNull
    private String businessComponentName;

    private long rangeFrom;

    private long rangeTo;


    public String getBusinessComponentName() {
        return businessComponentName;
    }

    public void setBusinessComponentName(String businessComponentName) {
        this.businessComponentName = businessComponentName;
    }

    public long getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(long rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public long getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(long rangeTo) {
        this.rangeTo = rangeTo;
    }


}
