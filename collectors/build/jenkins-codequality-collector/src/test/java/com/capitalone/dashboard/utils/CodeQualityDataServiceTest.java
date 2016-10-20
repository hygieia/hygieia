package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.groups.Tuple;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Created by plv163 on 19/10/2016.
 */
public class CodeQualityDataServiceTest {

    private CodeQualityRepository mockCodeQualityRepository;
    private CodeQualityConverter mockCodeQualityConverter;
    private CodeQualityDataService testee;

    @Before
    public void setup() {
        this.mockCodeQualityRepository = mock(CodeQualityRepository.class);
        this.mockCodeQualityConverter = mock(CodeQualityConverter.class);
        this.testee = new CodeQualityDataService(mockCodeQualityRepository, mockCodeQualityConverter);
    }


    @Test
    public void storesJobWithCorrectTimestamp() throws Exception {

        JunitXmlReport testXmlReport = new JunitXmlReport();
        testXmlReport.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1980, 10, 10, 10, 10, 10, 10, 0));
        JunitXmlReport testXmlReport1 = new JunitXmlReport();
        testXmlReport1.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1990, 11, 11, 11, 11, 11, 11, 0));
        List<JunitXmlReport> testXmlReports = Arrays.asList(testXmlReport, testXmlReport1);

        // report 1 asked for return this set
        Set<CodeQualityMetric> codeMetrics = getCodeQualityMetrics(Arrays.asList(
                tuple("test_success_density", "5", 5, CodeQualityMetricStatus.Ok, null),
                tuple("test_failures", "1", 1, CodeQualityMetricStatus.Alert, "broken"),
                tuple("test_errors", "2", 2, CodeQualityMetricStatus.Alert, ""),
                tuple("tests", "8", 8, CodeQualityMetricStatus.Warning, "potentially broken")));
        when(mockCodeQualityConverter.analyse(same(testXmlReport))).thenReturn(codeMetrics);

        // report 2 asked for return this set
        Set<CodeQualityMetric> codeMetrics1 = getCodeQualityMetrics(Arrays.asList(
                tuple("test_success_density", "5", 5, CodeQualityMetricStatus.Ok, "message"),
                tuple("test_failures", "0", 0, CodeQualityMetricStatus.Ok, null),
                tuple("test_errors", "1", 1, CodeQualityMetricStatus.Ok, "message"),
                tuple("tests", "6", 6, CodeQualityMetricStatus.Ok, "messageAgain")));
        when(mockCodeQualityConverter.analyse(same(testXmlReport1))).thenReturn(codeMetrics1);

        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        //test
        this.testee.storeJob("job1", dbJob, testXmlReports);

        verify(mockCodeQualityConverter).analyse(same(testXmlReport));
        ArgumentCaptor<CodeQuality> argumentCaptor = ArgumentCaptor.forClass(CodeQuality.class);
        verify(mockCodeQualityRepository).save(argumentCaptor.capture());
        CodeQuality capturedCodeQuality = argumentCaptor.getValue();
        assertThat(capturedCodeQuality.getMetrics())
                .extracting("name", "formattedValue", "value", "status", "statusMessage")
                .contains(
                        tuple("test_success_density", "10", 10, CodeQualityMetricStatus.Ok, "message"),
                        tuple("test_failures", "1", 1, CodeQualityMetricStatus.Alert, "broken"),
                        tuple("test_errors", "3", 3, CodeQualityMetricStatus.Alert, "message"),
                        tuple("tests", "14", 14, CodeQualityMetricStatus.Warning, "potentially broken,messageAgain"));
        // the collector name needs adding
        assertThat(capturedCodeQuality.getCollectorItemId()).isSameAs(dbJobId);
        AssertionsForClassTypes.assertThat(capturedCodeQuality.getName()).isEqualTo("job1");
        // it should have got the maximum time available
        AssertionsForClassTypes.assertThat(capturedCodeQuality.getTimestamp()).isEqualTo(DatatypeFactory.newInstance().newXMLGregorianCalendar(1990, 11, 11, 11, 11, 11, 11, 0).toGregorianCalendar().getTimeInMillis());
        assertThat(capturedCodeQuality.getType()).isEqualTo(CodeQualityType.StaticAnalysis);
        AssertionsForClassTypes.assertThat(capturedCodeQuality.getUrl()).isEqualTo("http://buildserver2/job1");

    }

    private Set<CodeQualityMetric> getCodeQualityMetrics(List<Tuple> tuples) {
        Set<CodeQualityMetric> codeMetrics = new HashSet<>();
        for (Tuple tuple : tuples) {
            CodeQualityMetric codeQualityMetric = new CodeQualityMetric();
            Object[] objects = tuple.toArray();
            codeQualityMetric.setName((String) objects[0]);
            codeQualityMetric.setFormattedValue((String) objects[1]);
            codeQualityMetric.setValue(objects[2]);
            codeQualityMetric.setStatus((CodeQualityMetricStatus) objects[3]);
            codeQualityMetric.setStatusMessage((String) objects[4]);
            codeMetrics.add(codeQualityMetric);
        }

        return codeMetrics;
    }

    @Test
    public void doesNotRecordJobIfLastStoredJobTimestampIsSameAsCurrent() throws Exception {

        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        JunitXmlReport testXmlReport = new JunitXmlReport();
        testXmlReport.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1980, 10, 10, 10, 10, 10, 10, 0));
        JunitXmlReport testXmlReport1 = new JunitXmlReport();
        final XMLGregorianCalendar oldestTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(1990, 11, 11, 11, 11, 11, 11, 0);
        testXmlReport1.setTimestamp(oldestTime);
        List<JunitXmlReport> testXmlReports = Arrays.asList(testXmlReport, testXmlReport1);

        CodeQuality storedCodeQuality = mock(CodeQuality.class);

        when(mockCodeQualityRepository.findByCollectorItemIdAndTimestamp(same(dbJobId), eq(oldestTime.toGregorianCalendar().getTimeInMillis()))).thenReturn(
                storedCodeQuality
        );


        //test
        this.testee.storeJob("job1", dbJob, testXmlReports);

        //asserts
        verify(mockCodeQualityRepository, times(0)).save(any(CodeQuality.class));
    }

    @Test
    public void doesNothingIfNoJob() {

        this.testee.storeJob("job1", null, null);

        verifyNoMoreInteractions(mockCodeQualityRepository);
    }

    @Test
    public void copesWithNoXMLReports() {
        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        this.testee.storeJob("job1", dbJob, null);

        verifyNoMoreInteractions(mockCodeQualityRepository);

    }

    @Test
    public void copesWithEmptyListOfXmlReports() {
        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        this.testee.storeJob("job1", dbJob, new ArrayList<>());

        verifyNoMoreInteractions(mockCodeQualityRepository);
    }
}