package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.JenkinsJob;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultJenkinsClientTest {

    @Mock private Supplier<RestOperations> restOperationsSupplier;
//    @Mock private CucumberJsonToTestResultTransformer transformer;
    @Mock private RestOperations rest;

    private JenkinsSettings settings;
    private DefaultJenkinsClient defaultJenkinsClient;

    private static final String URL = "URL";

    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new JenkinsSettings();
        defaultJenkinsClient = new DefaultJenkinsClient(restOperationsSupplier, new CucumberJsonToTestResultTransformer(),
                settings);
    }

    @Test
    public void verifyBasicAuth() throws Exception {
        HttpHeaders headers = defaultJenkinsClient.createHeaders("Aladdin:open sesame");
        assertEquals("Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void verifyAuthCredentials() throws Exception {
        //TODO: This change to clear a JAVA Warning should be correct but test fails, need to investigate
        //HttpEntity<HttpHeaders> headers = new HttpEntity<HttpHeaders>(defaultJenkinsClient.createHeaders("user:pass"));
        @SuppressWarnings({ "rawtypes", "unchecked" })
        HttpEntity headers = new HttpEntity(defaultJenkinsClient.createHeaders("user:pass"));
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        settings.setApiKey("doesnt");
        settings.setUsername("matter");
        defaultJenkinsClient.makeRestCall("http://user:pass@jenkins.com");
        verify(rest).exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class));
    }

    @Test
    public void verifyAuthCredentialsBySettings() throws Exception {
        //TODO: This change to clear a JAVA Warning should be correct but test fails, need to investigate
        //HttpEntity<HttpHeaders> headers = new HttpEntity<HttpHeaders>(defaultJenkinsClient.createHeaders("user:pass"));
        @SuppressWarnings({ "rawtypes", "unchecked" })
        HttpEntity headers = new HttpEntity(defaultJenkinsClient.createHeaders("does:matter"));
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        settings.setApiKey("matter");
        settings.setUsername("does");
        defaultJenkinsClient.makeRestCall("http://jenkins.com");
        verify(rest).exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class));
    }


    @Test
    public void instanceJobs_emptyResponse_returnsEmptyMap() {
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>("", HttpStatus.OK));

        Map<JenkinsJob, Set<Build>> jobs = defaultJenkinsClient.getInstanceJobs(URL);

        assertThat(jobs.size(), is(0));
    }

    @Test
    public void instanceJobs_twoJobsTwoBuilds() throws Exception {
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(getJson("instanceJobs_twoJobsTwoBuilds.json"), HttpStatus.OK));

        Map<JenkinsJob, Set<Build>> jobs = defaultJenkinsClient.getInstanceJobs(URL);

        assertThat(jobs.size(), is(2));
        Iterator<JenkinsJob> jobIt = jobs.keySet().iterator();

        //First job
        JenkinsJob job = jobIt.next();
        assertJob(job, "job1", "http://server/job/job1/");

        Iterator<Build> buildIt = jobs.get(job).iterator();
        assertBuild(buildIt.next(),"2", "http://server/job/job1/2/");
        assertBuild(buildIt.next(),"1", "http://server/job/job1/1/");
        assertThat(buildIt.hasNext(), is(false));

        //Second job
        job = jobIt.next();
        assertJob(job, "job2", "http://server/job/job2/");

        buildIt = jobs.get(job).iterator();
        assertBuild(buildIt.next(),"2", "http://server/job/job2/2/");
        assertBuild(buildIt.next(),"1", "http://server/job/job2/1/");
        assertThat(buildIt.hasNext(), is(false));

        assertThat(jobIt.hasNext(), is(false));
    }


    @Test
    public void test_endToend () throws Exception {

        String artifacts = getJson("job-artifacts.json");
        String cucumberJson = getJson("two-features.json");

        URI lastBuildArtifactUri = URI.create("http://server/job/job1/lastSuccessfulBuild/api/json?tree=timestamp,duration,number,fullDisplayName,building,artifacts[fileName,relativePath]");
        URI cucumberJsonUri = URI.create("http://server/job/job1/lastSuccessfulBuild/artifact/job1/test1/report/web/cucumber.json");
        when(rest.exchange(eq(lastBuildArtifactUri), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(artifacts, HttpStatus.OK));
        when(rest.exchange(eq(cucumberJsonUri), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(cucumberJson, HttpStatus.OK));
        TestResult testResult = defaultJenkinsClient.getCucumberTestResult("http://server/job/job1/");
        Collection<TestCapability> capabilities = testResult.getTestCapabilities();
        assertThat(capabilities, notNullValue());

        Iterator<TestCapability> capabilityIterator = capabilities.iterator();

        TestCapability capability = capabilityIterator.next();
        List<TestSuite> suites = (List<TestSuite>) capability.getTestSuites();
        assertThat(suites, notNullValue());

        Iterator<TestSuite> suiteIt = suites.iterator();
        Iterator<TestCase> testCaseIt;
        TestSuite suite;

        suite = suiteIt.next();
        testCaseIt = suite.getTestCases().iterator();
        assertSuite(suite, "Feature:eCUKE Feature", 4, 0, 0, 4, 15019839l);

        assertTestCase(testCaseIt.next(), "ecuke-feature;i-say-hi", "Scenario:I say hi", 4001555l, TestCaseStatus.Success);
        assertThat(testCaseIt.hasNext(), is(true));

        assertTestCase(testCaseIt.next(), "ecuke-feature;you-say-hi", "Scenario:You say hi", 1001212l, TestCaseStatus.Success);
        assertThat(testCaseIt.hasNext(), is(true));

        assertTestCase(testCaseIt.next(), "ecuke-feature;eating-cucumbers", "Scenario Outline:Eating Cucumbers", 2013197l, TestCaseStatus.Success);
        assertThat(testCaseIt.hasNext(), is(true));

        assertTestCase(testCaseIt.next(), "ecuke-feature;eating-cucumbers", "Scenario Outline:Eating Cucumbers", 8003875l, TestCaseStatus.Success);
        assertThat(testCaseIt.hasNext(), is(false));
    }


    private void assertSuite(TestSuite suite, String desc, int success, int fail, int skip, int total, long duration) {
        assertThat(suite.getType(), is(TestSuiteType.Functional));
        assertThat(suite.getDescription(), is(desc));
        assertThat(suite.getFailedTestCaseCount(), is(fail));
        assertThat(suite.getSuccessTestCaseCount(), is(success));
        assertThat(suite.getSkippedTestCaseCount(), is(skip));
        assertThat(suite.getTotalTestCaseCount(), is(total));
        assertThat(suite.getDuration(), is(duration));
        assertThat(suite.getStartTime(), is(0l));
        assertThat(suite.getEndTime(), is(0l));
    }

    private void assertTestCase(TestCase tc, String id, String name, long duration, TestCaseStatus status) {
        assertThat(tc.getId(), is(id));
        assertThat(tc.getDescription(), is(name));
        assertThat(tc.getDuration(), is(duration));
        assertThat(tc.getStatus(), is(status));
    }

    private void assertBuild(Build build, String number, String url) {
        assertThat(build.getNumber(), is(number));
        assertThat(build.getBuildUrl(), is(url));
    }

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultJenkinsClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }

    private void assertJob(JenkinsJob job, String name, String url) {
        assertThat(job.getJobName(), is(name));
        assertThat(job.getJobUrl(), is(url));
    }
}