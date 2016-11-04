package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by fzd332 on 10/12/16.
 */
@Component
public class CodeQualityMetricsConverter implements CodeQualityVisitor {

    private static final String TOTAL_NO_OF_TESTS = "tests";
    private static final String TEST_FAILURES = "test_failures";
    private static final String TEST_ERRORS = "test_errors";
    private static final String TEST_SUCCESS_DENSITY = "test_success_density";

    private final CodeQuality quality = new CodeQuality();

    // note for static analysis names are blocker_violations,critical_violations,major_violations,violations
    // function tests..

    @Override
    public void visit(JunitXmlReport report) {

        int testsPassed = report.getTests() - report.getFailures() - report.getErrors();

        Map<String, Pair<Integer, CodeQualityMetricStatus>> metricsMap = new HashMap<>();
        metricsMap.put(TOTAL_NO_OF_TESTS, Pair.of(report.getTests(), CodeQualityMetricStatus.Ok));
        metricsMap.put(TEST_FAILURES, Pair.of(report.getFailures(), report.getFailures() > 0 ? CodeQualityMetricStatus.Warning : CodeQualityMetricStatus.Ok));
        metricsMap.put(TEST_ERRORS, Pair.of(report.getErrors(), report.getErrors() > 0 ? CodeQualityMetricStatus.Alert : CodeQualityMetricStatus.Ok));
        metricsMap.put(TEST_SUCCESS_DENSITY, Pair.of(testsPassed, CodeQualityMetricStatus.Ok));

        long timestamp = Math.max(quality.getTimestamp(), report.getTimestamp().toGregorianCalendar().getTimeInMillis());
        quality.setTimestamp(timestamp);
        quality.setType(CodeQualityType.StaticAnalysis);

        Set<CodeQualityMetric> existingMetrics = quality.getMetrics();
        final Map<String, CodeQualityMetric> mapOfExistingMetrics = existingMetrics.stream().collect(Collectors.toMap(CodeQualityMetric::getName, Function.identity()));

        metricsMap.forEach((key, value) -> {

            CodeQualityMetric currentValue = mapOfExistingMetrics.get(key);
            CodeQualityMetric newValue = null;
            if (null == currentValue) {
                CodeQualityMetric codeQualityMetric = new CodeQualityMetric();
                codeQualityMetric.setName(key);
                codeQualityMetric.setFormattedValue(String.valueOf(value.getLeft()));
                codeQualityMetric.setValue(value.getLeft());
                codeQualityMetric.setStatus(value.getRight());
                newValue = codeQualityMetric;
            } else {
                // do the sum
                quality.getMetrics().remove(currentValue);
                newValue = new CodeQualityMetric(key);
                newValue.setValue((int) currentValue.getValue() + value.getLeft());
                newValue.setFormattedValue(String.valueOf((int) currentValue.getValue() + value.getLeft()));
                int newOrdinal = Math.max(value.getRight().ordinal(), currentValue.getStatus().ordinal());
                newValue.setStatus(CodeQualityMetricStatus.values()[newOrdinal]);
            }
            quality.addMetric(newValue);
        });

    }

    @Override
    public void visit(FindBubsXmlReport visitee) {
        throw new NotImplementedException();
    }

    @Override
    public CodeQuality produceResult() {
        return quality;
    }

}
