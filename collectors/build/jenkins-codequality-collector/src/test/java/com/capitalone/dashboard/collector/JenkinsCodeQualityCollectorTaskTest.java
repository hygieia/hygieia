package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.Artifact;
import com.capitalone.dashboard.jenkins.JenkinsBuild;
import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.JenkinsSettings;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityCollectorRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityJobRepository;
import com.capitalone.dashboard.utils.CodeQualityConverter;
import org.assertj.core.groups.Tuple;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;


/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsCodeQualityCollectorTaskTest {

    private JenkinsCodeQualityCollectorTask testee;

    private TaskScheduler mockScheduler;
    private JenkinsCodeQualityCollectorRepository mockRepo;
    private JenkinsClient mockJenkinsHelper;
    private CodeQualityConverter mockCodeQualityConverter;
    private CodeQualityRepository mockCodeQualityRepository;
    private JenkinsCodeQualityJobRepository mockJobRepository;

    @Before
    public void setup() {
        mockScheduler = mock(TaskScheduler.class);
        mockRepo = mock(JenkinsCodeQualityCollectorRepository.class);
        mockJenkinsHelper = mock(JenkinsClient.class);
        mockCodeQualityConverter = mock(CodeQualityConverter.class);
        mockCodeQualityRepository = mock(CodeQualityRepository.class);
        mockJobRepository = mock(JenkinsCodeQualityJobRepository.class);
        JenkinsSettings settings = new JenkinsSettings();
        settings.setCron("0 * * * * *");
        settings.setServers(Arrays.asList("server1", "server2"));
        this.testee = new JenkinsCodeQualityCollectorTask(mockScheduler, mockRepo, mockJobRepository, settings, mockJenkinsHelper, mockCodeQualityConverter, mockCodeQualityRepository);
    }

    @Test
    public void getCollectorReturnsAJenkinsCodeQualityCollector() {

        //test
        final JenkinsCodeQualityCollector collector = testee.getCollector();
        // assert
        assertThat(collector).isNotNull().isInstanceOf(JenkinsCodeQualityCollector.class);
        assertThat(collector.isEnabled()).isTrue();
        assertThat(collector.isOnline()).isTrue();
        assertThat(collector.getBuildServers()).contains("server1", "server2");
        assertThat(collector.getCollectorType()).isEqualTo(CollectorType.CodeQuality);
        assertThat(collector.getName()).isEqualTo("JenkinsCodeQuality");
    }

    @Test
    public void getCollectorRepositoryReturnsTheRepository() {
        assertThat(testee.getCollectorRepository()).isNotNull().isSameAs(mockRepo);
    }

    @Test
    public void onStartupTheCronJobIsScheduledAndStartStausUpdated() {
        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
        when(mockRepo.findByName(eq("JenkinsCodeQuality"))).thenReturn(mockCollector);

        testee.onStartup();

        verify(mockRepo).findByName(eq("JenkinsCodeQuality"));
        ArgumentCaptor<Trigger> capturedTrigger = ArgumentCaptor.forClass(Trigger.class);
        verify(mockScheduler).schedule(same(testee), capturedTrigger.capture());
        // TODO assert the cron trigger!

        verify(mockRepo).save(same(mockCollector));
        verify(mockCollector).setOnline(eq(true));
    }

    @Test
    public void collectCausesACollectionOfResultsFromConfiguredJobs() throws Exception {

        // expect the collector to deliver its config
        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
        List<String> buildServers = new ArrayList<>();
        buildServers.add("http://buildserver");
        buildServers.add("http://buildserver2");
        when(mockCollector.getBuildServers()).thenAnswer(invocationOnMock -> buildServers);

        // this returns 3 jobs split over the 2 build servers
        List<JenkinsJob> allJenkinsJobs = getJenkinsJobs();
        when(mockJenkinsHelper.getJobs(argThat(hasItems("http://buildserver", "http://buildserver")))).thenReturn(allJenkinsJobs);

        // return a list of jobs when asked
        JunitXmlReport testXmlReport = new JunitXmlReport();
        JunitXmlReport testXmlReport1 = new JunitXmlReport();
        List<JunitXmlReport> testXmlReports = Arrays.asList(testXmlReport, testXmlReport1);
        when(mockJenkinsHelper.getLatestArtifacts(eq(JunitXmlReport.class), any(JenkinsJob.class), anyList())).thenReturn(testXmlReports);

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

        ObjectId collectorId = new ObjectId();
        when(mockCollector.getId()).thenReturn(collectorId);

        // test
        testee.collect(mockCollector);

        // verify
        ArgumentCaptor<JenkinsJob> jobCapture = ArgumentCaptor.forClass(JenkinsJob.class);
        ArgumentCaptor<List> patternListCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockJenkinsHelper).getLatestArtifacts(eq(JunitXmlReport.class), jobCapture.capture(), patternListCaptor.capture());
        JenkinsJob capturedJob = jobCapture.getValue();
        assertThat(capturedJob).hasFieldOrPropertyWithValue("name", "job1").hasFieldOrPropertyWithValue("url", "http://buildserver2/job1");
        List<Pattern> capturedPatterns = patternListCaptor.getValue();
        assertThat(capturedPatterns).hasSize(1).allMatch(
                pattern -> pattern.pattern().equals(".*\\.xml"));

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
        assertThat(capturedCodeQuality.getCollectorItemId()).isSameAs(collectorId);
        assertThat(capturedCodeQuality.getName()).isEqualTo("job1");
        // TODO timestamp need sto be found from the test results and added here.
        assertThat(capturedCodeQuality.getType()).isEqualTo(CodeQualityType.StaticAnalysis);
        assertThat(capturedCodeQuality.getUrl()).isEqualTo("http://buildserver2/job1");
    }

    @Test
    public void cleansUpJobsInDatabaseButNotInCollector() {
        // expect
        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
        List<String> buildServers = new ArrayList<>();
        buildServers.add("http://buildserver1");
        buildServers.add("http://buildserver2");
        when(mockCollector.getBuildServers()).thenAnswer(invocationOnMock -> buildServers);

        ObjectId collectorId = new ObjectId();
        when(mockCollector.getId()).thenReturn(collectorId);

        when(this.mockJenkinsHelper.getJobs(same(buildServers))).thenReturn(Arrays.asList(
                JenkinsJob.newBuilder().url("http://buildserver1").jobName("job1").build(),
                JenkinsJob.newBuilder().url("http://buildserver2").jobName("job1").build()
        ));

        List<JenkinsCodeQualityJob> allStoredJenkinsJobs = new ArrayList<>();
        allStoredJenkinsJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://buildserver0").build());
        allStoredJenkinsJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://buildserver1").build());
        allStoredJenkinsJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job2").jenkinsServer("http://buildserver1").build());
        allStoredJenkinsJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job3").jenkinsServer("http://buildserver1").build());
        allStoredJenkinsJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://buildserver2").build());
        when(this.mockJobRepository.findAllByCollectorId(eq(collectorId))).thenReturn(allStoredJenkinsJobs);

        //test
        this.testee.collect(mockCollector);


        ArgumentCaptor<JenkinsCodeQualityJob> deletedJobsCaptor = ArgumentCaptor.forClass(JenkinsCodeQualityJob.class);
        verify(this.mockJobRepository, times(1)).delete(deletedJobsCaptor.capture());
        JenkinsCodeQualityJob capturedValues = deletedJobsCaptor.getValue();
        assertThat(capturedValues.getOptions().get("jenkinsServer")).isEqualTo("http://buildserver0");
    }

    @Test
    public void createsNewJobsIfNotFound() {
        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);

        ObjectId collectorId = new ObjectId();
        when(mockCollector.getId()).thenReturn(collectorId);

        when(mockCollector.getBuildServers()).thenReturn(Arrays.asList("http://myBuildServer"));
        List<JenkinsJob> jobsWithNewJob = new ArrayList<>();
        jobsWithNewJob.add(JenkinsJob.newBuilder().jobName("job1").url("http://myBuildServer/job1")
                .lastSuccessfulBuild(JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("junit.xml").build()).build()).build());
        jobsWithNewJob.add(JenkinsJob.newBuilder().jobName("myNewJob").url("http://myBuildServer/myNewJob")
                .lastSuccessfulBuild(JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("junit.xml").build()).build()).build());
        jobsWithNewJob.add(JenkinsJob.newBuilder().jobName("job1").url("http://myBuildServer2/job1")
                .lastSuccessfulBuild(JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("junit.xml").build()).build()).build());
        when(mockJenkinsHelper.getJobs(anyList())).thenReturn(jobsWithNewJob);

        List<JenkinsCodeQualityJob> existingJobs = new ArrayList<>();
        existingJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://myBuildServer").build());
        existingJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://myBuildServer2").build());
        when(mockJobRepository.findAllByCollectorId(same(collectorId))).thenReturn(existingJobs);

        // test
        this.testee.collect(mockCollector);

        //
        ArgumentCaptor<JenkinsCodeQualityJob> newJobCaptor = ArgumentCaptor.forClass(JenkinsCodeQualityJob.class);
        verify(this.mockJobRepository, times(1)).save(newJobCaptor.capture());
        JenkinsCodeQualityJob capturedJob = newJobCaptor.getValue();
        JenkinsCodeQualityJob expectedNewJob = JenkinsCodeQualityJob.newBuilder().collectorId(collectorId).jobName("myNewJob").jenkinsServer("http://myBuildServer/myNewJob").build();
        assertThat(capturedJob).isEqualToComparingFieldByField(expectedNewJob);
        assertThat(capturedJob.getNiceName()).isEqualTo("myNewJob");
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

    private List<JenkinsJob> getJenkinsJobs() {
        List<JenkinsJob> allJenkinsJobs = new ArrayList<>();
        allJenkinsJobs.add(JenkinsJob.newBuilder()
                .url("http://buildserver2/job1").jobName("job1").lastSuccessfulBuild(JenkinsBuild.newBuilder()
                        .artifact(Artifact.newBuilder().fileName("junit.xml").build())
                        .artifact(Artifact.newBuilder().fileName("something.war").build()).build())
                .build());
        allJenkinsJobs.add(JenkinsJob.newBuilder()
                .url("http://buildserver/job2").jobName("job2").lastSuccessfulBuild(JenkinsBuild.newBuilder()
                        .artifact(Artifact.newBuilder().fileName("junit.txt").build())
                        .artifact(Artifact.newBuilder().fileName("something.war").build()).build())
                .build());
        allJenkinsJobs.add(JenkinsJob.newBuilder()
                .url("http://buildserver/job3").jobName("job3").lastSuccessfulBuild(JenkinsBuild.newBuilder()
                        .artifact(Artifact.newBuilder().fileName("something.war").path("/artifact/").build()).build())
                .build());
        return allJenkinsJobs;
    }

    @Test
    public void jenkinsHelperDoesNotReturnAnyJobsTheCollectShouldReturn() {
        // expect the collector to deliver its config
        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
        List<String> buildServers = new ArrayList<>();
        buildServers.add("http://buildserver");
        buildServers.add("http://buildserver2");
        when(mockCollector.getBuildServers()).thenAnswer(invocationOnMock -> buildServers);

        when(mockJenkinsHelper.getJobs(argThat(hasItems("http://buildserver", "http://buildserver")))).thenReturn(null);

        this.testee.collect(mockCollector);
    }

}