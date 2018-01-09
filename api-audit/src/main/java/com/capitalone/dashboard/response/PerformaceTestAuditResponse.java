package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.PerfTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class PerformaceTestAuditResponse {


    private AuditStatus auditStatuses;

    Collection<PerfTest> result = new ArrayList<>();

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
