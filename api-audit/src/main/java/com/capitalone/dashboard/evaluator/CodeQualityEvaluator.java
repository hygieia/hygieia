package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.common.CommonCodeReview;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorItemConfigHistory;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollItemConfigHistoryRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import com.capitalone.dashboard.status.CodeQualityAuditStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

@Component
public class CodeQualityEvaluator extends Evaluator<CodeQualityAuditResponse> {

    private final CodeQualityRepository codeQualityRepository;
    private final CommitRepository commitRepository;
    private final CollItemConfigHistoryRepository collItemConfigHistoryRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeQualityEvaluator.class);


    @Autowired
    public CodeQualityEvaluator(CodeQualityRepository codeQualityRepository, CommitRepository commitRepository, CollItemConfigHistoryRepository collItemConfigHistoryRepository) {
        this.codeQualityRepository = codeQualityRepository;
        this.commitRepository = commitRepository;
        this.collItemConfigHistoryRepository = collItemConfigHistoryRepository;
    }


    @Override
    public Collection<CodeQualityAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> data) throws AuditException {

        List<CollectorItem> codeQualityItems = getCollectorItems(dashboard, "codeanalysis", CollectorType.CodeQuality);
        if (CollectionUtils.isEmpty(codeQualityItems)) {
            throw new AuditException("No code quality job configured", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }

        List<CollectorItem> repoItems = getCollectorItems(dashboard, "repo", CollectorType.SCM);

        Map<String, List<CollectorItem>> repoData = new HashMap<>();
        repoData.put("repos", repoItems);

        return codeQualityItems.stream().map(item -> evaluate(item, beginDate, endDate, repoData)).collect(Collectors.toList());
    }


    @Override
    public CodeQualityAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> data) {
        List<CollectorItem> repoItems;
        if (!MapUtils.isEmpty(data) &&
                (data.get("repos") instanceof List) &&
                !CollectionUtils.isEmpty(Collections.singleton(data.get("repos"))) &&
                (((List) data.get("repos")).get(0) instanceof CollectorItem)) {
            repoItems = (List<CollectorItem>) data.get("repos");

        } else {
            repoItems = new ArrayList<>();
        }
        return getStaticAnalysisResponse(collectorItem, repoItems, beginDate, endDate);
    }


    /**
     * Reusable method for constructing the CodeQualityAuditResponse object for a
     *
     * @return CodeQualityAuditResponse
     */
    private CodeQualityAuditResponse getStaticAnalysisResponse(CollectorItem collectorItem, List<CollectorItem> repoItems, long beginDate, long endDate) {
        List<CodeQuality> codeQualities = codeQualityRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(collectorItem.getId(), beginDate-1, endDate+1);
        ObjectMapper mapper = new ObjectMapper();
        CodeQualityAuditResponse codeQualityAuditResponse = new CodeQualityAuditResponse();

        if (CollectionUtils.isEmpty(codeQualities)) {
            return codeQualityAuditResponse;
        } else {
            codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_CHECK_IS_CURRENT);
        }

        CodeQuality returnQuality = codeQualities.get(0);
        codeQualityAuditResponse.setUrl(returnQuality.getUrl());
        codeQualityAuditResponse.setCodeQuality(returnQuality);
        codeQualityAuditResponse.setLastExecutionTime(returnQuality.getTimestamp());

        for (CodeQualityMetric metric : returnQuality.getMetrics()) {
            //TODO: This is sonar specific - need to move this to api settings via properties file
            if (metric.getName().equalsIgnoreCase("quality_gate_details")) {
                codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_GATES_FOUND);
                try {
                    JSONObject qualityGateDetails = (JSONObject) new JSONParser().parse(metric.getValue().toString());
                    JSONArray conditions = (JSONArray) qualityGateDetails.get("conditions");
                    Iterator itr = conditions.iterator();

                    // Iterate through the quality_gate conditions
                    while(itr.hasNext()) {
                        Map condition = ((Map) itr.next());

                        // Set audit statuses for Thresholds found if they are defined in quality_gate_details metric
                        if(condition.get("metric").toString().equalsIgnoreCase("blocker_violations") &&
                                condition.containsKey("error")) {
                            codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_THRESHOLD_BLOCKER_FOUND);
                        } else if(condition.get("metric").toString().equalsIgnoreCase("critical_violations") &&
                                condition.containsKey("error")) {
                            codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_THRESHOLD_CRITICAL_FOUND);
                        } else if(condition.get("metric").toString().equalsIgnoreCase("test_success_density") &&
                                condition.containsKey("error")) {
                            codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_THRESHOLD_UNIT_TEST_FOUND);
                        } else if(condition.get("metric").toString().equalsIgnoreCase("new_coverage") &&
                                condition.containsKey("error")) {
                            codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_THRESHOLD_CODE_COVERAGE_FOUND);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (metric.getStatus() != null) {
                    codeQualityAuditResponse.addAuditStatus("ok".equalsIgnoreCase(metric.getStatus().toString()) ? CodeQualityAuditStatus.CODE_QUALITY_AUDIT_OK : CodeQualityAuditStatus.CODE_QUALITY_AUDIT_FAIL);
                } else {
                    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
                    };
                    Map<String, String> values;
                    try {
                        values = mapper.readValue((String) metric.getValue(), typeRef);
                        if (MapUtils.isNotEmpty(values) && values.containsKey("level")) {
                            String level = values.get("level");
                            codeQualityAuditResponse.addAuditStatus(level.equalsIgnoreCase("ok") ? CodeQualityAuditStatus.CODE_QUALITY_AUDIT_OK : CodeQualityAuditStatus.CODE_QUALITY_AUDIT_FAIL);
                        }
                        break;
                    } catch (IOException e) {
                        LOGGER.error("Error in CodeQualityEvaluator.getStaticAnalysisResponse() - Unable to parse quality_gate metrics - " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            // Set audit statuses if the threshold is met based on the status field of code_quality metric
            if (metric.getName().equalsIgnoreCase("blocker_violations") &&
                    metric.getStatus().equals(CodeQualityMetricStatus.Ok)) {
                codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_THRESHOLD_BLOCKER_MET);
            } else if (metric.getName().equalsIgnoreCase("critical_violations") &&
                    metric.getStatus().equals(CodeQualityMetricStatus.Ok)) {
                codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_THRESHOLD_CRITICAL_MET);
            } else if (metric.getName().equalsIgnoreCase("new_coverage") &&
                    metric.getStatus().equals(CodeQualityMetricStatus.Ok)) {
                codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_THRESHOLD_CODE_COVERAGE_MET);
            } else if (metric.getName().equalsIgnoreCase("test_success_density") &&
                    metric.getStatus().equals(CodeQualityMetricStatus.Ok)) {
                codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.CODE_QUALITY_THRESHOLD_UNIT_TEST_MET);
            }
        }


        List<CollectorItemConfigHistory> configHistories = getProfileChanges(returnQuality, beginDate, endDate);
        if (CollectionUtils.isEmpty(configHistories)) {
            codeQualityAuditResponse.addAuditStatus(CodeQualityAuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_NO_CHANGE);
            return codeQualityAuditResponse;
        }
        Set<String> codeAuthors = CommonCodeReview.getCodeAuthors(repoItems, beginDate, endDate, commitRepository);
        List<String> overlap = configHistories.stream().map(CollectorItemConfigHistory::getUserID).filter(codeAuthors::contains).collect(Collectors.toList());
        codeQualityAuditResponse.addAuditStatus(!CollectionUtils.isEmpty(overlap) ? CodeQualityAuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_FAIL : CodeQualityAuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_OK);

        return codeQualityAuditResponse;
    }


    private List<CollectorItemConfigHistory> getProfileChanges(CodeQuality codeQuality, long beginDate, long endDate) {
        return collItemConfigHistoryRepository
                .findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(codeQuality.getCollectorItemId(), beginDate - 1, endDate + 1);
    }
}
