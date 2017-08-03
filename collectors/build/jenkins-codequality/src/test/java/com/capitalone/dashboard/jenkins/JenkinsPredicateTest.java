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

public class JenkinsPredicateTest {


    @Test
    public void artefactsFilterReturnsOnlyJobsThatMatch() throws Exception {

        List<JenkinsJob> jenkinsJobs = jenkinsJobs();

        List<JenkinsJob> allMatching = jenkinsJobs.stream().filter(JenkinsPredicate.artifactInJobContaining(Collections.singletonList(Pattern.compile(".*\\.xml")))).collect(Collectors.toList());

        assertThat(allMatching).hasSize(1).is(new Condition<JenkinsJob>(jenkinsJob -> jenkinsJob.getName().equals("job"), "job nmae is %s", "job"), atIndex(0));

    }

    @Test
    public void artefactsInSubJobsAreNotReturnedAsMatched() throws Exception {
        List<JenkinsJob> jenkinsJobs1 = new ArrayList<>();
        jenkinsJobs1.add(JenkinsJob.newBuilder().url("http://jenkins0/").jobName("job1").job(
            JenkinsJob.newBuilder().jobName("job3").lastSuccessfulBuild(
                JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("something.xml").build()).build()).build())
            .build());
        jenkinsJobs1.add(JenkinsJob.newBuilder().url("http://jenkins0/").jobName("job").lastSuccessfulBuild(JenkinsBuild.newBuilder().build()).build());
        jenkinsJobs1.add(JenkinsJob.newBuilder().url("http://jenkins1/").jobName("job2")
                .lastSuccessfulBuild(JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("someting.war").build()).build()).build());
        jenkinsJobs1.add(JenkinsJob.newBuilder().url("http://jenkins2/").jobName("job")
                .lastSuccessfulBuild(JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("someting.xml").build()).build()).build());
        List<JenkinsJob> jenkinsJobs = jenkinsJobs1;

        List<JenkinsJob> allMatching = jenkinsJobs.stream().filter(JenkinsPredicate.artifactInJobContaining(Collections.singletonList(Pattern.compile(".*\\.xml")))).collect(Collectors.toList());

      // do not recurse into the job structure
        assertThat(allMatching).hasSize(1).is(new Condition<JenkinsJob>(jenkinsJob -> jenkinsJob.getName().equals("job"), "job name is %s", "job"), atIndex(0));
    }

    @Test
    public void multiplePatternsMatched() throws Exception {
        List<JenkinsJob> jenkinsJobs = jenkinsJobs();

        List<JenkinsJob> allMatching = jenkinsJobs.stream().filter(JenkinsPredicate.artifactInJobContaining(Arrays.asList(Pattern.compile(".*\\.war"), Pattern.compile(".*\\.xml")))).collect(Collectors.toList());

        assertThat(allMatching).hasSize(2);
        assertThat(allMatching.stream().filter(job -> "http://jenkins0/job1".equals(job.getUrl())).count()).isEqualTo(0);
    }

    private List<JenkinsJob> jenkinsJobs() {
        List<JenkinsJob> jenkinsJobs = new ArrayList<>();
        jenkinsJobs.add(JenkinsJob.newBuilder().url("http://jenkins0/").jobName("job1").build());
        jenkinsJobs.add(JenkinsJob.newBuilder().url("http://jenkins0/").jobName("job").lastSuccessfulBuild(JenkinsBuild.newBuilder().build()).build());
        jenkinsJobs.add(JenkinsJob.newBuilder().url("http://jenkins1/").jobName("job2")
                .lastSuccessfulBuild(JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("someting.war").build()).build()).build());
        jenkinsJobs.add(JenkinsJob.newBuilder().url("http://jenkins2/").jobName("job")
                .lastSuccessfulBuild(JenkinsBuild.newBuilder().artifact(Artifact.newBuilder().fileName("someting.xml").build()).build()).build());
        return jenkinsJobs;
    }

    @Test
    public void multipleArtifactsAreMatched() {
        List<Artifact> artifacts = new ArrayList<>();
        artifacts.add(Artifact.newBuilder().fileName("yName").build());
        artifacts.add(Artifact.newBuilder().fileName("yName.xml").build());
        artifacts.add(Artifact.newBuilder().fileName("yName.txt").build());

        final List<Artifact> filteredArtifacts = artifacts.stream().filter(JenkinsPredicate.artifactContaining(Arrays.asList(Pattern.compile(".*\\.xml")))).collect(Collectors.toList());

        assertThat(filteredArtifacts).hasSize(1);
        assertThat(filteredArtifacts.get(0)).hasFieldOrPropertyWithValue("fileName", "yName.xml");
    }

    @Test
    public void singleArtefactMatched() {
        List<Artifact> artifacts = new ArrayList<>();
        artifacts.add(Artifact.newBuilder().fileName("yName").build());
        artifacts.add(Artifact.newBuilder().fileName("yName.xml").build());
        artifacts.add(Artifact.newBuilder().fileName("yName.txt").build());

        final List<Artifact> filteredArtifacts = artifacts.stream().filter(JenkinsPredicate.artifactContaining(Pattern.compile(".*\\.xml"))).collect(Collectors.toList());

        assertThat(filteredArtifacts).hasSize(1);
        assertThat(filteredArtifacts.get(0)).hasFieldOrPropertyWithValue("fileName", "yName.xml");
    }


}