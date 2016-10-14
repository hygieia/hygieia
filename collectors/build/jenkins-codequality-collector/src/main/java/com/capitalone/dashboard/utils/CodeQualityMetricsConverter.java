package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.JunitXmlReport;
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

        String testsPassed = String.valueOf(report.getTests() - report.getFailures() - report.getErrors());

        Map<String, String> metricsMap = new HashMap<>();
        metricsMap.put(TOTAL_NO_OF_TESTS, String.valueOf(report.getTests()));
        metricsMap.put(TEST_FAILURES, String.valueOf(report.getFailures()));
        metricsMap.put(TEST_ERRORS, String.valueOf(report.getErrors()));
        metricsMap.put(TEST_SUCCESS_DENSITY, testsPassed);

        metricsMap.forEach((key, value) -> {
            CodeQualityMetric codeQualityMetric = new CodeQualityMetric();
            codeQualityMetric.setName(key);
            codeQualityMetric.setFormattedValue(value);
            codeQualityMetrics.add(codeQualityMetric);
        });



        return codeQualityMetrics;
    }
}
