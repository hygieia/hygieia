package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;
import org.springframework.stereotype.Component;

@Component
public interface AuditResultService {

    /**
     * Get all audit results
     */
    Iterable<AuditResult> all();
}
