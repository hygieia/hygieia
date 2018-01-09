package com.capitalone.dashboard.service;

import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import com.capitalone.dashboard.response.QualityProfileAuditResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CodeQualityAuditServiceImpl implements CodeQualityAuditService {


//    private static final Log LOGGER = LogFactory.getLog(CodeQualityAuditServiceImpl.class);

    @Autowired
    public CodeQualityAuditServiceImpl() {

    }


    @Override
    public List<CodeQualityAuditResponse> getCodeQualityAudit(String projectName, String artifactVersion) {
        return null;
    }

    @Override
    public QualityProfileAuditResponse getQualityGateValidationDetails(String repoUrl, String repoBranch, String projectName, String artifactVersion, long beginDate, long endDate) {
        return null;
    }
}
