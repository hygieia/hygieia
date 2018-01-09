package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.response.CodeQualityProfileValidationResponse;
import com.capitalone.dashboard.response.StaticAnalysisResponse;

import java.io.IOException;
import java.util.List;

public interface CodeQualityAuditService {

    List<StaticAnalysisResponse> getCodeQualityAudit(String projectName, String artifactVersion) throws IOException, HygieiaException;

    CodeQualityProfileValidationResponse getQualityGateValidationDetails(String repoUrl, String repoBranch, String projectName, String artifactVersion, long beginDate, long endDate) throws HygieiaException;



}
