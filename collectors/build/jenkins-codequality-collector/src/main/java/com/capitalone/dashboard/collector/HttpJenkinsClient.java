package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class HttpJenkinsClient implements JenkinsClient {


    private RestTemplate restTemplate;
    private JenkinsSettings settings;
    private static final String JOBS_SEP=",";
    private static final String JOBS_PARAM="jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]";
    private static final String JOBS_CLOSE="]";
    private static final String JENKINS_JOB_BASE_URL = "%s/api/json?tree=";
    private static final String JENKINS_ARTIFACT_URL = "%s/lastSuccessfulBuild/artifact/%s";

    @Autowired
    public HttpJenkinsClient(RestTemplate restTemplate, JenkinsSettings settings) {
        this.restTemplate = restTemplate;
        this.settings = settings;
    }

    @Override
    public List<JenkinsJob> getJobs(List<String> servers) {
        List<JenkinsJob> jobs = new ArrayList<>();
        servers.forEach(server -> {
            // TODO get the job depth stuff in place
            StringBuilder jobDepth = new StringBuilder();
            jobDepth.append(JENKINS_JOB_BASE_URL);
            int maxJobDepth = this.settings.getJobDepth();
            for (int i = 0; i < maxJobDepth ; i++) {
                if (i > 0) {
                    jobDepth.append(JOBS_SEP);
                }
                jobDepth.append(JOBS_PARAM);
            }
            for (int i = 0; i < maxJobDepth; i++) {
                jobDepth.append(JOBS_CLOSE);
            }

            String url = String.format(jobDepth.toString(), server);
            final ResponseEntity<JobContainer> jobsOnServer;
            try {
                jobsOnServer = restTemplate.exchange(new URI(url), HttpMethod.GET, createSecureRequestEntity(), JobContainer.class);
                jobs.addAll(jobsOnServer.getBody().getJobs());
            } catch (URISyntaxException e) {
                // silently swallow
            }
        });
        return jobs;
    }

    @Override
    public <T> List<T> getLatestArtifacts(Class<T> type, JenkinsJob job, Pattern matchingJobPatterns) {

        List<Artifact> allMatchingArtifacts = job.getLastSuccessfulBuild().getArtifacts().stream().filter(JenkinsPredicate.artifactContaining(matchingJobPatterns)).collect(Collectors.toList());

        List<T> xmlReports = new ArrayList<>();
        allMatchingArtifacts.forEach(artifact -> {
            ResponseEntity<T> response = null;
            try {
                response = restTemplate.exchange(new URI(String.format(JENKINS_ARTIFACT_URL, job.getUrl(), artifact.getRelativePath())), HttpMethod.GET, createSecureRequestEntity(), type);
                xmlReports.add(response.getBody());
            } catch (URISyntaxException e) {
                // silently fail
            }
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
