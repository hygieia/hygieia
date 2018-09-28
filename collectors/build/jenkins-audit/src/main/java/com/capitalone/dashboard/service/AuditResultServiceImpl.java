package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.repository.AuditResultRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AuditResultServiceImpl implements AuditResultService {

    private AuditResultRepository auditResultRepository;

    public AuditResultServiceImpl( AuditResultRepository auditResultRepository){
        this.auditResultRepository = auditResultRepository;
    }

    @Override
    public Iterable<AuditResult> all() {
        return  auditResultRepository.findAll(new Sort(Sort.Direction.DESC, "timestamp"));
    }
}
