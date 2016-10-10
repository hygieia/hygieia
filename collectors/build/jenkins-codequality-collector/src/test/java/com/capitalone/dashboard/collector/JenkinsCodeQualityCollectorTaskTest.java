package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.Artefact;
import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.repository.JenkinsCodeQualityRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsCodeQualityCollectorTaskTest {

    private JenkinsCodeQualityCollectorTask testee;

    private TaskScheduler mockScheduler;
    private JenkinsCodeQualityRepository mockRepo;
    private JenkinsClient mockJenkinsHelper;

    @Before
    public void setup() {
        mockScheduler = mock(TaskScheduler.class);
        mockRepo = mock(JenkinsCodeQualityRepository.class);
        mockJenkinsHelper = mock(JenkinsClient.class);
        this.testee = new JenkinsCodeQualityCollectorTask(mockScheduler, mockRepo, "0 * * * * *", mockJenkinsHelper);
    }

    @Test
    public void getCollectorReturnsAJenkinsCodeQualityCollector() {

        assertThat(testee.getCollector()).isNotNull().isInstanceOf(JenkinsCodeQualityCollector.class);
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

        JenkinsCodeQualityCollector mockCollector = mock(JenkinsCodeQualityCollector.class);
        List<String> buildServers = new ArrayList<>();
        buildServers.add("http://buildserver");
        buildServers.add("http://buildserver2");
        when(mockCollector.getBuildServers()).thenAnswer(invocationOnMock -> {
            return buildServers;
        });

        List<JenkinsJob> allJenkinsJobs = new ArrayList<>();
        allJenkinsJobs.add(JenkinsJob.newBuilder()
                .jenkinsServer("http://buildserver").jobName("job1")
                .artefact(Artefact.newBuilder().artefactName("junit.xml").build())
                .artefact(Artefact.newBuilder().artefactName("something.war").build())
                .build());
        allJenkinsJobs.add(JenkinsJob.newBuilder()
                .jenkinsServer("http://buildserver").jobName("job2")
                .artefact(Artefact.newBuilder().artefactName("junit.xml").build())
                .artefact(Artefact.newBuilder().artefactName("something.war").build())
                .build());
        allJenkinsJobs.add(JenkinsJob.newBuilder()
                .jenkinsServer("http://buildserver").jobName("job3")
                .artefact(Artefact.newBuilder().artefactName("something.war").build())
                .build());
        when(mockJenkinsHelper.getJobs(argThat(hasItems("http://buildserver", "http://buildserver")))).thenReturn(allJenkinsJobs);


        testee.collect(mockCollector);

    }

}