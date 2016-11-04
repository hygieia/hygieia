package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        fakeQuality.setTimestamp(10L);
        when(mockVisitor.produceResult()).thenReturn(fakeQuality);


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
        FindBubsXmlReport bugsXmlReport = new FindBubsXmlReport();
        reportList.add(bugsXmlReport);

        CodeQualityVisitor mockVistor = mock(CodeQualityVisitor.class);
        when(mockCodeQualityConverter.produceVisitor()).thenReturn(mockVistor);

        CodeQuality fakeReturn = new CodeQuality();
        when(mockVistor.produceResult()).thenReturn(fakeReturn);

        this.testee.storeJob("job1", dbJob, reportList);

        verify(mockVistor).visit(same(testXmlReport));
        verify(mockVistor).visit(same(testXmlReport));

        verify(mockCodeQualityRepository).save(fakeReturn);
    }
}