package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollItemConfigHistoryRepository;

import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class CodeQualityEvaluatorTest {
    @InjectMocks
    private CodeQualityEvaluator codeQualityEvaluator;
    @Mock
    private CodeQualityRepository codeQualityRepository;
    @Mock
    private CollItemConfigHistoryRepository collItemConfigHistoryRepository;



    @Test
    public void testEvaluate_CodeQualityNotConfigured(){
        CollectorItem c = null;
        CodeQualityAuditResponse response = codeQualityEvaluator.evaluate(c,125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_NOT_CONFIGURED"));
    }


    @Test
    public void testEvalatefor_COLLECTOR_ITEM_ERROR(){
        List<CodeQuality> codeQualities = makeCodeQualityGateDetailsFoundAuditFAIL("cloud-service-parent");
        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualities);

        CodeQualityAuditResponse response = codeQualityEvaluator.evaluate(createCollectorItem1(0),125634536, 6235263, null);

        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("COLLECTOR_ITEM_ERROR"));
        Assert.assertEquals(true, response.getMessage().toString().contains("Unable to collect scan results at this point - check Sonar project exist"));
    }

    @Test
    //Test for 5.3 version Code Quality Audit OK
    public void testEvalatefor_StatusMetAuditOk(){
        List<CodeQuality> codeQualities = makeCodeQualityGateDetailsMetricNotFound("cloud-service-parent");

        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualities);
        when(collItemConfigHistoryRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(null);

        CodeQualityAuditResponse response = codeQualityEvaluator.evaluate(createCollectorItem(100),125634536, 6235263, null);

        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_CHECK_IS_CURRENT"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_BLOCKER_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_UNIT_TEST_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_AUDIT_OK"));
        Assert.assertEquals(false, response.getAuditStatuses().toString().contains("CODE_QUALITY_GATES_FOUND"));
    }

    @Test
    //Test for 5.3 version Code Quality Audit FAIl
    public void testEvalatefor_StatusMet(){
        List<CodeQuality> codeQualities = makeCodeQualityGateDetailsFoundAuditFAIL("cloud-service-parent");

        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualities);
        when(collItemConfigHistoryRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(null);

        CodeQualityAuditResponse response = codeQualityEvaluator.evaluate(createCollectorItem(100),125634536, 6235263, null);

        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_CHECK_IS_CURRENT"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_BLOCKER_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("QUALITY_PROFILE_VALIDATION_AUDIT_NO_CHANGE"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CODE_COVERAGE_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_UNIT_TEST_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_AUDIT_FAIL"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_GATES_FOUND"));
    }


    @Test
    //Test for 6.7 version test for code Quality audit Ok
    public void testEvalatefor_StaticAnalysisResponse(){
        List<CodeQuality> codeQualities = makeCodeQualityOk("cloud-service-parent");

        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualities);
        when(collItemConfigHistoryRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(null);

        CodeQualityAuditResponse response = codeQualityEvaluator.evaluate(createCollectorItem(100),125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CRITICAL_FOUND"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_CHECK_IS_CURRENT"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CODE_COVERAGE_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_GATES_FOUND"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_AUDIT_OK"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CODE_COVERAGE_FOUND"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CRITICAL_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_BLOCKER_FOUND"));
    }

    @Test
    //Test for 6.7 version test for code Quality Audit Fail
    public void testEvalatefor_StaticAnalysisResponseAuditFail(){
        List<CodeQuality> codeQualities = makeCodeQualityFail("cloud-service-parent");

        when(codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(codeQualities);
        when(collItemConfigHistoryRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class),any(Long.class),any(Long.class))).thenReturn(null);

        CodeQualityAuditResponse response = codeQualityEvaluator.evaluate(createCollectorItem(100),125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CRITICAL_FOUND"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_CHECK_IS_CURRENT"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CODE_COVERAGE_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_GATES_FOUND"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_AUDIT_FAIL"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CODE_COVERAGE_FOUND"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_CRITICAL_MET"));
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_BLOCKER_FOUND"));
        Assert.assertEquals(false, response.getAuditStatuses().toString().contains("CODE_QUALITY_THRESHOLD_BLOCKER_MET"));
    }

    private CollectorItem createCollectorItem1(int lastUpdated) {
        CollectorItem items = new CollectorItem();
        items.setCollectorId(ObjectId.get());
        items.setEnabled(true);
        items.getOptions().put("jobName", "testHygieiaCodeQuality");
        items.getOptions().put("instanceUrl", "http://github.com/capone/hygieia");
        items.setLastUpdated(lastUpdated);
        return items;
    }

    private CollectorItem createCollectorItem(int projectId) {
        CollectorItem items = new CollectorItem();
        items.setCollectorId(ObjectId.get());
        items.setEnabled(true);
        items.getOptions().put("jobName", "testHygieiaCodeQuality");
        items.getOptions().put("instanceUrl", "http://github.com/capone/hygieia");
        items.getOptions().put("projectId", projectId);
        return items;
    }

    private List<CodeQuality> makeCodeQualityGateDetailsMetricNotFound(String name) {
        CodeQuality codeQuality = new CodeQuality();
        CodeQualityMetric  codeQualityMetric1 = new CodeQualityMetric();
        codeQualityMetric1.setName("test_success_density");
        codeQualityMetric1.setStatus(CodeQualityMetricStatus.Warning);
        CodeQualityMetric  codeQualityMetric2 = new CodeQualityMetric();
        codeQualityMetric2.setName("blocker_violations");
        codeQualityMetric2.setStatus(CodeQualityMetricStatus.Ok);
        codeQuality.addMetric(codeQualityMetric1);
        codeQuality.addMetric(codeQualityMetric2);
        List<CodeQuality> codeQualityList = new ArrayList<>();
        codeQualityList.add(codeQuality);
        return  codeQualityList;

    }

    private List<CodeQuality> makeCodeQualityGateDetailsFoundAuditFAIL(String name) {
        CodeQuality codeQuality = new CodeQuality();
        CodeQualityMetric  codeQualityMetric1 = new CodeQualityMetric();
        codeQualityMetric1.setName("blocker_violations");
        codeQualityMetric1.setStatus(CodeQualityMetricStatus.Ok);

        CodeQualityMetric  codeQualityMetric2 = new CodeQualityMetric();
        codeQualityMetric2.setName("coverage");
        codeQualityMetric2.setStatus(CodeQualityMetricStatus.Ok);

        CodeQualityMetric  codeQualityMetric3 = new CodeQualityMetric();
        codeQualityMetric3.setName("test_success_density");
        codeQualityMetric3.setStatus(CodeQualityMetricStatus.Warning);

        CodeQualityMetric  codeQualityMetric4 = new CodeQualityMetric();
        codeQualityMetric4.setName("quality_gate_details");
        codeQualityMetric4.setStatus(CodeQualityMetricStatus.Error);

        codeQuality.addMetric(codeQualityMetric2);
        codeQuality.addMetric(codeQualityMetric1);
        codeQuality.addMetric(codeQualityMetric3);
        codeQuality.addMetric(codeQualityMetric4);
        List<CodeQuality> codeQualityList = new ArrayList<>();
        codeQualityList.add(codeQuality);
        return codeQualityList;
    }

    private List<CodeQuality> makeCodeQualityOk(String name) {
        CodeQuality codeQuality = new CodeQuality();
        CodeQualityMetric  codeQualityMetric1 = new CodeQualityMetric();
        codeQualityMetric1.setName("quality_gate_details");
        codeQualityMetric1.setStatus(null);
        codeQualityMetric1.setValue("{\"level\":\"WARN\",\"conditions\":[{\"metric\":\"blocker_violations\",\"op\":\"GT\",\"warning\":\"\",\"error\":\"0\",\"actual\":\"0\",\"level\":\"WARN\"},{\"metric\":\"critical_violations\",\"op\":\"GT\",\"warning\":\"\",\"error\":\"0\",\"actual\":\"0\",\"level\":\"OK\"},{\"metric\":\"major_violations\",\"op\":\"GT\",\"warning\":\"\",\"error\":\"0\",\"actual\":\"0\",\"level\":\"OK\"},{\"metric\":\"minor_violations\",\"op\":\"GT\",\"warning\":\"60\",\"error\":\"70\",\"actual\":\"65\",\"level\":\"WARN\"},{\"metric\":\"coverage\",\"op\":\"LT\",\"warning\":\"95\",\"error\":\"90\",\"actual\":\"93.5\",\"level\":\"OK\"},{\"metric\":\"line_coverage\",\"op\":\"LT\",\"warning\":\"95\",\"error\":\"90\",\"actual\":\"96.3\",\"level\":\"OK\"}],\"ignoredConditions\":false}");
        codeQuality.addMetric(codeQualityMetric1);
        List<CodeQuality> codeQualityList = new ArrayList<>();
        codeQualityList.add(codeQuality);
        return codeQualityList;
    }
    private List<CodeQuality> makeCodeQualityFail(String name) {
        CodeQuality codeQuality = new CodeQuality();
        CodeQualityMetric  codeQualityMetric = new CodeQualityMetric();
        codeQualityMetric.setName("quality_gate_details");
        codeQualityMetric.setStatus(null);
        codeQualityMetric.setValue("{\"level\":\"ERROR\",\"conditions\":[{\"metric\":\"blocker_violations\",\"op\":\"GT\",\"warning\":\"\",\"error\":\"0\",\"actual\":\"0\",\"level\":\"ERROR\"},{\"metric\":\"critical_violations\",\"op\":\"GT\",\"warning\":\"\",\"error\":\"0\",\"actual\":\"0\",\"level\":\"OK\"},{\"metric\":\"major_violations\",\"op\":\"GT\",\"warning\":\"\",\"error\":\"0\",\"actual\":\"0\",\"level\":\"OK\"},{\"metric\":\"minor_violations\",\"op\":\"GT\",\"warning\":\"60\",\"error\":\"70\",\"actual\":\"65\",\"level\":\"WARN\"},{\"metric\":\"coverage\",\"op\":\"LT\",\"warning\":\"95\",\"error\":\"90\",\"actual\":\"93.5\",\"level\":\"OK\"},{\"metric\":\"line_coverage\",\"op\":\"LT\",\"warning\":\"95\",\"error\":\"90\",\"actual\":\"96.3\",\"level\":\"OK\"}],\"ignoredConditions\":false}");

        codeQuality.addMetric(codeQualityMetric);
        List<CodeQuality> codeQualityList = new ArrayList<>();
        codeQualityList.add(codeQuality);
        return codeQualityList;
    }

}