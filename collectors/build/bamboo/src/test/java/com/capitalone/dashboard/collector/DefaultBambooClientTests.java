package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.BambooJob;
import com.capitalone.dashboard.model.SCM;
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
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBambooClientTests {

    @Mock private Supplier<RestOperations> restOperationsSupplier;
    @Mock private RestOperations rest;
    private BambooSettings settings;
    private BambooClient bambooClient;
    private DefaultBambooClient defaultBambooClient;

    private static final String URL_TEST = "http://server/job/job2/2/";

    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new BambooSettings();
        bambooClient = defaultBambooClient = new DefaultBambooClient(restOperationsSupplier,
                settings);
    }

    @Test
    public void joinURLsTest() throws Exception {
        String u = DefaultBambooClient.joinURL("http://bamboo.com",
                "/api/json?tree=jobs[name,url,builds[number,url]]");
        assertEquals("http://bamboo.com/api/json?tree=jobs[name,url,builds[number,url]]", u);

        String u4 = DefaultBambooClient.joinURL("http://bamboo.com/", "test",
                "/api/json?tree=jobs[name,url,builds[number,url]]");
        assertEquals("http://bamboo.com/test/api/json?tree=jobs[name,url,builds[number,url]]", u4);

        String u2 = DefaultBambooClient.joinURL("http://bamboo.com/", "/test/",
                "/api/json?tree=jobs[name,url,builds[number,url]]");
        assertEquals("http://bamboo.com/test/api/json?tree=jobs[name,url,builds[number,url]]", u2);

        String u3 = DefaultBambooClient.joinURL("http://bamboo.com", "///test",
                "/api/json?tree=jobs[name,url,builds[number,url]]");
        assertEquals("http://bamboo.com/test/api/json?tree=jobs[name,url,builds[number,url]]", u3);
    }

    @Test
    public void rebuildURLTest() throws Exception {

        String u1 = DefaultBambooClient.rebuildJobUrl("http://bamboo.com/job/job1", "https://123456:234567@bamboo.com");
        assertEquals("https://123456:234567@bamboo.com/job/job1", u1);

        String u2 = DefaultBambooClient.rebuildJobUrl("https://bamboo.com/job/job1", "https://123456:234567@bamboo.com");
        assertEquals("https://123456:234567@bamboo.com/job/job1", u2);

        String u3 = DefaultBambooClient.rebuildJobUrl("http://bamboo.com/job/job1", "http://123456:234567@bamboo.com");
        assertEquals("http://123456:234567@bamboo.com/job/job1", u3);

        String u4 = DefaultBambooClient.rebuildJobUrl("http://bamboo.com/job/job1", "http://123456:234567@bamboo.com");
        assertEquals("http://123456:234567@bamboo.com/job/job1", u4);

        String orig = "http://bamboo.com/job/job1%20with%20space";
        String u5 = DefaultBambooClient.rebuildJobUrl(orig, "http://bamboo.com");
        assertEquals(orig, u5);
    }

    @Test
    public void verifyBasicAuth() throws Exception {
        @SuppressWarnings("unused")
        URL u = new URL(new URL("http://bamboo.com"), "/api/json?tree=jobs[name,url," +
                "builds[number,url]]");

        HttpHeaders headers = defaultBambooClient.createHeaders("Aladdin:open sesame");
        assertEquals("Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void verifyAuthCredentials() throws Exception {
        //TODO: This change to clear a JAVA Warning should be correct but test fails, need to investigate
        //HttpEntity<HttpHeaders> headers = new HttpEntity<HttpHeaders>(defaultBambooClient.createHeaders("user:pass"));
        @SuppressWarnings({ "rawtypes", "unchecked" })
        HttpEntity headers = new HttpEntity(defaultBambooClient.createHeaders("user:pass"));
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        settings.setApiKey("doesnt");
        settings.setUsername("matter");
        defaultBambooClient.makeRestCall("http://user:pass@bamboo.com");
        verify(rest).exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class));
    }

    @Test
    public void verifyAuthCredentialsBySettings() throws Exception {
        //TODO: This change to clear a JAVA Warnings should be correct but test fails, need to investigate
        //HttpEntity<HttpHeaders> headers = new HttpEntity<HttpHeaders>(defaultBambooClient.createHeaders("does:matter"));
        @SuppressWarnings({ "unchecked", "rawtypes" })
        HttpEntity headers = new HttpEntity(defaultBambooClient.createHeaders("does:matter"));
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        settings.setApiKey("matter");
        settings.setUsername("does");
        defaultBambooClient.makeRestCall("http://bamboo.com");
        verify(rest).exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class));
    }

    @Test
    public void verifyGetLogUrl() throws Exception {
        //TODO: This change should be correct but test fails, need to investigate
        //HttpEntity<HttpHeaders> headers = new HttpEntity<HttpHeaders>(defaultBambooClient.createHeaders("does:matter"));
        @SuppressWarnings({ "unchecked", "rawtypes" })
        HttpEntity headers = new HttpEntity(defaultBambooClient.createHeaders("does:matter"));
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET),
                eq(headers), eq(String.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        settings.setApiKey("matter");
        settings.setUsername("does");
        defaultBambooClient.getLog("http://bamboo.com");
        verify(rest).exchange(eq(URI.create("http://bamboo.com/consoleText")), eq(HttpMethod.GET),
                eq(headers), eq(String.class));
    }

    @Test
    public void instanceJobs_emptyResponse_returnsEmptyMap() {
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"plans\":{\"plan\":[]}}", HttpStatus.OK));

        Map<BambooJob, Set<Build>> jobs = bambooClient.getInstanceJobs(URL_TEST);

        assertThat(jobs.size(), is(0));
    }

    // @Test
    // public void instanceJobs_twoJobsTwoBuilds() throws Exception {
    //     when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
    //             .thenReturn(new ResponseEntity<>(getJson("instanceJobs_twoJobsTwoBuilds.json"), HttpStatus.OK));

    //     Map<BambooJob, Set<Build>> jobs = bambooClient.getInstanceJobs(URL_TEST);

    //     assertThat(jobs.size(), is(2));
    //     Iterator<BambooJob> jobIt = jobs.keySet().iterator();

    //     //First job
    //     BambooJob job = jobIt.next();
    //     assertJob(job, "job1", "http://server/job/job1/");

    //     Iterator<Build> buildIt = jobs.get(job).iterator();
    //     assertBuild(buildIt.next(),"2", "http://server/job/job1/2/");
    //     assertBuild(buildIt.next(),"1", "http://server/job/job1/1/");
    //     assertThat(buildIt.hasNext(), is(false));

    //     //Second job
    //     job = jobIt.next();
    //     assertJob(job, "job2", "http://server/job/job2/");

    //     buildIt = jobs.get(job).iterator();
    //     assertBuild(buildIt.next(),"2", "http://server/job/job2/2/");
    //     assertBuild(buildIt.next(),"1", "http://server/job/job2/1/");
    //     assertThat(buildIt.hasNext(), is(false));

    //     assertThat(jobIt.hasNext(), is(false));
    // }

    @Test
    public void buildDetails_full() throws Exception {
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(getJson("buildDetails_full.json"), HttpStatus.OK));

        Build build = bambooClient.getBuildDetails("http://server/job/job2/2/", "http://server");

        assertThat(build.getTimestamp(), notNullValue());
        assertThat(build.getNumber(), is("15"));
        assertThat(build.getBuildUrl(), is(URL_TEST));
        assertThat(build.getStartTime(), is(1472119510543L));
        assertThat(build.getEndTime(), is(1472119653736L));
        assertThat(build.getDuration(), is(143193L));
        assertThat(build.getBuildStatus(), is(BuildStatus.Success));
        assertThat(build.getSourceChangeSet().size(), is(3));

        // ChangeSet 1
        SCM scm = build.getSourceChangeSet().get(0);
        assertThat(scm.getScmUrl(), is("http://coderepo.com/projects/janina/repos/api/commits/dde5f6ce438ab6749a2873d75706bb27071576fa"));
        assertThat(scm.getScmRevisionNumber(), is("dde5f6ce438ab6749a2873d75706bb27071576fa"));
        assertThat(scm.getScmCommitLog(), is("add new value in sysparam.properties"));
        assertThat(scm.getScmAuthor(), is("something"));
        assertThat(scm.getScmCommitTimestamp(), notNullValue());
        assertThat(scm.getNumberOfChanges(), is(1L));

        // ChangeSet 2
        scm = build.getSourceChangeSet().get(1);
        assertThat(scm.getScmUrl(), is("http://coderepo.com/projects/janina/repos/api/commits/c3dd4c0d232b8b475da24815280d8bf09cdc449f"));
        assertThat(scm.getScmRevisionNumber(), is("c3dd4c0d232b8b475da24815280d8bf09cdc449f"));
        assertThat(scm.getScmCommitLog(), is("fix image path"));
        assertThat(scm.getScmAuthor(), is("Something"));
        assertThat(scm.getScmCommitTimestamp(), notNullValue());
        assertThat(scm.getNumberOfChanges(), is(4L));
    }

    private void assertBuild(Build build, String number, String url) {
        assertThat(build.getNumber(), is(number));
        assertThat(build.getBuildUrl(), is(url));
    }

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultBambooClientTests.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }

    private void assertJob(BambooJob job, String name, String url) {
        assertThat(job.getJobName(), is(name));
        assertThat(job.getJobUrl(), is(url));
    }
}
