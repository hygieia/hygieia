package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.JunitXmlReport;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by fzd332 on 10/12/16.
 */
@Component
public class CodeQualityMetricsConverter implements CodeQualityConverter{

    private static final String TOTAL_NO_OF_TESTS = "tests";
    private static final String TEST_FAILURES = "test_failures";
    private static final String TEST_ERRORS = "test_errors";
    private static final String TEST_SUCCESS_DENSITY = "test_success_density";


    @Override
    public Set<CodeQualityMetric> analyse(JunitXmlReport report) {
        Set<CodeQualityMetric> codeQualityMetrics = new HashSet<>();

        Integer testsPassed = report.getTests() - report.getFailures() - report.getErrors();

        Map<String, Pair<Integer, CodeQualityMetricStatus>> metricsMap = new HashMap<>();
        metricsMap.put(TOTAL_NO_OF_TESTS, Pair.of(report.getTests(), CodeQualityMetricStatus.Ok));
        metricsMap.put(TEST_FAILURES, Pair.of(report.getFailures(), CodeQualityMetricStatus.Warning));
        metricsMap.put(TEST_ERRORS, Pair.of(report.getErrors(), CodeQualityMetricStatus.Alert));
        metricsMap.put(TEST_SUCCESS_DENSITY, Pair.of(testsPassed, CodeQualityMetricStatus.Ok));

        metricsMap.forEach((key, value) -> {
            CodeQualityMetric codeQualityMetric = new CodeQualityMetric();
            codeQualityMetric.setName(key);
            codeQualityMetric.setFormattedValue(String.valueOf(value.getLeft()));
            codeQualityMetric.setValue(value.getLeft());
            codeQualityMetric.setStatus(value.getRight());
            codeQualityMetrics.add(codeQualityMetric);
        });


        return codeQualityMetrics;
    }

}
