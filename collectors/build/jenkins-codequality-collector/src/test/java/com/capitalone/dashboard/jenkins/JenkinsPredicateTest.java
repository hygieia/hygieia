package com.capitalone.dashboard.jenkins;

import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.mockito.Mockito.when;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsPredicateTest {


    @Test
    public void artefactsFilterReturnsOnlyJobsThatMatch() throws Exception {

        List<JenkinsJob> jenkinsJobs = jenkinsJobs();

        List<JenkinsJob> allMatching = jenkinsJobs.stream().filter(JenkinsPredicate.artefactContaining(Collections.singletonList(Pattern.compile(".*\\.xml")))).collect(Collectors.toList());

        assertThat(allMatching).hasSize(1).is(new Condition<JenkinsJob>(jenkinsJob -> jenkinsJob.getJobName().equals("job"), "job nmae is %s", "job"), atIndex(0));

    }

    @Test
    public void multiplePatternsMatched() throws Exception {
        List<JenkinsJob> jenkinsJobs = jenkinsJobs();

        List<JenkinsJob> allMatching = jenkinsJobs.stream().filter(JenkinsPredicate.artefactContaining(Arrays.asList(Pattern.compile(".*\\.war"), Pattern.compile(".*\\.xml")))).collect(Collectors.toList());

        assertThat(allMatching).hasSize(2);
        assertThat(allMatching.stream().filter(job->"http://jenkins0/".equals(job.getJenkinsServer())).count()).isEqualTo(0);
    }

    private List<JenkinsJob> jenkinsJobs() {
        List<JenkinsJob> jenkinsJobs = new ArrayList<>();
        jenkinsJobs.add(JenkinsJob.newBuilder().jenkinsServer("http://jenkins0/").jobName("job").build());
        jenkinsJobs.add(JenkinsJob.newBuilder().jenkinsServer("http://jenkins1/").jobName("job2").artefact(Artefact.newBuilder().artefactName("someting.war").build()).build());
        jenkinsJobs.add(JenkinsJob.newBuilder().jenkinsServer("http://jenkins2/").jobName("job").artefact(Artefact.newBuilder().artefactName("someting.xml").build()).build());
        return jenkinsJobs;
    }


}