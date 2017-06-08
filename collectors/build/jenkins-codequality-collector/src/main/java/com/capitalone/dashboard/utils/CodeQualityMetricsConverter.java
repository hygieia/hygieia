package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

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

    private static final String BLOCKER_VIOLATIONS = "blocker_violations";
    private static final String CRITICAL_VIOLATIONS = "critical_violations";
    private static final String MAJOR_VIOLCATIONS = "major_violations";
    private static final String VIOLATIONS = "violations";

    private final CodeQuality quality = new CodeQuality();

    // note for static analysis names are ,,,violations
    // function tests..

    @Override
    public void visit(JunitXmlReport report) {

        int testsPassed = report.getTests() - report.getFailures() - report.getErrors();

        Map<String, Pair<Integer, CodeQualityMetricStatus>> metricsMap = new HashMap<>();
        metricsMap.put(TOTAL_NO_OF_TESTS, Pair.of(report.getTests(), CodeQualityMetricStatus.Ok));
        metricsMap.put(TEST_FAILURES, Pair.of(report.getFailures(), report.getFailures() > 0 ? CodeQualityMetricStatus.Warning : CodeQualityMetricStatus.Ok));
        metricsMap.put(TEST_ERRORS, Pair.of(report.getErrors(), report.getErrors() > 0 ? CodeQualityMetricStatus.Alert : CodeQualityMetricStatus.Ok));
        metricsMap.put(TEST_SUCCESS_DENSITY, Pair.of(testsPassed, CodeQualityMetricStatus.Ok));

        if (null != report.getTimestamp()) {
            long timestamp = Math.max(quality.getTimestamp(), report.getTimestamp().toGregorianCalendar().getTimeInMillis());
            quality.setTimestamp(timestamp);
        }
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
    public void visit(FindBubsXmlReport findBugReport) {
        Map<String, Pair<Integer, CodeQualityMetricStatus>> metricsMap = new HashMap<>();
        metricsMap.put(BLOCKER_VIOLATIONS, Pair.of(0, CodeQualityMetricStatus.Ok));
        metricsMap.put(CRITICAL_VIOLATIONS, Pair.of(0, CodeQualityMetricStatus.Ok));
        metricsMap.put(MAJOR_VIOLCATIONS, Pair.of(0, CodeQualityMetricStatus.Ok));
        metricsMap.put(VIOLATIONS, Pair.of(0, CodeQualityMetricStatus.Ok));

        // loop over all the stuff in the report and accumulate violations.
        if (null != findBugReport.getFiles()) {
            findBugReport.getFiles().stream().forEach(bugFile -> {
                bugFile.getBugCollection().stream().forEach(
                        bugInstance -> {
                            switch (bugInstance.getPriority()) {
                                case Blocker: {
                                    final Pair<Integer, CodeQualityMetricStatus> metricStatusPair = metricsMap.get(BLOCKER_VIOLATIONS);
                                    final Pair newPair = Pair.of(metricStatusPair.getLeft().intValue() + 1, CodeQualityMetricStatus.Alert);
                                    metricsMap.put(BLOCKER_VIOLATIONS, newPair);
                                    break;
                                }
                                case Critical: {
                                    final Pair<Integer, CodeQualityMetricStatus> metricStatusPair = metricsMap.get(CRITICAL_VIOLATIONS);
                                    final Pair newPair = Pair.of(metricStatusPair.getLeft().intValue() + 1, CodeQualityMetricStatus.Alert);
                                    metricsMap.put(CRITICAL_VIOLATIONS, newPair);
                                    break;
                                }
                                case Normal: {
                                    final Pair<Integer, CodeQualityMetricStatus> metricStatusPair = metricsMap.get(MAJOR_VIOLCATIONS);
                                    final Pair newPair = Pair.of(metricStatusPair.getLeft().intValue() + 1, CodeQualityMetricStatus.Warning);
                                    metricsMap.put(MAJOR_VIOLCATIONS, newPair);
                                    break;
                                }
                                case Low: {
                                    final Pair<Integer, CodeQualityMetricStatus> metricStatusPair = metricsMap.get(VIOLATIONS);
                                    final Pair newPair = Pair.of(metricStatusPair.getLeft().intValue() + 1, CodeQualityMetricStatus.Warning);
                                    metricsMap.put(VIOLATIONS, newPair);
                                    break;
                                }
                                default:
                                    // not recognised. ignore
                                    break;
                            }
                        }
                );
            });
        }


        // finally produce the result
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
    public CodeQuality produceResult() {
        return quality;
    }

}
