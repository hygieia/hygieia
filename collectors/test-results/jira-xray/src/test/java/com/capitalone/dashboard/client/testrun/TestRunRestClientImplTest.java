package com.capitalone.dashboard.client.testrun;

import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.client.api.domain.TestRun;
import com.capitalone.dashboard.client.core.json.TestArrayJsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TestRunRestClientImpl.class)
public class TestRunRestClientImplTest {
    private final String TEST_EXEC_KEY="EME-4944";
    private final String TEST_KEY="EME-1683";
    private final long TEST_ID=507571;

    @Mock
    private DisposableHttpClient httpClient;
    @Mock
    private Promise pr;

    @Before
    public final void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestRun testRun = new TestRun(URI.create(""), "EME-1683", 507571L, TestRun.Status.PASS, null, null, "jqm884", "jqm884", null, null, null, null, null);
        PowerMockito.when(pr.claim()).thenReturn(testRun);
    }
    @Test
    public void getTestRunsByTestExecKeyAndTestKey() throws Exception {

        TestRunRestClientImpl mock = PowerMockito.spy(new TestRunRestClientImpl(URI.create(""),httpClient));
        PowerMockito.doReturn(pr).when(mock,"getAndParse",Matchers.anyObject(),Matchers.any(TestArrayJsonParser.class));
        Promise<TestRun> testruns = mock.getTestRun(TEST_EXEC_KEY, TEST_KEY);
        Assert.assertNotNull(testruns.claim());
        Assert.assertEquals("EME-1683", testruns.claim().getKey());

    }
    @Test
    public void  getTestRunByTestRunId()throws Exception{
        TestRunRestClientImpl mock = PowerMockito.spy(new TestRunRestClientImpl(URI.create(""),httpClient));
        PowerMockito.doReturn(pr).when(mock,"getAndParse",Matchers.any(URI.class),Matchers.any(TestArrayJsonParser.class));
        Promise<TestRun> testruns = mock.getTestRun(TEST_ID);
        Assert.assertNotNull(testruns.claim());
        Assert.assertEquals("EME-1683", testruns.claim().getKey());
    }

}