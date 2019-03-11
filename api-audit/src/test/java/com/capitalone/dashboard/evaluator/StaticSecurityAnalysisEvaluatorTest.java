package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.response.SecurityReviewAuditResponse;
import com.capitalone.dashboard.status.CodeQualityAuditStatus;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class StaticSecurityAnalysisEvaluatorTest {

    @InjectMocks
    private StaticSecurityAnalysisEvaluator staticSecurityAnalysisEvaluator;

    @Mock
    private CodeQualityRepository codeQualityRepository;

    @Test
    public void testEvaluate_StaticSecurityMissing(){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(ObjectId.get());
        SecurityReviewAuditResponse response = staticSecurityAnalysisEvaluator.evaluate(collectorItem, 125634536, 6235243, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_MISSING.name()));
    }

    @Test
    public void testEvaluate_StaticSecurityCritical(){

        List<CodeQuality> codeQualitiesCritical = getSecurityCodeQualityData("Critical", CodeQualityMetricStatus.Alert, "");
        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualitiesCritical);
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.getOptions().put("reportUrl", "");
        SecurityReviewAuditResponse response = staticSecurityAnalysisEvaluator.evaluate(collectorItem, 125634436, 125634636, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FOUND_CRITICAL.name()));
    }

    @Test
    public void testEvaluate_StaticSecurityHigh(){

        List<CodeQuality> codeQualitiesCritical = getSecurityCodeQualityData("High", CodeQualityMetricStatus.Alert, "");
        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualitiesCritical);
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.getOptions().put("reportUrl", "");
        SecurityReviewAuditResponse response = staticSecurityAnalysisEvaluator.evaluate(collectorItem, 125634436, 125634636, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FOUND_HIGH.name()));
    }

    @Test
    public void testEvaluate_StaticSecurityOk(){

        List<CodeQuality> codeQualitiesCritical = getSecurityCodeQualityData("Score", CodeQualityMetricStatus.Ok, "5");
        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualitiesCritical);
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.getOptions().put("reportUrl", "");
        SecurityReviewAuditResponse response = staticSecurityAnalysisEvaluator.evaluate(collectorItem, 125634436, 125634636, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_OK.name()));
    }

    @Test
    public void testEvaluate_StaticSecurityFail(){

        List<CodeQuality> codeQualitiesCritical = getSecurityCodeQualityData("Score", CodeQualityMetricStatus.Alert, "0");
        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualitiesCritical);
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.getOptions().put("reportUrl", "");
        SecurityReviewAuditResponse response = staticSecurityAnalysisEvaluator.evaluate(collectorItem, 125634436, 125634636, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_FAIL.name()));
    }

    @Test
    public void testEvaluate_StaticSecurityNoClosedFindings(){

        List<CodeQuality> codeQualitiesCritical = getSecurityCodeQualityData("Score", CodeQualityMetricStatus.Warning, "100");
        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualitiesCritical);
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.getOptions().put("reportUrl", "");
        SecurityReviewAuditResponse response = staticSecurityAnalysisEvaluator.evaluate(collectorItem, 125634436, 125634636, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains(CodeQualityAuditStatus.STATIC_SECURITY_SCAN_NO_CLOSED_FINDINGS.name()));
    }

    private List<CodeQuality> getSecurityCodeQualityData(String securityLevel, CodeQualityMetricStatus status, String securityScore){
        CodeQuality codeQuality = new CodeQuality();
        codeQuality.setCollectorItemId(ObjectId.get());
        codeQuality.setType(CodeQualityType.SecurityAnalysis);
        codeQuality.setTimestamp(125634536);
        CodeQualityMetric codeQualityMetric = new CodeQualityMetric();
        codeQualityMetric.setName(securityLevel);
        codeQualityMetric.setStatus(status);
        codeQualityMetric.setValue(securityScore);
        codeQuality.addMetric(codeQualityMetric);
        return Arrays.asList(codeQuality);
    }
}
