package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.JunitXmlReport;

import java.util.*;

/**
 * Created by fzd332 on 10/12/16.
 */
public class CodeQualityMetricsConverter implements CodeQualityConverter{

    private static final String TOTAL_NO_OF_TESTS = "tests";
    private static final String TEST_FAILURES = "test_failures";
    private static final String TEST_ERRORS = "test_errors";
    private static final String TEST_SUCCESS_DENSITY = "test_success_density";


    @Override
    public Set<CodeQualityMetric> analyse(JunitXmlReport report) {
        Set<CodeQualityMetric> codeQualityMetrics = new HashSet<>();

        String tests_passed = String.valueOf(report.getTests() - report.getFailures() - report.getErrors());

        Map<String, String> metricsMap = new HashMap<>();
        metricsMap.put(TOTAL_NO_OF_TESTS, String.valueOf(report.getTests()));
        metricsMap.put(TEST_FAILURES, String.valueOf(report.getFailures()));
        metricsMap.put(TEST_ERRORS, String.valueOf(report.getErrors()));
        metricsMap.put(TEST_SUCCESS_DENSITY, tests_passed);

        metricsMap.forEach((key, value) -> {
            CodeQualityMetric codeQualityMetric = new CodeQualityMetric();
            codeQualityMetric.setName(key);
            codeQualityMetric.setFormattedValue(value);
            codeQualityMetrics.add(codeQualityMetric);
        });



        return codeQualityMetrics;
    }
}
