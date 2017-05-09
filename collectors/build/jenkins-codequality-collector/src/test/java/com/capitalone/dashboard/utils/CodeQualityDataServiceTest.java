package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.jenkins.JenkinsBuild;
import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.model.JenkinsCodeQualityJob;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.quality.CodeQualityVisitee;
import com.capitalone.dashboard.model.quality.CodeQualityVisitor;
import com.capitalone.dashboard.model.quality.FindBugsXmlReport;
import com.capitalone.dashboard.model.quality.JunitXmlReport;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

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
    public void doesNotRecordJobIfLastStoredJobTimestampIsSameAsCurrent() throws Exception {

        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        JunitXmlReport testXmlReport = new JunitXmlReport();
        List<JunitXmlReport> testXmlReports = Arrays.asList(testXmlReport);

        CodeQuality storedCodeQuality = mock(CodeQuality.class);

        when(mockCodeQualityRepository.findByCollectorItemIdAndTimestamp(same(dbJobId), eq(10L))).thenReturn(
                storedCodeQuality
        );

        CodeQualityVisitor mockVisitor = mock(CodeQualityVisitor.class);
        when(mockCodeQualityConverter.produceVisitor()).thenReturn(mockVisitor);
        CodeQuality fakeQuality = new CodeQuality();
        when(mockVisitor.produceResult()).thenReturn(fakeQuality);

        JenkinsJob job = JenkinsJob.newBuilder().jobName("job1").lastSuccessfulBuild(
                JenkinsBuild.newBuilder().timestamp(10L).build()).build();

        //test
        this.testee.storeJob(job, dbJob, testXmlReports);

        //asserts
        verify(mockCodeQualityRepository, times(0)).save(any(CodeQuality.class));
    }

    @Test
    public void doesNothingIfNoJob() {
        JenkinsJob job = JenkinsJob.newBuilder().jobName("job1").build();
        this.testee.storeJob(job, null, null);

        verifyNoMoreInteractions(mockCodeQualityRepository);
    }

    @Test
    public void copesWithNoXMLReports() {
        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        JenkinsJob job = JenkinsJob.newBuilder().jobName("job1").build();
        this.testee.storeJob(job, dbJob, null);

        verifyNoMoreInteractions(mockCodeQualityRepository);

    }

    @Test
    public void copesWithEmptyListOfXmlReports() {
        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        JenkinsJob job = JenkinsJob.newBuilder().jobName("job1").build();
        this.testee.storeJob(job, dbJob, new ArrayList<>());

        verifyNoMoreInteractions(mockCodeQualityRepository);
    }

    @Test
    public void handlesReportsWithJunitAndFindBugs() {
        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        List<CodeQualityVisitee> reportList = new ArrayList<>();
        // give it a junit report
        JunitXmlReport testXmlReport = new JunitXmlReport();
        testXmlReport.setErrors(10);
        testXmlReport.setFailures(1);
        testXmlReport.setTests(25);
        reportList.add(testXmlReport);
        // and a findbugs report
        FindBugsXmlReport bugsXmlReport = new FindBugsXmlReport();
        reportList.add(bugsXmlReport);

        CodeQualityVisitor mockVistor = mock(CodeQualityVisitor.class);
        when(mockCodeQualityConverter.produceVisitor()).thenReturn(mockVistor);

        CodeQuality fakeReturn = new CodeQuality();
        when(mockVistor.produceResult()).thenReturn(fakeReturn);

        JenkinsJob job = JenkinsJob.newBuilder().jobName("job1").lastSuccessfulBuild(JenkinsBuild.newBuilder().timestamp(14000).build()).build();
        this.testee.storeJob(job, dbJob, reportList);

        verify(mockVistor).visit(same(testXmlReport));
        verify(mockVistor).visit(same(testXmlReport));

        ArgumentCaptor<CodeQuality> captor = ArgumentCaptor.forClass(CodeQuality.class);
        verify(mockCodeQualityRepository).save(captor.capture());
        CodeQuality capturedValue = captor.getValue();
        assertThat(capturedValue.getName()).isEqualTo("job1");
        assertThat(capturedValue.getTimestamp()).isEqualTo(14000);
        assertThat(capturedValue.getType()).isEqualTo(CodeQualityType.StaticAnalysis);
        assertThat(capturedValue.getUrl()).isEqualTo("http://buildserver2/job1");
        assertThat(capturedValue.getCollectorItemId()).isSameAs(dbJobId);
    }

}