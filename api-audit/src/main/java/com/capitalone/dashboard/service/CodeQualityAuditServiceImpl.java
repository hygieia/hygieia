package com.capitalone.dashboard.service;

import com.capitalone.dashboard.response.CodeQualityProfileValidationResponse;
import com.capitalone.dashboard.response.StaticAnalysisResponse;
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
    public List<StaticAnalysisResponse> getCodeQualityAudit(String projectName, String artifactVersion) {
        return null;
    }

    @Override
    public CodeQualityProfileValidationResponse getQualityGateValidationDetails(String repoUrl, String repoBranch, String projectName, String artifactVersion, long beginDate, long endDate) {
        return null;
    }
}
