package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditResultService {

    AuditResult getAuditResult(ObjectId id);

    Page<AuditResult> getAuditResultsAll(Pageable pageable);

    Page<AuditResult> getAuditResultsByAuditType(AuditType auditType, Pageable pageable);

    Iterable<AuditResult> getAuditResultsByDBoardTitle(String title);

    Iterable<AuditResult> getAuditResultsByDBoardTitleAndAuditType(String title, AuditType auditType);

    Iterable<AuditResult> getAuditResultsByServAndAppNames(String configItemBusServName, String configItemBusAppName);

    Iterable<AuditResult> getAuditResultsByServAndAppNamesAndAuditType(String configItemBusServName, String configItemBusAppName, AuditType auditType);

    Page<AuditResult> getAuditResultsByLineOfBusAndAuditType(String lineOfBusiness, AuditType auditType, Pageable pageable);

    Page<AuditResult> getAuditResultsByLineOfBus(String lineOfBusiness, Pageable pageable);
}
