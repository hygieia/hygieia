package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.Artifact;
import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.model.JunitXmlReport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
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
        testee = new HttpJenkinsClient(mockRestTemplate);
    }


    @Test
    public void artifactsReturnedInJunitFormatOnlyForMatchingArtifacts() {

        when(mockRestTemplate.getForEntity(any(String.class), eq(JunitXmlReport.class))).thenReturn(ResponseEntity.ok()
                                                                                                                  .body(new JunitXmlReport()));

        JenkinsJob job = JenkinsJob.newBuilder().jobName("myJob").jenkinsServer("http://myBuildServer/")
                .artifact(Artifact.newBuilder().artifactName("TEST-someSuite-test.xml").path("mysubmodule/TEST-someSuite-test.xml").build())
                .artifact(Artifact.newBuilder().artifactName("this_does_not_match").path("mysubmodule/this_does_not_match").build())
                .artifact(Artifact.newBuilder().artifactName("TEST-anotherSuite-test.xml").path("mysubmodule/TEST-anotherSuite-test.xml").build())
                .build();
        List<JunitXmlReport> xmlReports = testee.getLatestArtifacts(JunitXmlReport.class, job, Arrays.asList(Pattern.compile("TEST-.*-test\\.xml")));

        verify(mockRestTemplate).getForEntity(argThat(is(equalTo("http://myBuildServer/myJob/lastSuccessfulBuild/artifact/mysubmodule/TEST-someSuite-test.xml"))), eq(JunitXmlReport.class));
//        verifyNoMoreInteractions(mockRestTemplate);
        assertThat(xmlReports).size().isEqualTo(2);
    }

}