package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.*;
import com.capitalone.dashboard.model.JunitXmlReport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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

/**
 * Created by plv163 on 12/10/2016.
 */
public class HttpJenkinsClientTest {

    private RestTemplate mockRestTemplate;

    private HttpJenkinsClient testee;

    @Before
    public void setup() {
        mockRestTemplate = mock(RestTemplate.class);
        JenkinsSettings settings = new JenkinsSettings();
        testee = new HttpJenkinsClient(mockRestTemplate, settings);
    }

    @Test
    public void artifactsReturnedInJunitFormatOnlyForMatchingArtifacts() {

        when(mockRestTemplate.exchange(argThat(is(equalTo("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml"))), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JunitXmlReport.class)))
                .thenReturn(ResponseEntity.ok().body(new JunitXmlReport()));

        JenkinsJob job = JenkinsJob.newBuilder()
                                   .jobName("myJob")
                                   .url("http://myBuildServer/myJob")
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

        verify(mockRestTemplate).exchange(argThat(is(equalTo("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml"))), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JunitXmlReport.class));
        assertThat(xmlReports).size().isEqualTo(1);
    }

    @Test
    public void getJobsShouldReturnAListOfJobs() {
        when(mockRestTemplate.exchange(eq("http://buildserver1/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]"), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JobContainer.class)))
                .thenReturn(ResponseEntity.ok()
                                          .body(JobContainer.newBuilder()
                                                            .job(JenkinsJob.newBuilder()
                                                                           .jobName("job1")
                                                                           .url("http://buildserver1/job1")
                                                                           .build())
                                                            .build()));
        when(mockRestTemplate.exchange(eq("http://buildserver2/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]"), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JobContainer.class)))
                .thenReturn(ResponseEntity.ok()
                                          .body(JobContainer.newBuilder()
                                                            .job(JenkinsJob.newBuilder()
                                                                           .jobName("job1")
                                                                           .url("http://buildserver2/job1")
                                                                           .build())
                                                            .build()));

        final List<JenkinsJob> jobs = testee.getJobs(Arrays.asList("http://buildserver1", "http://buildserver2"));

        verify(mockRestTemplate).exchange(eq("http://buildserver1/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]"), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JobContainer.class));
        verify(mockRestTemplate).exchange(eq("http://buildserver2/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]"), eq(HttpMethod.GET), isNull(HttpEntity.class), eq(JobContainer.class));
        assertThat(jobs).isNotNull();
        assertThat(jobs).hasSize(2);
        assertThat(jobs).extracting("url")
                        .containsExactlyInAnyOrder("http://buildserver1/job1", "http://buildserver2/job1");
    }

    @Test
    public void createsSecurityHeaderIfSetForJobs() {
        JenkinsSettings settings = new JenkinsSettings();
        settings.setApiKey("api_Key");
        settings.setUsername("username");
        HttpJenkinsClient localTestee = new HttpJenkinsClient(mockRestTemplate, settings);

        when(mockRestTemplate.exchange(eq("http://buildserver1/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]"), eq(HttpMethod.GET), any(HttpEntity.class), eq(JobContainer.class)))
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
        verify(mockRestTemplate).exchange(eq("http://buildserver1/api/json?tree=jobs[name,url,lastSuccessfulBuild[timestamp,artifacts[*]]]"), eq(HttpMethod.GET), entityCaptor
                .capture(), eq(JobContainer.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        byte[] encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64("username:api_Key".getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        assertThat(capturedEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0)).isEqualTo(authHeader);
    }

    @Test
    public void createsSecurityHeaderIfSetForArtifacts() {
        JenkinsSettings settings = new JenkinsSettings();
        settings.setApiKey("apiKey");
        settings.setUsername("name");
        HttpJenkinsClient localTestee = new HttpJenkinsClient(mockRestTemplate, settings);

        when(mockRestTemplate.exchange(argThat(is(equalTo("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml"))), eq(HttpMethod.GET), any(HttpEntity.class), eq(JobContainer.class)))
                .thenReturn(ResponseEntity.ok().body( JobContainer.newBuilder().build()));


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
        verify(mockRestTemplate).exchange(eq("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml"), eq(HttpMethod.GET), entityCaptor
                .capture(), eq(JobContainer.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        byte[] encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64("name:apiKey".getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        assertThat(capturedEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0)).isEqualTo(authHeader);
    }

}