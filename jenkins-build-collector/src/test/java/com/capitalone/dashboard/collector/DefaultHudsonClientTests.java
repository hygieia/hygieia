package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.HudsonJob;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultHudsonClientTests {

    @Mock private Supplier<RestOperations> restOperationsSupplier;
    @Mock private RestOperations rest;
    private HudsonClient hudsonClient;

    private static final String URL = "URL";

    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        hudsonClient = new DefaultHudsonClient(restOperationsSupplier, new HudsonSettings());
    }

    @Test
    public void instanceJobs_emptyResponse_returnsEmptyMap() {
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>("", HttpStatus.OK));

        Map<HudsonJob, Set<Build>> jobs = hudsonClient.getInstanceJobs(URL);

        assertThat(jobs.size(), is(0));
    }

    @Test
    public void instanceJobs_twoJobsTwoBuilds() throws Exception {
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(getJson("instanceJobs_twoJobsTwoBuilds.json"), HttpStatus.OK));

        Map<HudsonJob, Set<Build>> jobs = hudsonClient.getInstanceJobs(URL);

        assertThat(jobs.size(), is(2));
        Iterator<HudsonJob> jobIt = jobs.keySet().iterator();

        //First job
        HudsonJob job = jobIt.next();
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
    public void buildDetails_full() throws Exception {
        when(rest.exchange(Matchers.any(URI.class), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>(getJson("buildDetails_full.json"), HttpStatus.OK));

        Build build = hudsonClient.getBuildDetails(URL);

        assertThat(build.getTimestamp(), notNullValue());
        assertThat(build.getNumber(), is("2483"));
        assertThat(build.getBuildUrl(), is(URL));
        assertThat(build.getArtifactVersionNumber(), nullValue());
        assertThat(build.getStartTime(), is(1421281415000L));
        assertThat(build.getEndTime(), is(1421284113495L));
        assertThat(build.getDuration(), is(2698495L));
        assertThat(build.getBuildStatus(), is(BuildStatus.Failure));
        assertThat(build.getStartedBy(), is("ab"));
        assertThat(build.getSourceChangeSet().size(), is(2));

        // ChangeSet 1
        SCM scm = build.getSourceChangeSet().get(0);
        assertThat(scm.getScmUrl(), is("http://svn.apache.org/repos/asf/lucene/dev/branches/branch_5x"));
        assertThat(scm.getScmRevisionNumber(), is("1651902"));
        assertThat(scm.getScmCommitLog(), is("Merged revision(s) 1651901 from lucene/dev/trunk:\nLUCENE-6177: fix typo"));
        assertThat(scm.getScmAuthor(), is("uschindler"));
        assertThat(scm.getScmCommitTimestamp(), notNullValue());
        assertThat(scm.getNumberOfChanges(), is(4L));

        // ChangeSet 2
        scm = build.getSourceChangeSet().get(1);
        assertThat(scm.getScmUrl(), nullValue());
        assertThat(scm.getScmRevisionNumber(), is("1651896"));
        assertThat(scm.getScmCommitLog(), is("SOLR-6900: bin/post improvements including glob handling, spaces in file names, and improved help output (merged from trunk r1651895)"));
        assertThat(scm.getScmAuthor(), is("ehatcher"));
        assertThat(scm.getScmCommitTimestamp(), notNullValue());
        assertThat(scm.getNumberOfChanges(), is(5L));
    }

    private void assertBuild(Build build, String number, String url) {
        assertThat(build.getNumber(), is(number));
        assertThat(build.getBuildUrl(), is(url));
    }

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultHudsonClientTests.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }

    private void assertJob(HudsonJob job, String name, String url) {
        assertThat(job.getJobName(), is(name));
        assertThat(job.getJobUrl(), is(url));
    }
}
