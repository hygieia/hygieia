package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.PerfTest;

import java.util.ArrayList;
import java.util.Collection;

public class PerformaceTestAuditResponse {


    private AuditStatus auditStatuses;

    private Collection<PerfTest> result = new ArrayList<>();

    public AuditStatus getAuditStatuses() {
        return auditStatuses;
    }

    public void setAuditStatuses(AuditStatus auditStatuses) {
        this.auditStatuses = auditStatuses;
    }

    public Collection<PerfTest> getResult() {
        return result;
    }

    public void setResult(Collection<PerfTest> result) {
        this.result = result;
    }




}
