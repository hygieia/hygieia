package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.PerfTest;
import com.capitalone.dashboard.status.PerformanceTestAuditStatus;

import java.util.ArrayList;
import java.util.Collection;

public class PerformanceTestAuditResponse extends AuditReviewResponse<PerformanceTestAuditStatus> {


    private Collection<PerfTest> result = new ArrayList<>();

    public Collection<PerfTest> getResult() {
        return result;
    }

    public void setResult(Collection<PerfTest> result) {
        this.result = result;
    }




}
