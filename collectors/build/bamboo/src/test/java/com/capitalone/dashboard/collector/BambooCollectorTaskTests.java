package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.BambooCollector;
import com.capitalone.dashboard.model.BambooJob;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.BambooCollectorRepository;
import com.capitalone.dashboard.repository.BambooJobRepository;
import com.google.common.collect.Sets;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BambooCollectorTaskTests {

    @Mock private TaskScheduler taskScheduler;
    @Mock private BambooCollectorRepository bambooCollectorRepository;
    @Mock private BambooJobRepository bambooJobRepository;
    @Mock private BuildRepository buildRepository;
    @Mock private BambooClient bambooClient;
    @Mock private BambooSettings bambooSettings;
    @Mock private ComponentRepository dbComponentRepository;

    @InjectMocks private BambooCollectorTask task;

    private static final String SERVER1 = "server1";
    private static final String NICENAME1 = "niceName1";

    @Test
    public void collect_noBuildServers_nothingAdded() {
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(new BambooCollector());
        verifyZeroInteractions(bambooClient, buildRepository);
    }

    @Test
    public void collect_noJobsOnServer_nothingAdded() {
        when(bambooClient.getInstanceJobs(SERVER1)).thenReturn(new HashMap<BambooJob, Set<Build>>());
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collectorWithOneServer());

        verify(bambooClient).getInstanceJobs(SERVER1);
        verifyNoMoreInteractions(bambooClient, buildRepository);
    }

    @Test
    public void collect_twoJobs_jobsAdded() {
        when(bambooClient.getInstanceJobs(SERVER1)).thenReturn(twoJobsWithTwoBuilds(SERVER1, NICENAME1));
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collectorWithOneServer());
        verify(bambooJobRepository, times(1)).save(anyListOf(BambooJob.class));
    }

    @Test
    public void collect_oneJob_exists_notAdded() {
        BambooCollector collector = collectorWithOneServer();
        BambooJob job = bambooJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        when(bambooClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job));
        when(bambooJobRepository.findJob(collector.getId(), SERVER1, job.getJobName()))
                .thenReturn(job);
        when(dbComponentRepository.findAll()).thenReturn(components());

        task.collect(collector);

        verify(bambooJobRepository, never()).save(job);
    }


    @Test
    public void delete_job() {
        BambooCollector collector = collectorWithOneServer();
		collector.setId(ObjectId.get());
        BambooJob job1 = bambooJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        job1.setCollectorId(collector.getId());
        BambooJob job2 = bambooJob("JOB2", SERVER1, "JOB2_URL", NICENAME1);
        job2.setCollectorId(collector.getId());
        List<BambooJob> jobs = new ArrayList<>();
        jobs.add(job1);
        jobs.add(job2);
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        when(bambooClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job1));
        when(bambooJobRepository.findByCollectorIdIn(udId)).thenReturn(jobs);
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);
        List<BambooJob> delete = new ArrayList<>();
        delete.add(job2);
        verify(bambooJobRepository, times(1)).delete(delete);
    }

    @Test
    public void delete_never_job() {
        BambooCollector collector = collectorWithOneServer();
		collector.setId(ObjectId.get());
        BambooJob job1 = bambooJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        job1.setCollectorId(collector.getId());
        List<BambooJob> jobs = new ArrayList<>();
        jobs.add(job1);
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        when(bambooClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job1));
        when(bambooJobRepository.findByCollectorIdIn(udId)).thenReturn(jobs);
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);
        verify(bambooJobRepository, never()).delete(anyListOf(BambooJob.class));
    }

    @Test
    public void collect_jobNotEnabled_buildNotAdded() {
        BambooCollector collector = collectorWithOneServer();
        BambooJob job = bambooJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        Build build = build("JOB1_1", "JOB1_1_URL");

        when(bambooClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job, build));
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);

        verify(buildRepository, never()).save(build);
    }

    @Test
    public void collect_jobEnabled_buildExists_buildNotAdded() {
        BambooCollector collector = collectorWithOneServer();
        BambooJob job = bambooJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        Build build = build("JOB1_1", "JOB1_1_URL");

        when(bambooClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job, build));
        when(bambooJobRepository.findEnabledJobs(collector.getId(), SERVER1))
                .thenReturn(Arrays.asList(job));
        when(buildRepository.findByCollectorItemIdAndNumber(job.getId(), build.getNumber())).thenReturn(build);
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);

        verify(buildRepository, never()).save(build);
    }

    @Test
    public void collect_jobEnabled_newBuild_buildAdded() {
        BambooCollector collector = collectorWithOneServer();
        BambooJob job = bambooJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        Build build = build("JOB1_1", "JOB1_1_URL");

        when(bambooClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job, build));
        when(bambooJobRepository.findEnabledJobs(collector.getId(), SERVER1))
                .thenReturn(Arrays.asList(job));
        when(buildRepository.findByCollectorItemIdAndNumber(job.getId(), build.getNumber())).thenReturn(null);
        when(bambooClient.getBuildDetails(build.getBuildUrl(), job.getInstanceUrl())).thenReturn(build);
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);

        verify(buildRepository, times(1)).save(build);
    }

    private BambooCollector collectorWithOneServer() {
        return BambooCollector.prototype(Arrays.asList(SERVER1), Arrays.asList(NICENAME1));
    }

    private Map<BambooJob, Set<Build>> oneJobWithBuilds(BambooJob job, Build... builds) {
        Map<BambooJob, Set<Build>> jobs = new HashMap<>();
        jobs.put(job, Sets.newHashSet(builds));
        return jobs;
    }

    private Map<BambooJob, Set<Build>> twoJobsWithTwoBuilds(String server, String niceName) {
        Map<BambooJob, Set<Build>> jobs = new HashMap<>();
        jobs.put(bambooJob("JOB1", server, "JOB1_URL", niceName), Sets.newHashSet(build("JOB1_1", "JOB1_1_URL"), build("JOB1_2", "JOB1_2_URL")));
        jobs.put(bambooJob("JOB2", server, "JOB2_URL", niceName), Sets.newHashSet(build("JOB2_1", "JOB2_1_URL"), build("JOB2_2", "JOB2_2_URL")));
        return jobs;
    }

    private BambooJob bambooJob(String jobName, String instanceUrl, String jobUrl, String niceName) {
        BambooJob job = new BambooJob();
        job.setJobName(jobName);
        job.setInstanceUrl(instanceUrl);
        job.setJobUrl(jobUrl);
        job.setNiceName(niceName);
        return job;
    }

    private Build build(String number, String url) {
        Build build = new Build();
        build.setNumber(number);
        build.setBuildUrl(url);
        return build;
    }

    private ArrayList<com.capitalone.dashboard.model.Component> components() {
    	ArrayList<com.capitalone.dashboard.model.Component> cArray = new ArrayList<com.capitalone.dashboard.model.Component>();
    	com.capitalone.dashboard.model.Component c = new Component();
    	c.setId(new ObjectId());
    	c.setName("COMPONENT1");
    	c.setOwner("JOHN");
    	cArray.add(c);
    	return cArray;
    }
}
