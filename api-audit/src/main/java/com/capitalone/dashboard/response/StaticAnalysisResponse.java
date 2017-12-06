package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.CodeQuality;


public class StaticAnalysisResponse extends AuditReviewResponse {
	 private CodeQuality codeQualityDetails;

    public CodeQuality getCodeQualityDetails() {
        return codeQualityDetails;
    }

    public void setCodeQualityDetails(CodeQuality codeQualityDetails) {
        this.codeQualityDetails = codeQualityDetails;
    }

}
