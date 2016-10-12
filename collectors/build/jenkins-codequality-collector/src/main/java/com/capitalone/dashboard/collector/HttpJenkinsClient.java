package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.Artifact;
import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.JenkinsPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by plv163 on 12/10/2016.
 */
public class HttpJenkinsClient implements JenkinsClient {


    private RestTemplate restTemplate;
    private static final String JENKINS_ARTIFACT_URL = "%s%s/lastSuccessfulBuild/artifact/%s";

    @Autowired
    public HttpJenkinsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }

    @Override
    public List<JenkinsJob> getJobs(Iterable<String> servers) {
        return null;
    }

    @Override
    public <T> List<T> getLatestArtifacts(Class<T> type, JenkinsJob job, List<Pattern> matchingJobPatterns) {
        List<Artifact> allMatchingArtifacts = job.getArtifacts().stream().filter(JenkinsPredicate.artifactContaining(matchingJobPatterns)).collect(Collectors.toList());

        allMatchingArtifacts.forEach(artifact -> {
            ResponseEntity<T> response = restTemplate.getForEntity(String.format(JENKINS_ARTIFACT_URL, job.getJenkinsServer(), job.getJobName(), artifact.getPath()), type);
        });

        return new ArrayList<T>();
    }
}
