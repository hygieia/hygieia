package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.AuditStatus;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GenericAuditResponse {

    private Set<AuditStatus> auditStatuses = EnumSet.noneOf(AuditStatus.class);

    private Map<String, Object> response = new HashMap<>();

    public static final String PULL_REQUESTS = "pull_request";

    public static final String CODE_REVIEW = "code_review";
    public static final String JOB_REVIEW = "job_review";
    public static final String JOB_CONFIG_REVIEW = "job_config_review";
    public static final String STATIC_CODE_REVIEW = "static_code_review";
    public static final String STATIC_CODE_CONFIG_REVIEW = "static_code_config_review";
    public static final String FUNCTIONAL_TEST_REVIEW = "functional_test_review";

    public Set<AuditStatus> getAuditStatuses() {
        return auditStatuses;
    }

    public void addAuditStatus(AuditStatus status) {
        auditStatuses.add(status);
    }

    public void setAuditStatuses(Set<AuditStatus> auditStatuses) {
        this.auditStatuses = auditStatuses;
    }

    public Object getResponse(String name) {
        return response.get(name);
    }

    public void addResponse(String name, Object object) {
        response.put(name, object);
    }
}