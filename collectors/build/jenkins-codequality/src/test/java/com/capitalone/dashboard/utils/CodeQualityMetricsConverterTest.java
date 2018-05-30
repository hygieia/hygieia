package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.quality.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.assertj.core.data.Percentage;
import org.junit.Test;

import javax.xml.datatype.DatatypeFactory;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.assertj.core.api.Assertions.tuple;

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
    public void ignoresEmptyJunitReportFile() {
        JunitXmlReport testXmlReport = new JunitXmlReport();

        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        testXmlReport.accept(testee);
        CodeQuality calculatedCodeQuality = testee.produceResult();

        AssertionsForInterfaceTypes.assertThat(calculatedCodeQuality.getMetrics())
                .extracting("name", "formattedValue", "value", "status")
                .contains(
                        tuple("test_success_density", "0", 0, CodeQualityMetricStatus.Ok),
                        tuple("test_failures", "0", 0, CodeQualityMetricStatus.Ok),
                        tuple("test_errors", "0", 0, CodeQualityMetricStatus.Ok),
                        tuple("tests", "0", 0, CodeQualityMetricStatus.Ok));
    }

    @Test
    public void handlesFindbugsFiles() throws Exception {

        //set up 2 files each with 2 bugs of different priorties
        FindBugsXmlReport findBugsXmlReport = produceFindbugsReport();


        // do the test... dispatch vistor to both reports
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        findBugsXmlReport.accept(testee);

        CodeQuality calculatedCodeQuality = testee.produceResult();
        AssertionsForInterfaceTypes.assertThat(calculatedCodeQuality.getMetrics())
                .extracting("name", "formattedValue", "value", "status")
                .contains(
                        tuple("blocker_violations", "2", 2, CodeQualityMetricStatus.Alert),
                        tuple("critical_violations", "1", 1, CodeQualityMetricStatus.Alert),
                        tuple("major_violations", "1", 1, CodeQualityMetricStatus.Warning),
                        tuple("violations", "1", 1, CodeQualityMetricStatus.Warning));

    }

    private FindBugsXmlReport produceFindbugsReport() {
        FindBugsXmlReport findBugsXmlReport = new FindBugsXmlReport();
        List<FindBugsXmlReport.BugFile> files = new ArrayList<>();
        FindBugsXmlReport.BugFile bugFile = new FindBugsXmlReport.BugFile();
        List<FindBugsXmlReport.BugInstance> bugCollection = new ArrayList<FindBugsXmlReport.BugInstance>();

        FindBugsXmlReport.BugInstance bugInstance = createBugInstance(FindBugsXmlReport.BugPriority.Normal);
        FindBugsXmlReport.BugInstance bugInstance2 = createBugInstance(FindBugsXmlReport.BugPriority.Blocker);
        bugCollection.add(bugInstance);
        bugCollection.add(bugInstance2);
        bugFile.setBugCollection(bugCollection);
        files.add(bugFile);

        FindBugsXmlReport.BugFile bugFile2 = new FindBugsXmlReport.BugFile();
        List<FindBugsXmlReport.BugInstance> bugCollection2 = new ArrayList<FindBugsXmlReport.BugInstance>();

        FindBugsXmlReport.BugInstance bugInstance3 = createBugInstance(FindBugsXmlReport.BugPriority.Low);
        FindBugsXmlReport.BugInstance bugInstance4 = createBugInstance(FindBugsXmlReport.BugPriority.Blocker);
        FindBugsXmlReport.BugInstance bugInstance5 = createBugInstance(FindBugsXmlReport.BugPriority.Critical);
        bugCollection2.add(bugInstance3);
        bugCollection2.add(bugInstance4);
        bugCollection2.add(bugInstance5);
        bugFile2.setBugCollection(bugCollection2);
        files.add(bugFile2);

        findBugsXmlReport.setFiles(files);
        return findBugsXmlReport;
    }

    @Test
    public void findBugsReportAllOkayIfNoViolations() {
        FindBugsXmlReport findBugsXmlReport = new FindBugsXmlReport();

        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        findBugsXmlReport.accept(testee);

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
        FindBugsXmlReport findBugsXmlReport = produceFindbugsReport();
        FindBugsXmlReport findBugsXmlReport2 = produceFindbugsReport();


        // do the test... dispatch vistor to both reports
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        findBugsXmlReport.accept(testee);
        findBugsXmlReport2.accept(testee);

        CodeQuality calculatedCodeQuality = testee.produceResult();

        AssertionsForInterfaceTypes.assertThat(calculatedCodeQuality.getMetrics())
                .extracting("name", "formattedValue", "value", "status")
                .contains(
                        tuple("blocker_violations", "4", 4, CodeQualityMetricStatus.Alert),
                        tuple("critical_violations", "2", 2, CodeQualityMetricStatus.Alert),
                        tuple("major_violations", "2", 2, CodeQualityMetricStatus.Warning),
                        tuple("violations", "2", 2, CodeQualityMetricStatus.Warning));
    }

    private FindBugsXmlReport.BugInstance createBugInstance(FindBugsXmlReport.BugPriority priority) {
        FindBugsXmlReport.BugInstance bugInstance = new FindBugsXmlReport.BugInstance();
        bugInstance.setCategory(FindBugsXmlReport.BugCategory.BAD_PRACTICE);
        bugInstance.setLineNumber(12);
        bugInstance.setMessage("this is slow");
        bugInstance.setPriority(priority);
        bugInstance.setType("somthing");
        return bugInstance;
    }

    @Test
    public void coverageStatsFromJacocoXml() {
        JacocoXmlReport jacocoXmlReport = produceJacocoXmlReport();

        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        jacocoXmlReport.accept(testee);

        CodeQuality calculatedCodeQuality = testee.produceResult();

        assertThat(calculatedCodeQuality.getMetrics()).extracting("name","formattedValue","value","status")
                .contains(
                        tuple("total_lines_covered","15",15,CodeQualityMetricStatus.Ok),
                        tuple("total_lines_missed","6",6,CodeQualityMetricStatus.Ok),
                        tuple("total_instructions_covered","10",10,CodeQualityMetricStatus.Ok),
                        tuple("total_instructions_missed","1",1,CodeQualityMetricStatus.Ok));

        Optional<CodeQualityMetric> coverage = calculatedCodeQuality.getMetrics().stream().filter(codeQualityMetric -> codeQualityMetric.getName().equals("coverage")).findFirst();
        assertThat(coverage.get().getFormattedValue()).isEqualTo("90.909");
        assertThat(coverage.get().getStatus()).isEqualTo(CodeQualityMetricStatus.Ok);
        assertThat(((Double)coverage.get().getValue()).doubleValue()).isCloseTo(90.0909, Percentage.withPercentage(1));
        Optional<CodeQualityMetric> lineCoverage = calculatedCodeQuality.getMetrics().stream().filter(codeQualityMetric -> codeQualityMetric.getName().equals("line_coverage")).findFirst();
        assertThat(lineCoverage.get().getFormattedValue()).isEqualTo("71.429");
        assertThat(lineCoverage.get().getStatus()).isEqualTo(CodeQualityMetricStatus.Ok);
        assertThat(((Double)lineCoverage.get().getValue()).doubleValue()).isCloseTo(71.429, Percentage.withPercentage(1));

    }

    @Test
    public void sumsCoverageStatsFromJacocoXml() {
        JacocoXmlReport jacocoXmlReport1 = produceJacocoXmlReport();
        JacocoXmlReport jacocoXmlReport2 = produceJacocoXmlReport();

        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        jacocoXmlReport1.accept(testee);
        jacocoXmlReport2.accept(testee);

        CodeQuality calculatedCodeQuality = testee.produceResult();

        assertThat(calculatedCodeQuality.getMetrics()).extracting("name","formattedValue","value","status")
                .contains(
                        tuple("total_lines_covered","30",30,CodeQualityMetricStatus.Ok),
                        tuple("total_lines_missed","12",12,CodeQualityMetricStatus.Ok),
                        tuple("total_instructions_covered","20",20,CodeQualityMetricStatus.Ok),
                        tuple("total_instructions_missed","2",2,CodeQualityMetricStatus.Ok));

        Optional<CodeQualityMetric> coverage = calculatedCodeQuality.getMetrics().stream().filter(codeQualityMetric -> codeQualityMetric.getName().equals("coverage")).findFirst();
        assertThat(coverage.get().getFormattedValue()).isEqualTo("90.909");
        assertThat(coverage.get().getStatus()).isEqualTo(CodeQualityMetricStatus.Ok);
        assertThat(((Double)coverage.get().getValue()).doubleValue()).isCloseTo(90.0909, Percentage.withPercentage(1));
        Optional<CodeQualityMetric> lineCoverage = calculatedCodeQuality.getMetrics().stream().filter(codeQualityMetric -> codeQualityMetric.getName().equals("line_coverage")).findFirst();
        assertThat(lineCoverage.get().getFormattedValue()).isEqualTo("71.429");
        assertThat(lineCoverage.get().getStatus()).isEqualTo(CodeQualityMetricStatus.Ok);
        assertThat(((Double)lineCoverage.get().getValue()).doubleValue()).isCloseTo(71.429, Percentage.withPercentage(1));

    }

    private JacocoXmlReport produceJacocoXmlReport() {
        JacocoXmlReport report = new JacocoXmlReport();
        JacocoXmlReport.Counter instructions = new JacocoXmlReport.Counter();
        instructions.setType(JacocoXmlReport.CounterType.INSTRUCTION);
        instructions.setCovered(10);
        instructions.setMissed(1);

        JacocoXmlReport.Counter method = new JacocoXmlReport.Counter();
        method.setType(JacocoXmlReport.CounterType.METHOD);
        method.setCovered(11);
        method.setMissed(2);

        JacocoXmlReport.Counter complexity = new JacocoXmlReport.Counter();
        complexity.setType(JacocoXmlReport.CounterType.COMPLEXITY);
        complexity.setCovered(12);
        complexity.setMissed(3);

        JacocoXmlReport.Counter clazz = new JacocoXmlReport.Counter();
        clazz.setType(JacocoXmlReport.CounterType.CLASS);
        clazz.setCovered(13);
        clazz.setMissed(4);

        JacocoXmlReport.Counter branch = new JacocoXmlReport.Counter();
        branch.setType(JacocoXmlReport.CounterType.BRANCH);
        branch.setCovered(14);
        branch.setMissed(5);

        JacocoXmlReport.Counter line = new JacocoXmlReport.Counter();
        line.setType(JacocoXmlReport.CounterType.LINE);
        line.setCovered(15);
        line.setMissed(6);

        List<JacocoXmlReport.Counter> counters = Arrays.asList(instructions,method,complexity,clazz,branch,line);
        report.setCounters(counters);
        return report;
    }

    @Test
    public void pmdReport(){
        PmdReport report = this.producePmdReport();

        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        report.accept(testee);

        CodeQuality codeQualityMetrics = testee.produceResult();

        assertThat(codeQualityMetrics.getMetrics()).extracting("name","formattedValue","value","status")
                .contains(
                        tuple("blocker_violations","10",10,CodeQualityMetricStatus.Alert),
                        tuple("critical_violations","12",12,CodeQualityMetricStatus.Alert),
                        tuple("major_violations","2",2,CodeQualityMetricStatus.Warning),
                        tuple("violations","2",2,CodeQualityMetricStatus.Warning));
    }

    @Test
    public void sumsMultiplePmdReports() {
        PmdReport report1 = this.producePmdReport();
        PmdReport report2 = this.producePmdReport();

        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        report1.accept(testee);
        report2.accept(testee);

        CodeQuality codeQualityMetrics = testee.produceResult();

        assertThat(codeQualityMetrics.getMetrics()).extracting("name","formattedValue","value","status")
                .contains(
                        tuple("blocker_violations","20",20,CodeQualityMetricStatus.Alert),
                        tuple("critical_violations","24",24,CodeQualityMetricStatus.Alert),
                        tuple("major_violations","4",4,CodeQualityMetricStatus.Warning),
                        tuple("violations","4",4,CodeQualityMetricStatus.Warning));
    }

    private PmdReport producePmdReport(){
        PmdReport report = new PmdReport();
        PmdReport.PmdFile file = new PmdReport.PmdFile();
        List<PmdReport.PmdViolation> violations = new ArrayList<>();
        for (int i=0;i<10;i++) {
            PmdReport.PmdViolation violation = new PmdReport.PmdViolation();
            violation.setPriority(1);
            violations.add(violation);
        }
        for (int i=0;i<12;i++) {
            PmdReport.PmdViolation violation = new PmdReport.PmdViolation();
            violation.setPriority(2);
            violations.add(violation);
        }
        for (int i=0;i<2;i++) {
            PmdReport.PmdViolation violation = new PmdReport.PmdViolation();
            violation.setPriority(3);
            violations.add(violation);
        }
        PmdReport.PmdViolation violation4 = new PmdReport.PmdViolation();
        violation4.setPriority(4);
        violations.add(violation4);
        PmdReport.PmdViolation violation5 = new PmdReport.PmdViolation();
        violation5.setPriority(5);
        violations.add(violation5);
        file.setViolations(violations);
        report.setFiles(Arrays.asList(file));
        return report;
    }

    @Test
    public void checkStyleReports() {
        CheckstyleReport report = produceCheckStyleReport();
        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        report.accept(testee);

        CodeQuality codeQualityMetrics = testee.produceResult();

        assertThat(codeQualityMetrics.getMetrics()).extracting("name","formattedValue","value","status")
                .contains(
                        tuple("blocker_violations","9",9,CodeQualityMetricStatus.Alert),
                        tuple("critical_violations","11",11,CodeQualityMetricStatus.Alert),
                        tuple("major_violations","2",2,CodeQualityMetricStatus.Warning),
                        tuple("violations","1",1,CodeQualityMetricStatus.Warning));
    }

    @Test
    public void checkstyleHandlesNoErrors() {
        CheckstyleReport report = new CheckstyleReport();
        CheckstyleReport.CheckstyleFile file = new CheckstyleReport.CheckstyleFile();
        report.setFiles(Arrays.asList(file));

        CodeQualityMetricsConverter testee = new CodeQualityMetricsConverter();
        report.accept(testee);

        CodeQuality codeQualityMetrics = testee.produceResult();

        assertThat(codeQualityMetrics.getMetrics()).extracting("name","formattedValue","value","status")
            .contains(
                tuple("blocker_violations","0",0,CodeQualityMetricStatus.Ok),
                tuple("critical_violations","0",0,CodeQualityMetricStatus.Ok),
                tuple("major_violations","0",0,CodeQualityMetricStatus.Ok),
                tuple("violations","0",0,CodeQualityMetricStatus.Ok));

    }

    private CheckstyleReport produceCheckStyleReport(){
        CheckstyleReport report = new CheckstyleReport();
        CheckstyleReport.CheckstyleFile file = new CheckstyleReport.CheckstyleFile();
        List<CheckstyleReport.CheckstyleError> errors = new ArrayList<>();
        for (int i=0;i<9;i++) {
            CheckstyleReport.CheckstyleError violation = new CheckstyleReport.CheckstyleError();
            violation.setSeverity(CheckstyleReport.CheckstyleSeverity.error);
            errors.add(violation);
        }
        for (int i=0;i<11;i++) {
            CheckstyleReport.CheckstyleError violation = new CheckstyleReport.CheckstyleError();
            violation.setSeverity(CheckstyleReport.CheckstyleSeverity.warning);
            errors.add(violation);
        }
        for (int i=0;i<2;i++) {
            CheckstyleReport.CheckstyleError violation = new CheckstyleReport.CheckstyleError();
            violation.setSeverity(CheckstyleReport.CheckstyleSeverity.info);
            errors.add(violation);
        }
        CheckstyleReport.CheckstyleError violation4 = new CheckstyleReport.CheckstyleError();
        violation4.setSeverity(CheckstyleReport.CheckstyleSeverity.ignore);
        errors.add(violation4);
        file.setErrors(errors);
        report.setFiles(Arrays.asList(file));
        return report;
    }

}