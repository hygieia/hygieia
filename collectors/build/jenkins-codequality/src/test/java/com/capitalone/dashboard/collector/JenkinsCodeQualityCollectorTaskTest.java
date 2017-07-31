package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.Artifact;
import com.capitalone.dashboard.jenkins.JenkinsBuild;
import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.JenkinsSettings;
import com.capitalone.dashboard.jenkins.model.JenkinsCodeQualityJob;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.quality.*;
import com.capitalone.dashboard.repository.JenkinsCodeQualityCollectorRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityJobRepository;
import com.capitalone.dashboard.utils.CodeQualityService;
import org.assertj.core.api.Condition;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import javax.xml.datatype.DatatypeFactory;
import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;


public class JenkinsCodeQualityCollectorTaskTest {

    private JenkinsCodeQualityCollectorTask testee;

    private TaskScheduler mockScheduler;
    private JenkinsCodeQualityCollectorRepository mockRepo;
    private JenkinsClient mockJenkinsHelper;
    private JenkinsCodeQualityJobRepository mockJobRepository;
    private CodeQualityService mockDataService;

    @Before
    public void setup() {
        mockScheduler = mock(TaskScheduler.class);
        mockRepo = mock(JenkinsCodeQualityCollectorRepository.class);
        mockJenkinsHelper = mock(JenkinsClient.class);
        mockJobRepository = mock(JenkinsCodeQualityJobRepository.class);
        mockDataService = mock(CodeQualityService.class);

        JenkinsSettings settings = new JenkinsSettings();
        settings.setCron("0 * * * * *");
        settings.setServers(Arrays.asList("server1", "server2"));
        settings.setArtifactRegex(ArtifactType.junit, Arrays.asList(".*\\.xml"));
        this.testee = new JenkinsCodeQualityCollectorTask(mockScheduler, mockRepo, mockJobRepository, settings, mockJenkinsHelper, mockDataService);
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
        when(mockJenkinsHelper.getJobs(anyList())).thenReturn(allJenkinsJobs);

        // return a list of jobs when asked
        JunitXmlReport testXmlReport = new JunitXmlReport();
        testXmlReport.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1980, 10, 10, 10, 10, 10, 10, 0));
        JunitXmlReport testXmlReport1 = new JunitXmlReport();
        testXmlReport1.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(1990, 11, 11, 11, 11, 11, 11, 0));
        List<JunitXmlReport> testXmlReports = Arrays.asList(testXmlReport, testXmlReport1);
        when(mockJenkinsHelper.getLatestArtifacts(eq(JunitXmlReport.class), any(JenkinsJob.class), any(Pattern.class))).thenReturn(testXmlReports);

        //
        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);
        when(mockJobRepository.findAllByCollectorId(any(ObjectId.class))).thenReturn(Collections.singletonList(dbJob));


        ObjectId collectorId = new ObjectId();
        when(mockCollector.getId()).thenReturn(collectorId);

        // test
        testee.collect(mockCollector);

        // verify
        ArgumentCaptor<JenkinsJob> jobCapture = ArgumentCaptor.forClass(JenkinsJob.class);
        ArgumentCaptor<Pattern> patternCaptor = ArgumentCaptor.forClass(Pattern.class);
        verify(mockJenkinsHelper).getLatestArtifacts(eq(JunitXmlReport.class), jobCapture.capture(), patternCaptor.capture());
        JenkinsJob capturedJob = jobCapture.getValue();
        assertThat(capturedJob).hasFieldOrPropertyWithValue("name", "job1").hasFieldOrPropertyWithValue("url", "http://buildserver2/job1");
        Pattern capturedPattern = patternCaptor.getValue();
        assertThat(capturedPattern.pattern().equals(".*\\.xml"));
        verify(mockDataService).storeJob(same(allJenkinsJobs.get(0)), eq(dbJob), eq(testXmlReports));


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
                JenkinsJob.newBuilder().url("http://buildserver1/job1").jobName("job1").build(),
                JenkinsJob.newBuilder().url("http://buildserver2/job1").jobName("job1").build()
        ));

        List<JenkinsCodeQualityJob> allStoredJenkinsJobs = new ArrayList<>();
        allStoredJenkinsJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://buildserver0/job1").build());
        allStoredJenkinsJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://buildserver1/job1").build());
        allStoredJenkinsJobs.add(JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://buildserver2/job1").build());
        when(this.mockJobRepository.findAllByCollectorId(eq(collectorId))).thenReturn(allStoredJenkinsJobs);

        //test
        this.testee.collect(mockCollector);


        ArgumentCaptor<JenkinsCodeQualityJob> deletedJobsCaptor = ArgumentCaptor.forClass(JenkinsCodeQualityJob.class);
        verify(this.mockJobRepository, times(1)).delete(deletedJobsCaptor.capture());
        JenkinsCodeQualityJob capturedValues = deletedJobsCaptor.getValue();
        assertThat(capturedValues.getOptions().get("jenkinsServer")).isEqualTo("http://buildserver0/job1");
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

        {
            List<JenkinsCodeQualityJob> existingJobs = new ArrayList<>();
            JenkinsCodeQualityJob job1 = JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://myBuildServer/job1").build();
            ObjectId job1Id = new ObjectId();
            job1.setId(job1Id);
            existingJobs.add(job1);
            JenkinsCodeQualityJob job2 = JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://myBuildServer2/job1").build();
            ObjectId job2Id = new ObjectId();
            job2.setId(job2Id);
            existingJobs.add(job2);
            List<JenkinsCodeQualityJob> allJobsPostCreate = new ArrayList<>();
            JenkinsCodeQualityJob job1a = JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://myBuildServer/job1").build();
            job1a.setId(job1Id);
            allJobsPostCreate.add(job1a);
            JenkinsCodeQualityJob job2a = JenkinsCodeQualityJob.newBuilder().jobName("job1").jenkinsServer("http://myBuildServer2/job1").build();
            job2a.setId(job2Id);
            allJobsPostCreate.add(job2a);
            JenkinsCodeQualityJob job3a = JenkinsCodeQualityJob.newBuilder().jobName("myNewJob").jenkinsServer("http://myBuildServer2/myNewJob").build();
            ObjectId job3Id = new ObjectId();
            job3a.setId(job3Id);
            allJobsPostCreate.add(job3a);
            when(mockJobRepository.findAllByCollectorId(same(collectorId))).thenReturn(existingJobs, existingJobs, allJobsPostCreate);
        }

        // test
        this.testee.collect(mockCollector);

        //
        ArgumentCaptor<JenkinsCodeQualityJob> newJobCaptor = ArgumentCaptor.forClass(JenkinsCodeQualityJob.class);
        verify(this.mockJobRepository, times(1)).save(newJobCaptor.capture());
        JenkinsCodeQualityJob capturedJob = newJobCaptor.getValue();
        JenkinsCodeQualityJob expectedNewJob = JenkinsCodeQualityJob.newBuilder().collectorId(collectorId).jobName("myNewJob").jenkinsServer("http://myBuildServer/myNewJob").description("myNewJob (http://myBuildServer/myNewJob)").build();
        assertThat(capturedJob).isEqualToComparingFieldByField(expectedNewJob);
        assertThat(capturedJob.getNiceName()).isEqualTo("myNewJob");
    }

    @Test
    public void flattensJenkinsJobsToFlatMapBeforeFilter() {
        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);

        ObjectId collectorId = new ObjectId();
        when(mockCollector.getId()).thenReturn(collectorId);

        when(mockCollector.getBuildServers()).thenReturn(Arrays.asList("http://myBuildServer"));
        List<JenkinsJob> jobsWithNewJob = new ArrayList<>();
        jobsWithNewJob.add(JenkinsJob.newBuilder().jobName("job1").url("http://myBuildServer/job1")
            .job(
                JenkinsJob.newBuilder().jobName("subJob").url("http://myBuildServer/job1/subJob")
                    .lastSuccessfulBuild(
                        JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("junit.xml").build()).build()
                    ).build())
            .job(
                JenkinsJob.newBuilder().jobName("subJob2").url("http://myBuildServer/job1/subJob2").job(
                    JenkinsJob.newBuilder().jobName("subJob3").url("http://myBuildServer/job1/subJob2/subJob3")
                        .lastSuccessfulBuild(
                            JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("junit.xml").build()).build()
                        ).build()
                ).build()
            )
            .build());
        when(mockJenkinsHelper.getJobs(anyList())).thenReturn(jobsWithNewJob);



        // test
        this.testee.collect(mockCollector);

        //
        ArgumentCaptor<JenkinsCodeQualityJob> newJobCaptor = ArgumentCaptor.forClass(JenkinsCodeQualityJob.class);
        verify(this.mockJobRepository, times(2)).save(newJobCaptor.capture());
        List<JenkinsCodeQualityJob> capturedJobs = newJobCaptor.getAllValues();
        final List<String> expectedJobNames = Arrays.asList("subJob","subJob3");
        Condition<JenkinsCodeQualityJob> jobNamesThatMatch = new Condition<JenkinsCodeQualityJob>("job names match") {
            @Override
            public boolean matches(JenkinsCodeQualityJob value) {
                return expectedJobNames.contains(value.getJobName());
            }
        };
        assertThat(capturedJobs).have(jobNamesThatMatch);

    }

    @Test
    public void flattensJenkinsMapBeforeFindingNewJobs() {
        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);

        ObjectId collectorId = new ObjectId();
        when(mockCollector.getId()).thenReturn(collectorId);

        when(mockCollector.getBuildServers()).thenReturn(Arrays.asList("http://myBuildServer"));
        List<JenkinsJob> jobsWithNewJob = new ArrayList<>();
        jobsWithNewJob.add(JenkinsJob.newBuilder().jobName("job1").url("http://myBuildServer/job1")
                .job(
                        JenkinsJob.newBuilder().jobName("subJob").url("http://myBuildServer/job1/subJob")
                                .lastSuccessfulBuild(
                                        JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("junit.xml").build()).build()
                                ).build())
                .job(
                        JenkinsJob.newBuilder().jobName("subJob2").url("http://myBuildServer/job1/subJob2").job(
                                JenkinsJob.newBuilder().jobName("subJob3").url("http://myBuildServer/job1/subJob2/subJob3")
                                        .lastSuccessfulBuild(
                                                JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("junit.xml").build()).build()
                                        ).build()
                        ).build()
                )
                .build());
        when(mockJenkinsHelper.getJobs(anyList())).thenReturn(jobsWithNewJob);

        List<JenkinsCodeQualityJob> jobs = new ArrayList<>();
        for (String url: Arrays.asList("http://buildserver1/job1","http://myBuildServer/job1/subJob","http://myBuildServer/job1/subJob2","http://myBuildServer/job1/subJob2/subJob3")) {
            JenkinsCodeQualityJob dbJob1 = JenkinsCodeQualityJob.newBuilder().jenkinsServer(url).build();
            ObjectId dbJobId1 = new ObjectId();
            dbJob1.setId(dbJobId1);
            jobs.add(dbJob1);
        }

        when(mockJobRepository.findAllByCollectorId(any(ObjectId.class))).thenReturn(jobs);



        // test
        this.testee.collect(mockCollector);

        // should not have saved anything
        verify(mockJobRepository,times(0)).save(any(JenkinsCodeQualityJob.class));

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

        when(mockJenkinsHelper.getJobs(anyList())).thenReturn((List) null);

        this.testee.collect(mockCollector);
    }

    @Test
    public void configuredToCollectJunitAndFindbugs() {
        JenkinsSettings settings = new JenkinsSettings();
        settings.setCron("0 * * * * *");
        settings.setServers(Arrays.asList("server1", "server2"));
        settings.setArtifactRegex(ArtifactType.junit, Arrays.asList("junit.xml"));
        settings.setArtifactRegex(ArtifactType.findbugs, Arrays.asList("findbugs.xml"));
        this.testee = new JenkinsCodeQualityCollectorTask(mockScheduler, mockRepo, mockJobRepository, settings, mockJenkinsHelper, mockDataService);


        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
        List<String> buildServers = new ArrayList<>();
        buildServers.add("http://buildserver");
        buildServers.add("http://buildserver2");
        when(mockCollector.getBuildServers()).thenAnswer(invocationOnMock -> buildServers);

        JenkinsJob job1 = JenkinsJob.newBuilder().url("http://buildserver2/job1").jobName("job1").lastSuccessfulBuild(
                JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("junit.xml").build()).artifact(Artifact.newBuilder().fileName("findbugs.xml").build()).build()).build();
        List<JenkinsJob> allJobs = Collections.singletonList(job1);

        when(mockJenkinsHelper.getJobs(anyList())).thenReturn(allJobs);
        List<JunitXmlReport> junitList = new ArrayList<>();
        junitList.add(new JunitXmlReport());
        when(mockJenkinsHelper.getLatestArtifacts(same(JunitXmlReport.class), any(JenkinsJob.class), any(Pattern.class))).thenReturn(junitList);
        List<FindBugsXmlReport> findBugsList = new ArrayList<>();
        findBugsList.add(new FindBugsXmlReport());
        when(mockJenkinsHelper.getLatestArtifacts(same(FindBugsXmlReport.class), any(JenkinsJob.class), any(Pattern.class))).thenReturn(findBugsList);

        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").jobName("job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        ObjectId collectorId = new ObjectId();
        when(mockCollector.getId()).thenReturn(collectorId);
        when(this.mockJobRepository.findAllByCollectorId(eq(collectorId))).thenReturn(Arrays.asList(dbJob));

        this.testee.collect(mockCollector);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(this.mockDataService).storeJob(same(job1), eq(dbJob), captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    public void configuredToCollectJacocoXml() {
        doConfiureTest(ArtifactType.jacoco,"jacoco.xml","jacoco.xml", new JacocoXmlReport());
    }

    @Test
    public void configuredToCollectPmdXml() {
        doConfiureTest(ArtifactType.pmd,"pmd.xml","pmd.xml", new PmdReport());
    }

    @Test
    public void configuredToCollectCheckStyleXml() {
        doConfiureTest(ArtifactType.checkstyle,"checkstyle.xml","checkstyle.xml", new CheckstyleReport());
    }

    private <T>  void doConfiureTest(ArtifactType type, String pattern, String artefactName, T report) {
        JenkinsSettings settings = new JenkinsSettings();
        settings.setCron("0 * * * * *");
        settings.setServers(Arrays.asList("server1", "server2"));
        settings.setArtifactRegex(type, Arrays.asList(pattern));
        this.testee = new JenkinsCodeQualityCollectorTask(mockScheduler, mockRepo, mockJobRepository, settings, mockJenkinsHelper, mockDataService);


        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
        List<String> buildServers = new ArrayList<>();
        buildServers.add("http://buildserver");
        buildServers.add("http://buildserver2");
        when(mockCollector.getBuildServers()).thenAnswer(invocationOnMock -> buildServers);

        JenkinsJob job1 = JenkinsJob.newBuilder().url("http://buildserver2/job1").jobName("job1").lastSuccessfulBuild(
                JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName(artefactName).build()).build()).build();
        List<JenkinsJob> allJobs = Collections.singletonList(job1);

        when(mockJenkinsHelper.getJobs(anyList())).thenReturn(allJobs);
        List<T> reports = new ArrayList<>();
        reports.add(report);
        when(mockJenkinsHelper.getLatestArtifacts(same(report.getClass()), any(JenkinsJob.class), any(Pattern.class))).thenAnswer(
                new Answer<List<T>>() {
                    @Override
                    public List<T> answer(InvocationOnMock invocationOnMock) throws Throwable {
                        return reports;
                    }
                });

        JenkinsCodeQualityJob dbJob = JenkinsCodeQualityJob.newBuilder().jenkinsServer("http://buildserver2/job1").jobName("job1").build();
        ObjectId dbJobId = new ObjectId();
        dbJob.setId(dbJobId);

        ObjectId collectorId = new ObjectId();
        when(mockCollector.getId()).thenReturn(collectorId);
        when(this.mockJobRepository.findAllByCollectorId(eq(collectorId))).thenReturn(Arrays.asList(dbJob));

        this.testee.collect(mockCollector);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(this.mockDataService).storeJob(same(job1), eq(dbJob), captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }

}