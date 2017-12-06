package com.capitalone.dashboard.jenkins;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class JenkinsJobTest {

  @Test
  public void fattenMapCorrectly(){
    JenkinsJob job = JenkinsJob.newBuilder().jobName("job1").job(JenkinsJob.newBuilder().jobName("job2").build()).job(JenkinsJob.newBuilder().jobName("job3").job(JenkinsJob.newBuilder().jobName("job4").build()).build()).build();

    List<JenkinsJob> jobs = new ArrayList<>();
    jobs.add(job);

    List<JenkinsJob> flattenedJobs = jobs.stream().flatMap(JenkinsJob::streamJobs).collect(Collectors.toList());
    assertThat(flattenedJobs,hasSize(4));
  }

  @Test
  public void flattenMapWithNullInJobsWorks(){
    JenkinsJob job = JenkinsJob.newBuilder().jobName("job1").build();
    Whitebox.setInternalState(job,"jobs",null);

    List<JenkinsJob> jobs = new ArrayList<>();
    jobs.add(job);

    List<JenkinsJob> flattenedJobs = jobs.stream().flatMap(JenkinsJob::streamJobs).collect(Collectors.toList());
    assertThat(flattenedJobs,hasSize(1));
  }

}