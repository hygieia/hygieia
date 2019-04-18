
package com.capitalone.dashboard.core.client;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousSearchRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.api.TestRunRestClient;
import com.capitalone.dashboard.api.domain.TestRun;
import com.capitalone.dashboard.core.PluginConstants;
import com.capitalone.dashboard.core.json.TestRunJsonParser;
import com.capitalone.dashboard.core.json.gen.TestRunUpdateJsonGenerator;
import com.google.common.base.Function;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;


/**
 * This is the implementation class for TestRunRestClient
 */

public class TestRunRestClientImpl extends AbstractAsynchronousRestClient implements TestRunRestClient {
    private URI baseUri;
    private final TestRunJsonParser testRunParser=new TestRunJsonParser();
    private final TestRunUpdateJsonGenerator testRunUpdateJsonGenerator=new TestRunUpdateJsonGenerator();
    private SearchRestClient searchRestClient=null;

    protected TestRunRestClientImpl(HttpClient client) {
        super(client);
    }

    public TestRunRestClientImpl(URI serverUri, DisposableHttpClient httpClient){
        super(httpClient);
        searchRestClient=new AsynchronousSearchRestClient(UriBuilder.fromUri(serverUri).path("rest/api/latest/").build(),httpClient);
        baseUri = UriBuilder.fromUri(serverUri).path("/rest/raven/{restVersion}/api/").build(PluginConstants.XRAY_REST_VERSION);
    }


    /**
     * Gets test run using testExecKey & testKey
     *
     * @param testExecKey
     * @param testKey
     * @return
     */

    public Promise<TestRun> getTestRun(String testExecKey, String testKey) {
        UriBuilder uriBuilder=UriBuilder.fromUri(baseUri);
        uriBuilder.path("testrun").queryParam("testExecIssueKey",testExecKey).queryParam("testIssueKey",testKey);
        return this.getAndParse(uriBuilder.build(),this.testRunParser);
    }

    /**
     * Gets test run with testRunId
     *
     * @param testRunId
     * @return
     */

    public Promise<TestRun> getTestRun(Long testRunId) {
        UriBuilder uriBuilder=UriBuilder.fromUri(baseUri);
        uriBuilder.path("testrun").path("{id}");
        return this.getAndParse(uriBuilder.build(testRunId),this.testRunParser);
    }


    /**
     * Rest-API call to the /testrun/ with params to updates it's contents.
     *
     * @param testRunInput
     * @return
     */

    public Promise<Void> updateTestRun(TestRun testRunInput) {
        UriBuilder uriBuilder=UriBuilder.fromUri(baseUri);
        uriBuilder.path("testrun").path("{id}");

        return this.putAndParse(uriBuilder.build(testRunInput.getId()), testRunInput,testRunUpdateJsonGenerator, new JsonObjectParser<Void>() {
            public Void parse(JSONObject jsonObject) throws JSONException {
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

    @Override
    public Promise<TestRun.Status> getStatus(Long testRunId) {
        return null;
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

}

