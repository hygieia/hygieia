package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.repository.AuditStatusRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AuditStatusServiceImpl implements AuditStatusService {

    private AuditStatusRepository auditStatusRepository;

    public AuditStatusServiceImpl(){}

    public AuditStatusServiceImpl( AuditStatusRepository auditStatusRepository){
        this.auditStatusRepository = auditStatusRepository;
    }

    @Override
    public Iterable<AuditResult> all() {
        return  auditStatusRepository.findAll(new Sort(Sort.Direction.DESC, "timestamp"));
    }
}
