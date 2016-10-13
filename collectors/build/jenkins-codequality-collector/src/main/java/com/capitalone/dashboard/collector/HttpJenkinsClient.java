package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by plv163 on 12/10/2016.
 */
public class HttpJenkinsClient implements JenkinsClient {


    private RestTemplate restTemplate;
    private JenkinsSettings settings;
    private static final String JENKINS_JOB_URL = "%s/api/json?tree=jobs[name,url,lastSuccessfulBuild[artifacts[*]]]";
    private static final String JENKINS_ARTIFACT_URL = "%s/lastSuccessfulBuild/artifact/%s";

    @Autowired
    public HttpJenkinsClient(RestTemplate restTemplate, JenkinsSettings settings) {
        this.restTemplate = restTemplate;
        this.settings = settings;
    }

    @Override
    public List<JenkinsJob> getJobs(Iterable<String> servers) {
        List<JenkinsJob> jobs = new ArrayList<>();
        servers.forEach(server -> {
            final ResponseEntity<JobContainer> jobsOnServer = restTemplate.exchange(String.format(JENKINS_JOB_URL, server), HttpMethod.GET, createSecureRequestEntity(), JobContainer.class);
            jobs.addAll(jobsOnServer.getBody().getJobs());
        });
        return jobs;
    }

    @Override
    public <T> List<T> getLatestArtifacts(Class<T> type, JenkinsJob job, List<Pattern> matchingJobPatterns) {

        List<Artifact> allMatchingArtifacts = job.getLastSuccessfulBuild().getArtifacts().stream().filter(JenkinsPredicate.artifactContaining(matchingJobPatterns)).collect(Collectors.toList());

        List<T> xmlReports = new ArrayList<>();
        allMatchingArtifacts.forEach(artifact -> {
            ResponseEntity<T> response = restTemplate.exchange(String.format(JENKINS_ARTIFACT_URL, job.getUrl(), artifact.getRelativePath()), HttpMethod.GET, createSecureRequestEntity(), type);
            xmlReports.add(response.getBody());
        });

        return xmlReports;

    }

    public HttpEntity<?> createSecureRequestEntity() {
        if (settings.getApiKey() != null && settings.getUsername() != null) {
            HttpHeaders securityHeader = new HttpHeaders();
            byte[] encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64((
                    settings.getUsername() + ":" + settings.getApiKey()).getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);
            securityHeader.add(HttpHeaders.AUTHORIZATION, authHeader);
            return new HttpEntity<>(securityHeader);
        }
        return null;
    }
}
