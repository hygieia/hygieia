package com.capitalone.dashboard.client.testexecution;

import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.TestResultSettings;
import com.capitalone.dashboard.api.TestExecutionRestClient;
import com.capitalone.dashboard.api.TestRunRestClient;
import com.capitalone.dashboard.api.domain.TestExecution;
import com.capitalone.dashboard.api.domain.TestRun;
import com.capitalone.dashboard.api.domain.TestStep;
import com.capitalone.dashboard.core.client.JiraXRayRestClientImpl;
import com.capitalone.dashboard.core.client.JiraXRayRestClientSupplier;
import com.capitalone.dashboard.core.client.TestExecutionRestClientImpl;
import com.capitalone.dashboard.core.client.testexecution.TestExecutionClientImpl;
import com.capitalone.dashboard.core.json.util.RendereableItem;
import com.capitalone.dashboard.core.json.util.RendereableItemImpl;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.TestCaseStep;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultCollectorRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RunWith(PowerMockRunner.class)
@PrepareForTest(TestExecutionRestClientImpl.class)
public class TestExecutionClientImplTest {

    private TestResultSettings testResultSettings;
    @Mock
    private TestResultRepository testResultRepository;
    @Mock
    private TestResultCollectorRepository testResultCollectorRepository;
    @Mock
    private FeatureRepository featureRepository;
    @Mock
    private CollectorItemRepository collectorItemRepository;
    @Mock
    private DisposableHttpClient httpClient;
    @Mock
    private Promise pr;
    @Mock
    private Promise pr1;
    @Captor ArgumentCaptor<List<TestResult>> captor;

    @InjectMocks
    TestExecutionClientImpl testExecutionClientimpl;

