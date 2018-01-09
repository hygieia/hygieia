package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.response.QualityProfileAuditResponse;
import com.capitalone.dashboard.response.CodeQualityAuditResponse;

import java.io.IOException;
import java.util.List;

public interface CodeQualityAuditService {

    List<CodeQualityAuditResponse> getCodeQualityAudit(String projectName, String artifactVersion) throws IOException, HygieiaException;

    QualityProfileAuditResponse getQualityGateValidationDetails(String repoUrl, String repoBranch, String projectName, String artifactVersion, long beginDate, long endDate) throws HygieiaException;



}
