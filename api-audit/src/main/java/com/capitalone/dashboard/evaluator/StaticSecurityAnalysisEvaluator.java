package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.SecurityPolicy;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.response.SecurityReviewAuditResponse;
import com.capitalone.dashboard.status.CodeQualityAuditStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

@Component
public class StaticSecurityAnalysisEvaluator extends Evaluator<SecurityReviewAuditResponse> {

    private final CodeQualityRepository codeQualityRepository;
    private static final String STR_SCORE = "Score";
    private static final String STR_REPORT_URL = "reportUrl";

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
        securityReviewAuditResponse.setUrl(collectorItem.getOptions().get(STR_REPORT_URL).toString());
        securityReviewAuditResponse.setCodeQuality(returnQuality);
        securityReviewAuditResponse.setLastExecutionTime(returnQuality.getTimestamp());

        Set<CodeQualityMetric> metrics = returnQuality.getMetrics();
        boolean isSecurityCriticalAndOpen = metrics.stream()
                .filter(metric -> metric.getName().equalsIgnoreCase(SecurityPolicy.SecurityLevel.Critical.name()))
                .anyMatch(metric -> isSecurityStatusOpen(metric.getDisposition()));
        boolean isSecurityHighAndOpen = metrics.stream()
                .filter(metric -> metric.getName().equalsIgnoreCase(SecurityPolicy.SecurityLevel.High.name()))
                .anyMatch(metric -> isSecurityStatusOpen(metric.getDisposition()));

        if (isSecurityCriticalAndOpen){
            securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FOUND_CRITICAL);
        }else if (isSecurityHighAndOpen){
            securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FOUND_HIGH);
        }else{
            CodeQualityMetric scoreMetric = metrics.stream().filter(metric -> metric.getName().equalsIgnoreCase(STR_SCORE)).findFirst().get();
            String scoreVal = scoreMetric.getValue();
            Integer iScore = StringUtils.isNumeric(scoreVal)? Integer.parseInt(scoreVal) : 0;
            if (iScore == 100) {
                securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_NO_CLOSED_FINDINGS);
            } else if (iScore > 0) {
                securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_OK);
            } else {
                securityReviewAuditResponse.addAuditStatus(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FAIL);
            }
        }
        return securityReviewAuditResponse;
    }

    /***
     * security status is considered open if its empty/null or exists in open list or not exists in closed list
     * @param status
     * @return boolean
     */
    private boolean isSecurityStatusOpen(String status) {
        return (Strings.isNullOrEmpty(status)
                || SecurityPolicy.DispositionOpenStatus.contains(status)
                || !SecurityPolicy.DispositionClosedStatus.contains(status));
    }
}
