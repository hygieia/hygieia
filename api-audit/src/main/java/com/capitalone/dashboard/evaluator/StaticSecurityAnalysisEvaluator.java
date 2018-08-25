package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.response.SecurityReviewAuditResponse;
import com.capitalone.dashboard.status.CodeQualityAuditStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StaticSecurityAnalysisEvaluator extends Evaluator<SecurityReviewAuditResponse> {

    private final CodeQualityRepository codeQualityRepository;

    @Autowired
    public StaticSecurityAnalysisEvaluator(CodeQualityRepository codeQualityRepository) {
        this.codeQualityRepository = codeQualityRepository;
    }

    @Override
    public Collection<SecurityReviewAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> data) throws AuditException {

        List<CollectorItem> staticSecurityScanItems = getCollectorItems(dashboard, "codeanalysis", CollectorType.StaticSecurityScan);
        if (CollectionUtils.isEmpty(staticSecurityScanItems)) {
            throw new AuditException("No code quality job configured", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }

        return staticSecurityScanItems.stream().map(item -> evaluate(item, beginDate, endDate, null)).collect(Collectors.toList());
    }

    @Override
    public SecurityReviewAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> data) {
        return getStaticSecurityScanResponse(collectorItem, beginDate, endDate);
    }

    /**
     * Reusable method for constructing the CodeQualityAuditResponse object
     *
     * @param collectorItem Collector Item
     * @param beginDate Begin Date
     * @param endDate End Date
     * @return SecurityReviewAuditResponse
     */
    private SecurityReviewAuditResponse getStaticSecurityScanResponse(CollectorItem collectorItem, long beginDate, long endDate) {
        List<CodeQuality> codeQualities = codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(collectorItem.getId(), beginDate - 1, endDate + 1);

        ObjectMapper mapper = new ObjectMapper();
        SecurityReviewAuditResponse securityReviewAuditResponse = new SecurityReviewAuditResponse();

        if (CollectionUtils.isEmpty(codeQualities)) {
            securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_MISSING);
            return securityReviewAuditResponse;
        }

        CodeQuality returnQuality = codeQualities.get(0);
        securityReviewAuditResponse.setUrl(collectorItem.getOptions().get("reportUrl").toString());
        securityReviewAuditResponse.setCodeQuality(returnQuality);
        securityReviewAuditResponse.setLastExecutionTime(returnQuality.getTimestamp());

        returnQuality.getMetrics().forEach(metric -> {
            if (metric.getName().equalsIgnoreCase("Score")) {
                Integer value = Integer.parseInt(metric.getValue());
                if (value == 100) {
                    securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_NO_CLOSED_FINDINGS);
                } else if (value > 0) {
                    securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_OK);
                } else {
                    securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FAIL);
                }
            } else if (metric.getName().equalsIgnoreCase("Critical")) {
                securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FOUND_CRITICAL);
            } else if (metric.getName().equalsIgnoreCase("High")) {
                securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FOUND_HIGH);
            }
        });
        return securityReviewAuditResponse;
    }

}
