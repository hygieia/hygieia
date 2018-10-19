package com.capitalone.dashboard.client.testrun;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousSearchRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.client.api.domain.TestRun;
import com.capitalone.dashboard.client.api.domain.Defect;
import com.capitalone.dashboard.client.api.domain.Comment;
import com.capitalone.dashboard.client.api.domain.Evidence;
import com.capitalone.dashboard.client.api.domain.Example;
import com.capitalone.dashboard.client.api.domain.TestStep;
import com.google.common.base.Function;
import com.capitalone.dashboard.client.core.PluginConstants;
import com.capitalone.dashboard.client.core.json.StatusJsonParser;
import com.capitalone.dashboard.client.core.json.TestRunJsonParser;
import com.capitalone.dashboard.client.core.json.gen.TestRunUpdateJsonGenerator;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;

/**
 * Implementation for Test Run Rest Client
 */
public class TestRunRestClientImpl extends AbstractAsynchronousRestClient implements TestRunRestClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunRestClientImpl.class);

    private URI baseUri;
    private final TestRunJsonParser testRunParser=new TestRunJsonParser();
    private final TestRunUpdateJsonGenerator testRunUpdateJsonGenerator=new TestRunUpdateJsonGenerator();
    private final StatusJsonParser  statusParser=new StatusJsonParser();
    private SearchRestClient searchRestClient=null;

    protected TestRunRestClientImpl(HttpClient client) {
        super(client);
    }

    public TestRunRestClientImpl(URI serverUri, DisposableHttpClient httpClient){
        super(httpClient);
        searchRestClient=new AsynchronousSearchRestClient(UriBuilder.fromUri(serverUri).path("rest/api/latest/").build(new Object[0]),httpClient);
        baseUri = UriBuilder.fromUri(serverUri).path("/rest/raven/{restVersion}/api/").build(PluginConstants.XRAY_REST_VERSION);
    }

    /**
     *
     * @param testExecKey
     * @param testKey
     * @return
     */
    public Promise<TestRun> getTestRun(String testExecKey, String testKey) {
        UriBuilder uriBuilder= UriBuilder.fromUri(baseUri);
        uriBuilder.path("testrun").queryParam("testExecIssueKey",testExecKey).queryParam("testIssueKey",testKey);
        return this.getAndParse(uriBuilder.build(new Object[0]),this.testRunParser);
    }

    /**
     *
     * @param testRunId
     * @return
     */
    public Promise<TestRun> getTestRun(Long testRunId) {
        UriBuilder uriBuilder= UriBuilder.fromUri(baseUri);
        uriBuilder.path("testrun").path("{id}");
        return this.getAndParse(uriBuilder.build(testRunId),this.testRunParser);
    }

    /**
     * Rest-API call to the /testrun/ with params to updates it's contents.
     * @param testRunInput
     * @return
     */
    public Promise<Void> updateTestRun(TestRun testRunInput) {
        UriBuilder uriBuilder= UriBuilder.fromUri(baseUri);
        uriBuilder.path("testrun").path("{id}");
        LOGGER.info(uriBuilder.build(testRunInput.getId()).toString());
        try {
            LOGGER.info(testRunUpdateJsonGenerator.generate(testRunInput).toString());
        } catch (JSONException e) {
            LOGGER.info("JSON Exception:" + e);
        }
        return this.putAndParse(uriBuilder.build(testRunInput.getId()), testRunInput,testRunUpdateJsonGenerator, new JsonObjectParser<Void>() {
            public Void parse(JSONObject jsonObject) throws JSONException {
                LOGGER.info("CALLING PARSE ON UPDATE");
                return null;
            }
        });
    }

    /**
     * Query the testRuns using the "testTestExecutions" JQL defined by the XRAY plugin in JIRA.
     * @param testKey Issue jira key for the test.
     * @return a list of XRAY test-runs in which the Test identified by test-key is involved in
     */
    //TODO: MOVE THIS METHOD TO A BOUNDARY
    public Promise<Iterable<TestRun>> getTestRuns(final String testKey) {
        Promise<SearchResult> searchResultPromise= searchRestClient.searchJql("issue in testTestExecutions(\""+testKey+"\") ");
        return searchResultPromise.map(new Function<SearchResult,Iterable<TestRun>>(){
            public Iterable<TestRun> apply(@Nullable SearchResult searchResult) {
                ArrayList<TestRun> testRunsList=new ArrayList<TestRun>();
                for(Issue issue: searchResult.getIssues() ){
                    TestRun testRun=getTestRun(issue.getKey(),testKey).claim();
                    testRun.setTestExecKey(issue.getKey());
                    testRunsList.add(testRun);
                }
                return testRunsList;
            }
        });
    }

    /**
     * Rest-API call to the /{testrun_id}/status return not json response so crash
     * http://jira.xpand-addons.com/browse/XRAY-964.
     * @param testRunId Internal xray id for the TestRun
     * @return Status from the test run
     */
    public Promise<TestRun.Status> getStatus(Long testRunId) {
        UriBuilder uriBuilder= UriBuilder.fromUri(baseUri);
        uriBuilder.path("testrun").path("{id}").path("/status/");
        return this.getAndParse(uriBuilder.build(testRunId),statusParser);
    }

    public Promise<TestRun.Status> updateStatus(Long testRunId, TestRun.Status statusInput) {
        UriBuilder uriBuilder= UriBuilder.fromUri(baseUri);
        uriBuilder.path("testrun").path("{id}").path("/status/").queryParam("status",statusInput.name());
       throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }

    /**
     * Rest-API call to the /testrun? with params because the default api rest-call doesn't work on json format.
     * @param testExecKey Key from the test execution
     * @param testKey Key from the test which is involved in this test run
     * @return The status from test run
     */
    public Promise<TestRun.Status> getStatus(String testExecKey, String testKey) {
        return this.getTestRun(testExecKey,testKey).map(new Function<TestRun, TestRun.Status>() {
            public TestRun.Status apply(@Nullable TestRun testRun) {
                return testRun.getStatus();
            }
        });
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Defect> addDefect(String issueKey, Defect defect) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }

    //TODO: ADD IMPLEMENTATION
    public Promise<Iterable<Defect>> getDefects(Long testRunId) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Void> removeDefect(Long testRunId, String issueKey) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Iterable<Evidence>> getEvidences(Long testRunId) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Evidence> createEvidence(Long testRunId, Evidence newEvidence) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Void> removeEvidence(Long testRunId, String resourceName) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Void> removeEvidence(Long testRunId, Long evId) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Comment> getComment(Long testRunId) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Comment> updateComment(Long testRunId, String comment) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Example> getExample(Long testRunId) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
    //TODO: ADD IMPLEMENTATION
    public Promise<Iterable<TestStep>> getTestSteps(Long testRunId) {
        throw new IllegalArgumentException("NOT IMPLEMENTED YET");
    }
}
