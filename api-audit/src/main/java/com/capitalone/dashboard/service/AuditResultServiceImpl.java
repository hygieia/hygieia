package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.repository.AuditResultRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class AuditResultServiceImpl implements AuditResultService{

    @Autowired
    private AuditResultRepository auditResultRepository;

    @Autowired
    public AuditResultServiceImpl(AuditResultRepository auditResultRepository){
        this.auditResultRepository = auditResultRepository;
    }

    @Override
    public AuditResult getAuditResult(ObjectId id){
        return auditResultRepository.findById(id);
    }

    @Override
    public Page<AuditResult> getAuditResultsAll(Pageable pageable) {
        return auditResultRepository.findAll(pageable);
    }

    @Override
    public Page<AuditResult> getAuditResultsByAuditType(AuditType auditType, Pageable pageable) {
        return auditResultRepository.findByAuditType(auditType, pageable);
    }

    @Override
    public Iterable<AuditResult> getAuditResultsByDBoardTitle(String dashboardTitle){
        return auditResultRepository.findByDashboardTitle(dashboardTitle);
    }

    @Override
    public Iterable<AuditResult> getAuditResultsByDBoardTitleAndAuditType(String title, AuditType auditType) {
        return auditResultRepository.findByDashboardTitleAndAuditType(title, auditType);
    }

    @Override
    public Iterable<AuditResult> getAuditResultsByServAndAppNames(String configItemBusServName, String configItemBusAppName) {
        return auditResultRepository.findByConfigItemBusServNameAndConfigItemBusAppName(configItemBusServName, configItemBusAppName);
    }

    @Override
    public Iterable<AuditResult> getAuditResultsByServAndAppNamesAndAuditType(String configItemBusServName, String configItemBusAppName, AuditType auditType) {
        return auditResultRepository.findByConfigItemBusServNameAndConfigItemBusAppNameAndAuditType(configItemBusServName, configItemBusAppName, auditType);
    }

    @Override
    public Page<AuditResult> getAuditResultsByLineOfBusAndAuditType(String lineOfBus, AuditType auditType, Pageable pageable) {
        return auditResultRepository.findByLineOfBusinessAndAuditType(lineOfBus, auditType, pageable);
    }

    @Override
    public Page<AuditResult> getAuditResultsByLineOfBus(String lineOfBus, Pageable pageable) {
        return auditResultRepository.findByLineOfBusiness(lineOfBus, pageable);
    }
}
