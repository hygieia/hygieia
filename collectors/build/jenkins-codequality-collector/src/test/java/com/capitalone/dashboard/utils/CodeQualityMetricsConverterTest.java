package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.JunitXmlReport;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by fzd332 on 10/12/16.
 */
public class CodeQualityMetricsConverterTest extends TestCase {

    @Test
    public void testValidCodeQualityMetricsIsCreatedBasedOnJunitXmlReport() {
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        JunitXmlReport xmlReport = new JunitXmlReport();
        xmlReport.setErrors(2);
        xmlReport.setFailures(1);
        xmlReport.setTests(14);
        Set<CodeQualityMetric> codeQualityMetrics = testee.analyse(xmlReport);

        assertThat(codeQualityMetrics).extracting("name", "formattedValue")
                                      .contains(tuple("test_failures", "1"), tuple("test_errors", "2"), tuple("tests", "14"), tuple("test_success_density", "11"));
    }
}