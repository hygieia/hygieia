package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Test;

import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by fzd332 on 10/12/16.
 */
public class CodeQualityMetricsConverterTest {

    @Test
    public void validCodeQualityMetricsIsCreatedBasedOnJunitXmlReport() throws Exception {
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        JunitXmlReport xmlReport = new JunitXmlReport();
        xmlReport.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1980, 10, 10, 10, 10, 10, 10, 0));
        xmlReport.setErrors(2);
        xmlReport.setFailures(1);
        xmlReport.setTests(14);

        // do the test!
        xmlReport.accept(testee);
        CodeQuality producedQuality = testee.produceResult();

        Set<CodeQualityMetric> codeQualityMetrics = producedQuality.getMetrics();
        assertThat(codeQualityMetrics).extracting("name", "formattedValue", "value", "status")
                .contains(tuple("test_failures", "1", 1, CodeQualityMetricStatus.Warning),
                        tuple("test_errors", "2", 2, CodeQualityMetricStatus.Alert),
                        tuple("tests", "14", 14, CodeQualityMetricStatus.Ok),
                        tuple("test_success_density", "11", 11, CodeQualityMetricStatus.Ok));
    }

    @Test
    public void zeroErrorsAndFailuresIsOkay() throws Exception {
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        JunitXmlReport xmlReport = new JunitXmlReport();
        xmlReport.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1980, 10, 10, 10, 10, 10, 10, 0));
        xmlReport.setErrors(0);
        xmlReport.setFailures(0);
        xmlReport.setTests(14);

        // do the test!
        xmlReport.accept(testee);
        CodeQuality producedQuality = testee.produceResult();

        Set<CodeQualityMetric> codeQualityMetrics = producedQuality.getMetrics();
        assertThat(codeQualityMetrics).extracting("name", "formattedValue", "value", "status")
                .contains(tuple("test_failures", "0", 0, CodeQualityMetricStatus.Ok),
                        tuple("test_errors", "0", 0, CodeQualityMetricStatus.Ok),
                        tuple("tests", "14", 14, CodeQualityMetricStatus.Ok),
                        tuple("test_success_density", "14", 14, CodeQualityMetricStatus.Ok));
    }

    @Test
    public void updatesCodeQualityWithCorrectTimestampAndDoesAdding() throws Exception {

        JunitXmlReport testXmlReport = new JunitXmlReport();
        testXmlReport.setErrors(1);
        testXmlReport.setFailures(0);
        testXmlReport.setTests(8);
        testXmlReport.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1980, 10, 10, 10, 10, 10, 10, 0));
        JunitXmlReport testXmlReport1 = new JunitXmlReport();
        testXmlReport1.setErrors(1);
        testXmlReport1.setFailures(1);
        testXmlReport1.setTests(6);
        testXmlReport1.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1990, 11, 11, 11, 11, 11, 11, 0));


        // do the test... dispatch vistor to both reports
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        testXmlReport.accept(testee);
        testXmlReport1.accept(testee);


        CodeQuality calculatedCodeQuality = testee.produceResult();
        AssertionsForInterfaceTypes.assertThat(calculatedCodeQuality.getMetrics())
                .extracting("name", "formattedValue", "value", "status")
                .contains(
                        tuple("test_success_density", "11", 11, CodeQualityMetricStatus.Ok),
                        tuple("test_failures", "1", 1, CodeQualityMetricStatus.Warning),
                        tuple("test_errors", "2", 2, CodeQualityMetricStatus.Alert),
                        tuple("tests", "14", 14, CodeQualityMetricStatus.Ok));

        // it should have got the maximum time available
        AssertionsForClassTypes.assertThat(calculatedCodeQuality.getTimestamp()).isEqualTo(DatatypeFactory.newInstance().newXMLGregorianCalendar(1990, 11, 11, 11, 11, 11, 11, 0).toGregorianCalendar().getTimeInMillis());
        AssertionsForInterfaceTypes.assertThat(calculatedCodeQuality.getType()).isEqualTo(CodeQualityType.StaticAnalysis);

    }

    @Test
    public void handlesFindbugsFiles() throws Exception {

        //set up 2 files each with 2 bugs of different priorties
        FindBubsXmlReport findBubsXmlReport = produceFindbugsReport();


        // do the test... dispatch vistor to both reports
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        findBubsXmlReport.accept(testee);

        CodeQuality calculatedCodeQuality = testee.produceResult();
        AssertionsForInterfaceTypes.assertThat(calculatedCodeQuality.getMetrics())
                .extracting("name", "formattedValue", "value", "status")
                .contains(
                        tuple("blocker_violations", "2", 2, CodeQualityMetricStatus.Alert),
                        tuple("critical_violations", "1", 1, CodeQualityMetricStatus.Alert),
                        tuple("major_violations", "1", 1, CodeQualityMetricStatus.Warning),
                        tuple("violations", "1", 1, CodeQualityMetricStatus.Warning));

    }

    private FindBubsXmlReport produceFindbugsReport() {
        FindBubsXmlReport findBubsXmlReport = new FindBubsXmlReport();
        List<FindBubsXmlReport.BugFile> files = new ArrayList<>();
        FindBubsXmlReport.BugFile bugFile = new FindBubsXmlReport.BugFile();
        List<FindBubsXmlReport.BugInstance> bugCollection = new ArrayList<FindBubsXmlReport.BugInstance>();

        FindBubsXmlReport.BugInstance bugInstance = createBugInstance(FindBubsXmlReport.BugPriority.Normal);
        FindBubsXmlReport.BugInstance bugInstance2 = createBugInstance(FindBubsXmlReport.BugPriority.Blocker);
        bugCollection.add(bugInstance);
        bugCollection.add(bugInstance2);
        bugFile.setBugCollection(bugCollection);
        files.add(bugFile);

        FindBubsXmlReport.BugFile bugFile2 = new FindBubsXmlReport.BugFile();
        List<FindBubsXmlReport.BugInstance> bugCollection2 = new ArrayList<FindBubsXmlReport.BugInstance>();

        FindBubsXmlReport.BugInstance bugInstance3 = createBugInstance(FindBubsXmlReport.BugPriority.Low);
        FindBubsXmlReport.BugInstance bugInstance4 = createBugInstance(FindBubsXmlReport.BugPriority.Blocker);
        FindBubsXmlReport.BugInstance bugInstance5 = createBugInstance(FindBubsXmlReport.BugPriority.Critical);
        bugCollection2.add(bugInstance3);
        bugCollection2.add(bugInstance4);
        bugCollection2.add(bugInstance5);
        bugFile2.setBugCollection(bugCollection2);
        files.add(bugFile2);

        findBubsXmlReport.setFiles(files);
        return findBubsXmlReport;
    }

    @Test
    public void findBugsReportAllOkayIfNoViolations() {
        FindBubsXmlReport findBubsXmlReport = new FindBubsXmlReport();

        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        findBubsXmlReport.accept(testee);

        CodeQuality calculatedCodeQuality = testee.produceResult();
        AssertionsForInterfaceTypes.assertThat(calculatedCodeQuality.getMetrics())
                .extracting("name", "formattedValue", "value", "status")
                .contains(
                        tuple("blocker_violations", "0", 0, CodeQualityMetricStatus.Ok),
                        tuple("critical_violations", "0", 0, CodeQualityMetricStatus.Ok),
                        tuple("major_violations", "0", 0, CodeQualityMetricStatus.Ok),
                        tuple("violations", "0", 0, CodeQualityMetricStatus.Ok));
    }

    @Test
    public void sumsFindbugsOverMultipleFiles() {
        //set up 2 files each with 2 bugs of different priorties
        FindBubsXmlReport findBubsXmlReport = produceFindbugsReport();
        FindBubsXmlReport findBubsXmlReport2 = produceFindbugsReport();


        // do the test... dispatch vistor to both reports
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        findBubsXmlReport.accept(testee);
        findBubsXmlReport2.accept(testee);

        CodeQuality calculatedCodeQuality = testee.produceResult();

        AssertionsForInterfaceTypes.assertThat(calculatedCodeQuality.getMetrics())
                .extracting("name", "formattedValue", "value", "status")
                .contains(
                        tuple("blocker_violations", "4", 4, CodeQualityMetricStatus.Alert),
                        tuple("critical_violations", "2", 2, CodeQualityMetricStatus.Alert),
                        tuple("major_violations", "2", 2, CodeQualityMetricStatus.Warning),
                        tuple("violations", "2", 2, CodeQualityMetricStatus.Warning));
    }

    private FindBubsXmlReport.BugInstance createBugInstance(FindBubsXmlReport.BugPriority priority) {
        FindBubsXmlReport.BugInstance bugInstance = new FindBubsXmlReport.BugInstance();
        bugInstance.setCategory(FindBubsXmlReport.BugCategory.BAD_PRACTICE);
        bugInstance.setLineNumber(12);
        bugInstance.setMessage("this is slow");
        bugInstance.setPriority(priority);
        bugInstance.setType("somthing");
        return bugInstance;
    }
}