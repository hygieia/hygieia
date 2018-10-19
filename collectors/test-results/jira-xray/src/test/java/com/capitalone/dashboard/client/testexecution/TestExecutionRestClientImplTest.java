package com.capitalone.dashboard.client.testexecution;

import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.client.api.domain.TestExecution;
import com.capitalone.dashboard.client.api.domain.TestRun;
import com.capitalone.dashboard.client.core.json.TestArrayJsonParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TestExecutionRestClientImpl.class)
public class TestExecutionRestClientImplTest {

    @Mock
    private TestExecution testExecution;
    @Mock
    private DisposableHttpClient httpClient;
    @Mock
    private Promise pr;


    @Before
    public final void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestExecution.Test test =  new TestExecution.Test(URI.create(""),"EA-3403",28775L,1,TestRun.Status.PASS);
        PowerMockito.when(pr.claim()).thenReturn(test);

    }
    @Test
    public void getTests() throws Exception{
        testExecution = new TestExecution(URI.create(""), "EME-4644", 1977l);
        TestExecutionRestClientImpl mock = PowerMockito.spy(new TestExecutionRestClientImpl(URI.create(""),httpClient));
        PowerMockito.doReturn(pr).when(mock,"getAndParse",Matchers.any(URI.class),Matchers.any(TestArrayJsonParser.class));
        Promise<Iterable<TestExecution.Test>> testResult= mock.getTests(testExecution);
        Assert.assertNotNull(testResult.claim());
    }

    @Test
    public void get() throws Exception {
        testExecution = new TestExecution(URI.create(""), "EME-4644", 1977l);
        TestExecution.Test test =  new TestExecution.Test(URI.create(""),"EA-3403",28775L,1,TestRun.Status.PASS);
        try {
            TestExecutionRestClientImpl mock = PowerMockito.spy(new TestExecutionRestClientImpl(URI.create(""),httpClient));
            Promise<Iterable<TestExecution>> testResult= mock.get(test);
            Assert.assertNotNull(testResult);
        }catch (Exception e){

        }
    }





}