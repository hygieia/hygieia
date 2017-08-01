package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.*;
import com.capitalone.dashboard.model.quality.JunitXmlReport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class HttpJenkinsClientTest {

    private RestTemplate mockRestTemplate;

    private HttpJenkinsClient testee;

    @Before
    public void setup() {
        mockRestTemplate = mock(RestTemplate.class);
        JenkinsSettings settings = new JenkinsSettings();
        settings.setJobDepth(3);
        testee = new HttpJenkinsClient(mockRestTemplate, settings);
    }

    @Test
    public void artifactsReturnedInJunitFormatOnlyForMatchingArtifacts() throws Exception {

        when(mockRestTemplate.exchange(argThat(is(equalTo(new URI("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml")))), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JunitXmlReport.class)))
                .thenReturn(ResponseEntity.ok().body(new JunitXmlReport()));

        JenkinsJob job = JenkinsJob.newBuilder()
                                   .jobName("myJob")
                                   .url("http://myBuildServer/myJob")
                                   .job(JenkinsJob.newBuilder().jobName("wibble").build())
                                   .lastSuccessfulBuild(JenkinsBuild.newBuilder()
                                       .artifact(Artifact.newBuilder()
                                           .fileName("TEST-someSuite-test.xml")
                                           .path("mysubmodule/TEST-someSuite-test.xml")
                                           .build())
                                       .artifact(Artifact.newBuilder()
                                           .fileName("this_does_not_match")
                                           .path("mysubmodule/this_does_not_match")
                                           .build())
                                       .build()).build();

        List<JunitXmlReport> xmlReports = testee.getLatestArtifacts(JunitXmlReport.class, job, Pattern.compile("TEST-.*-test\\.xml"));

        verify(mockRestTemplate).exchange(argThat(is(equalTo(new URI("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml")))), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JunitXmlReport.class));
        assertThat(xmlReports).size().isEqualTo(1);
    }

    @Test
    public void getJobsShouldReturnAListOfJobs() throws Exception {
        when(mockRestTemplate.exchange(eq(new URI("http://buildserver1/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]],jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]],jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]]]")), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JobContainer.class)))
                .thenReturn(ResponseEntity.ok()
                    .body(JobContainer.newBuilder()
                        .job(JenkinsJob.newBuilder()
                            .jobName("job1")
                            .url("http://buildserver1/job1")
                            .build())
                        .build()));
        when(mockRestTemplate.exchange(eq(new URI("http://buildserver2/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]],jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]],jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]]]")), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JobContainer.class)))
                .thenReturn(ResponseEntity.ok()
                    .body(JobContainer.newBuilder()
                        .job(JenkinsJob.newBuilder()
                            .jobName("job1")
                            .url("http://buildserver2/job1")
                            .build())
                        .build()));

        final List<JenkinsJob> jobs = testee.getJobs(Arrays.asList("http://buildserver1", "http://buildserver2"));

        verify(mockRestTemplate).exchange(eq(new URI("http://buildserver1/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]],jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]],jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]]]")), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JobContainer.class));
        verify(mockRestTemplate).exchange(eq(new URI("http://buildserver2/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]],jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]],jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]]]")), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JobContainer.class));
        assertThat(jobs).isNotNull();
        assertThat(jobs).hasSize(2);
        assertThat(jobs).extracting("url")
                        .containsExactlyInAnyOrder("http://buildserver1/job1", "http://buildserver2/job1");
    }

    @Test
    public void createsSecurityHeaderIfSetForJobs() throws Exception {
        JenkinsSettings settings = new JenkinsSettings();
        settings.setJobDepth(1);
        settings.setApiKey("api_Key");
        settings.setUsername("username");
        HttpJenkinsClient localTestee = new HttpJenkinsClient(mockRestTemplate, settings);

        when(mockRestTemplate.exchange(eq(new URI("http://buildserver1/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]")), eq(HttpMethod.GET), any(HttpEntity.class), eq(JobContainer.class)))
                .thenReturn(ResponseEntity.ok()
                    .body(JobContainer.newBuilder()
                        .job(JenkinsJob.newBuilder()
                            .jobName("job1")
                            .url("http://buildserver1/job1")
                            .build())
                        .build()));

        //test
        localTestee.getJobs(Arrays.asList("http://buildserver1"));

        //verify
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(mockRestTemplate).exchange(eq(new URI("http://buildserver1/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]")), eq(HttpMethod.GET), entityCaptor
                .capture(), eq(JobContainer.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        byte[] encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64("username:api_Key".getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        assertThat(capturedEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0)).isEqualTo(authHeader);
    }

    @Test
    public void createsSecurityHeaderIfSetForArtifacts() throws Exception {
        JenkinsSettings settings = new JenkinsSettings();
        settings.setApiKey("apiKey");
        settings.setUsername("name");
        settings.setJobDepth(1);
        HttpJenkinsClient localTestee = new HttpJenkinsClient(mockRestTemplate, settings);

        when(mockRestTemplate.exchange(argThat(is(equalTo(new URI("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml")))), eq(HttpMethod.GET), any(HttpEntity.class), eq(JobContainer.class)))
                .thenReturn(ResponseEntity.ok().body(JobContainer.newBuilder().build()));


        //test
        final JenkinsJob jenkinsJob = JenkinsJob.newBuilder()
                                                .url("http://myBuildServer/myJob")
                                                .lastSuccessfulBuild(JenkinsBuild.newBuilder()
                                                    .artifact(Artifact.newBuilder()
                                                        .fileName("TEST-someSuite-test.xml")
                                                        .path("mysubmodule/TEST-someSuite-test.xml")
                                                        .build())
                                                    .build())
                                                .build();
        localTestee.getLatestArtifacts(JobContainer.class, jenkinsJob, Pattern.compile("TEST-.*-test\\.xml"));

        //verify
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(mockRestTemplate).exchange(eq(new URI("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml")), eq(HttpMethod.GET), entityCaptor
                .capture(), eq(JobContainer.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        byte[] encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64("name:apiKey".getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        assertThat(capturedEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0)).isEqualTo(authHeader);
    }

    @Test
    public void swallowsMalformedURLsInArtefacts() throws Exception {
        JenkinsSettings settings = new JenkinsSettings();
        settings.setApiKey("apiKey");
        settings.setUsername("name");
        settings.setJobDepth(1);
        HttpJenkinsClient localTestee = new HttpJenkinsClient(mockRestTemplate, settings);

        //test
        final JenkinsJob jenkinsJob = JenkinsJob.newBuilder()
            .url("://myBuildServer/myJob")
            .lastSuccessfulBuild(JenkinsBuild.newBuilder()
                .artifact(Artifact.newBuilder()
                    .fileName("TEST-someSuite-test.xml")
                    .path("mysubmodule/TEST-someSuite-test.xml")
                    .build())
                .build())
            .build();
        localTestee.getLatestArtifacts(JobContainer.class, jenkinsJob, Pattern.compile("TEST-.*-test\\.xml"));

    }

    @Test
    public void swallowsMalformedURLsInJobs() throws Exception {
        this.testee.getJobs(Arrays.asList("://buildserver1"));

    }

}