package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.repository.AuditResultRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public Iterable<AuditResult> getAuditResultsByTitle(String dashboardTitle){
        return auditResultRepository.findByDashboardTitle(dashboardTitle);
    }

    @Override
    public Iterable<AuditResult> getAuditResults() {
        return auditResultRepository.findAll(new Sort(Sort.Direction.ASC, "dashboardTitle"));
    }
}