    @Before
    public final void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        testResultSettings = new TestResultSettings();
        JiraXRayRestClientSupplier restClientSupplierMock = Mockito.mock(JiraXRayRestClientSupplier.class);
        JiraXRayRestClientImpl restClientMock =  Mockito.spy(new JiraXRayRestClientImpl(URI.create(""),httpClient));
        Mockito.when(restClientSupplierMock.get()).thenReturn(restClientMock);
        Mockito.when(restClientMock.getTestExecutionClient()).thenReturn(new TestExecutionRestClient() {
            @Override
            public Promise<Iterable<TestExecution.Test>> getTests(TestExecution key) {
                return pr;
            }

            @Override
            public Promise<Void> setTests(TestExecution testExec) {
                return null;
            }

            @Override
            public Promise<Void> removeTest(TestExecution testExecKey, TestExecution.Test testKey) {
                return null;
            }
        });
        Mockito.when(pr.claim()).thenReturn(createTests());
        Mockito.when(restClientMock.getTestRunClient()).thenReturn(new TestRunRestClient() {
            @Override
            public Promise<TestRun> getTestRun(String testExecKey, String testKey) {
                return pr1;
            }

            @Override
            public Promise<TestRun> getTestRun(Long testRunId) {
                return null;
            }

            @Override
            public Promise<Void> updateTestRun(TestRun testRunInput) {
                return null;
            }

            @Override
            public Promise<Iterable<TestRun>> getTestRuns(String testKey) {
                return null;
            }

            @Override
            public Promise<TestRun.Status> getStatus(Long testRunId) {
                return null;
            }
        });
        Mockito.when(pr1.claim()).thenReturn(createTestRuns());
        testExecutionClientimpl = new TestExecutionClientImpl(testResultRepository, testResultCollectorRepository, featureRepository, collectorItemRepository, testResultSettings, restClientSupplierMock);
        testResultSettings.setPageSize(20);
    }

    @Test
    public void updateMongoTestResultInformation(){

        Mockito.when(featureRepository.getStoryByType("Test Execution")).thenReturn(createFeature());
        int cnt = testExecutionClientimpl.updateTestResultInformation();
        Assert.assertEquals(1, cnt);
        Mockito.verify(testResultRepository, Mockito.times(1)).save(captor.capture());
        TestResult testResult1 = captor.getAllValues().get(0).get(0);
        Assert.assertEquals(null, testResult1.getCollectorItemId());
        Assert.assertEquals("summary1001", testResult1.getDescription());
        Assert.assertEquals("Hygieia",testResult1.getTargetAppName() );
        Assert.assertEquals(TestSuiteType.Manual,testResult1.getType() );
        Collection<TestCapability> testCapabilities = testResult1.getTestCapabilities();

        for(TestCapability testCapability:testCapabilities) {
            Assert.assertEquals("summary1001", testCapability.getDescription());
            Assert.assertEquals(1,testCapability.getTotalTestSuiteCount() );
            Assert.assertEquals(TestSuiteType.Manual,testCapability.getType() );
            Assert.assertEquals(TestCaseStatus.Success, testCapability.getStatus());
            Collection<TestSuite> testSuites = testCapability.getTestSuites();

            for(TestSuite testSuite : testSuites){
                Assert.assertEquals("summary1001", testSuite.getDescription());
                Assert.assertEquals(TestSuiteType.Manual, testSuite.getType());
                Assert.assertEquals(0, testSuite.getFailedTestCaseCount());
                Assert.assertEquals(2, testSuite.getSuccessTestCaseCount());
                Assert.assertEquals(2, testSuite.getTotalTestCaseCount());
                Assert.assertEquals(0, testSuite.getSkippedTestCaseCount());
                Assert.assertEquals(TestCaseStatus.Success, testSuite.getStatus());
                Collection<TestCase> testCases = testSuite.getTestCases();
                for(TestCase testCase : testCases){
                    Assert.assertEquals("3456", testCase.getId());
                    Assert.assertEquals("DEF678","DEF678" );
                    Assert.assertEquals(1, testCase.getTotalTestStepCount());
                    Assert.assertEquals(1, testCase.getSuccessTestStepCount());
                    Assert.assertEquals(TestCaseStatus.Success, testCase.getStatus());
                    Collection<TestCaseStep> testCaseSteps = testCase.getTestSteps();
                    for(TestCaseStep step : testCaseSteps){
                        Assert.assertEquals("1234", step.getId());
                        Assert.assertEquals("hello", step.getDescription());
                        Assert.assertEquals(TestCaseStatus.Success, step.getStatus());
                    }
                }
            }
        }
    }

    private List<Feature> createFeature() {
        List<Feature> features = new ArrayList<>();
        Feature feature1 = new Feature();
        //feature1.setsTeamID("503");
        feature1.setsName("summary1001");
        feature1.setsProjectName("Hygieia");
        feature1.setsTypeName("Test Execution");
        feature1.setsNumber("CAB1985");
        feature1.setsUrl("http://myurl.com");
        feature1.setsId("123");
        feature1.setsProjectName("Hygieia");
        features.add(feature1);
        return features;
    }

    private TestRun createTestRuns(){
        Iterable<TestStep> testSteps = new ArrayList<>();
        RendereableItem rendereableItem = new RendereableItemImpl("hello", "");

        TestStep testStep = new TestStep(URI.create(""), "DEF678", 1234L, 1, rendereableItem, null, null, TestStep.Status.PASS);
        ((ArrayList<TestStep>) testSteps).add(testStep);
        TestRun testRun = new TestRun(URI.create("myurl.com"), "Abc123", 3456L, TestRun.Status.PASS, null, null, null, null, testSteps);
        return testRun;
    }

    private Iterable<TestExecution.Test> createTests(){
        Iterable<TestExecution.Test> tests = new ArrayList<>();
        TestExecution.Test test1 = new TestExecution.Test(URI.create("http://URL.com"), "DEF567", 12345L);
        TestExecution.Test test2 = new TestExecution.Test(URI.create("http://myurl.com"), "FOX123", 78901L);
        ((ArrayList<TestExecution.Test>) tests).add(test1);
        ((ArrayList<TestExecution.Test>) tests).add(test2);


        return tests;
    }




}