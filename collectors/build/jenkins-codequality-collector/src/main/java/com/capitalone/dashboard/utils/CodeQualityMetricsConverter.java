package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.JunitXmlReport;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fzd332 on 10/12/16.
 */
public class CodeQualityMetricsConverter implements CodeQualityConverter{

    @Override
    public Set<CodeQualityMetric> analyse(JunitXmlReport report) {
        Set<CodeQualityMetric> codeQualityMetrics = new HashSet<>();



        CodeQualityMetric codeQualityMetric = new CodeQualityMetric();
        codeQualityMetric.setName("tests");
        codeQualityMetric.setFormattedValue(String.valueOf(report.getTests()));

        CodeQualityMetric codeQualityMetric1 = new CodeQualityMetric();
        codeQualityMetric1.setName("test_failures");
        codeQualityMetric1.setFormattedValue(String.valueOf(report.getFailures()));

        CodeQualityMetric codeQualityMetric2 = new CodeQualityMetric();
        codeQualityMetric2.setName("test_errors");
        codeQualityMetric2.setFormattedValue(String.valueOf(report.getErrors()));

        codeQualityMetrics.add(codeQualityMetric);
        codeQualityMetrics.add(codeQualityMetric1);
        codeQualityMetrics.add(codeQualityMetric2);



        return codeQualityMetrics;
    }
}
