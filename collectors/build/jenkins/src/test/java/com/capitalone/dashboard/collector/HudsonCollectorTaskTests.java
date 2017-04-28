package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.HudsonCollector;
import com.capitalone.dashboard.model.HudsonJob;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.HudsonCollectorRepository;
import com.capitalone.dashboard.repository.HudsonJobRepository;
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
public class HudsonCollectorTaskTests {

    @Mock private TaskScheduler taskScheduler;
    @Mock private HudsonCollectorRepository hudsonCollectorRepository;
    @Mock private HudsonJobRepository hudsonJobRepository;
    @Mock private BuildRepository buildRepository;
    @Mock private HudsonClient hudsonClient;
    @Mock private HudsonSettings hudsonSettings;
    @Mock private ComponentRepository dbComponentRepository;

    @InjectMocks private HudsonCollectorTask task;

    private static final String SERVER1 = "server1";
    private static final String NICENAME1 = "niceName1";

    @Test
    public void collect_noBuildServers_nothingAdded() {
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(new HudsonCollector());
        verifyZeroInteractions(hudsonClient, buildRepository);
    }

    @Test
    public void collect_noJobsOnServer_nothingAdded() {
        when(hudsonClient.getInstanceJobs(SERVER1)).thenReturn(new HashMap<HudsonJob, Set<Build>>());
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collectorWithOneServer());

        verify(hudsonClient).getInstanceJobs(SERVER1);
        verifyNoMoreInteractions(hudsonClient, buildRepository);
    }

    @Test
    public void collect_twoJobs_jobsAdded() {
        when(hudsonClient.getInstanceJobs(SERVER1)).thenReturn(twoJobsWithTwoBuilds(SERVER1, NICENAME1));
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collectorWithOneServer());
        verify(hudsonJobRepository, times(1)).save(anyListOf(HudsonJob.class));
    }

    @Test
    public void collect_oneJob_exists_notAdded() {
        HudsonCollector collector = collectorWithOneServer();
        HudsonJob job = hudsonJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        when(hudsonClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job));
        when(hudsonJobRepository.findJob(collector.getId(), SERVER1, job.getJobName()))
                .thenReturn(job);
        when(dbComponentRepository.findAll()).thenReturn(components());

        task.collect(collector);

        verify(hudsonJobRepository, never()).save(job);
    }


    @Test
    public void delete_job() {
        HudsonCollector collector = collectorWithOneServer();
		collector.setId(ObjectId.get());
        HudsonJob job1 = hudsonJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        job1.setCollectorId(collector.getId());
        HudsonJob job2 = hudsonJob("JOB2", SERVER1, "JOB2_URL", NICENAME1);
        job2.setCollectorId(collector.getId());
        List<HudsonJob> jobs = new ArrayList<>();
        jobs.add(job1);
        jobs.add(job2);
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        when(hudsonClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job1));
        when(hudsonJobRepository.findByCollectorIdIn(udId)).thenReturn(jobs);
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);
        List<HudsonJob> delete = new ArrayList<>();
        delete.add(job2);
        verify(hudsonJobRepository, times(1)).delete(delete);
    }

    @Test
    public void delete_never_job() {
        HudsonCollector collector = collectorWithOneServer();
		collector.setId(ObjectId.get());
        HudsonJob job1 = hudsonJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        job1.setCollectorId(collector.getId());
        List<HudsonJob> jobs = new ArrayList<>();
        jobs.add(job1);
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        when(hudsonClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job1));
        when(hudsonJobRepository.findByCollectorIdIn(udId)).thenReturn(jobs);
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);
        verify(hudsonJobRepository, never()).delete(anyListOf(HudsonJob.class));
    }

    @Test
    public void collect_jobNotEnabled_buildNotAdded() {
        HudsonCollector collector = collectorWithOneServer();
        HudsonJob job = hudsonJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        Build build = build("JOB1_1", "JOB1_1_URL");

        when(hudsonClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job, build));
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);

        verify(buildRepository, never()).save(build);
    }

    @Test
    public void collect_jobEnabled_buildExists_buildNotAdded() {
        HudsonCollector collector = collectorWithOneServer();
        HudsonJob job = hudsonJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        Build build = build("JOB1_1", "JOB1_1_URL");

        when(hudsonClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job, build));
        when(hudsonJobRepository.findEnabledJobs(collector.getId(), SERVER1))
                .thenReturn(Arrays.asList(job));
        when(buildRepository.findByCollectorItemIdAndNumber(job.getId(), build.getNumber())).thenReturn(build);
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);

        verify(buildRepository, never()).save(build);
    }

    @Test
    public void collect_jobEnabled_newBuild_buildAdded() {
        HudsonCollector collector = collectorWithOneServer();
        HudsonJob job = hudsonJob("JOB1", SERVER1, "JOB1_URL", NICENAME1);
        Build build = build("JOB1_1", "JOB1_1_URL");

        when(hudsonClient.getInstanceJobs(SERVER1)).thenReturn(oneJobWithBuilds(job, build));
        when(hudsonJobRepository.findEnabledJobs(collector.getId(), SERVER1))
                .thenReturn(Arrays.asList(job));
        when(buildRepository.findByCollectorItemIdAndNumber(job.getId(), build.getNumber())).thenReturn(null);
        when(hudsonClient.getBuildDetails(build.getBuildUrl(), job.getInstanceUrl())).thenReturn(build);
        when(dbComponentRepository.findAll()).thenReturn(components());
        task.collect(collector);

        verify(buildRepository, times(1)).save(build);
    }

    private HudsonCollector collectorWithOneServer() {
        return HudsonCollector.prototype(Arrays.asList(SERVER1), Arrays.asList(NICENAME1));
    }

    private Map<HudsonJob, Set<Build>> oneJobWithBuilds(HudsonJob job, Build... builds) {
        Map<HudsonJob, Set<Build>> jobs = new HashMap<>();
        jobs.put(job, Sets.newHashSet(builds));
        return jobs;
    }

    private Map<HudsonJob, Set<Build>> twoJobsWithTwoBuilds(String server, String niceName) {
        Map<HudsonJob, Set<Build>> jobs = new HashMap<>();
        jobs.put(hudsonJob("JOB1", server, "JOB1_URL", niceName), Sets.newHashSet(build("JOB1_1", "JOB1_1_URL"), build("JOB1_2", "JOB1_2_URL")));
        jobs.put(hudsonJob("JOB2", server, "JOB2_URL", niceName), Sets.newHashSet(build("JOB2_1", "JOB2_1_URL"), build("JOB2_2", "JOB2_2_URL")));
        return jobs;
    }

    private HudsonJob hudsonJob(String jobName, String instanceUrl, String jobUrl, String niceName) {
        HudsonJob job = new HudsonJob();
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
