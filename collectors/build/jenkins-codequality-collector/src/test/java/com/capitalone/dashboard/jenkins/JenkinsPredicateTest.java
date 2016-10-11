package com.capitalone.dashboard.jenkins;

import org.assertj.core.api.Condition;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsPredicateTest {


    @Test
    public void artefactsFilterReturnsOnlyJobsThatMatch() throws Exception {

        List<JenkinsJob> jenkinsJobs = jenkinsJobs();

        List<JenkinsJob> allMatching = jenkinsJobs.stream().filter(JenkinsPredicate.artifactContaining(Collections.singletonList(Pattern.compile(".*\\.xml")))).collect(Collectors.toList());

        assertThat(allMatching).hasSize(1).is(new Condition<JenkinsJob>(jenkinsJob -> jenkinsJob.getJobName().equals("job"), "job nmae is %s", "job"), atIndex(0));

    }

    @Test
    public void multiplePatternsMatched() throws Exception {
        List<JenkinsJob> jenkinsJobs = jenkinsJobs();

        List<JenkinsJob> allMatching = jenkinsJobs.stream().filter(JenkinsPredicate.artifactContaining(Arrays.asList(Pattern.compile(".*\\.war"), Pattern.compile(".*\\.xml")))).collect(Collectors.toList());

        assertThat(allMatching).hasSize(2);
        assertThat(allMatching.stream().filter(job->"http://jenkins0/".equals(job.getJenkinsServer())).count()).isEqualTo(0);
    }

    private List<JenkinsJob> jenkinsJobs() {
        List<JenkinsJob> jenkinsJobs = new ArrayList<>();
        jenkinsJobs.add(JenkinsJob.newBuilder().jenkinsServer("http://jenkins0/").jobName("job").build());
        jenkinsJobs.add(JenkinsJob.newBuilder().jenkinsServer("http://jenkins1/").jobName("job2").artifact(Artifact.newBuilder().artifactName("someting.war").build()).build());
        jenkinsJobs.add(JenkinsJob.newBuilder().jenkinsServer("http://jenkins2/").jobName("job").artifact(Artifact.newBuilder().artifactName("someting.xml").build()).build());
        return jenkinsJobs;
    }


}