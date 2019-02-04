//package com.capitalone.dashboard.client.testexecution;
//
//import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
//import com.atlassian.util.concurrent.Promise;
//import com.capitalone.dashboard.TestResultSettings;
//import com.capitalone.dashboard.api.*;
//import com.capitalone.dashboard.api.domain.TestExecution;
//import com.capitalone.dashboard.core.client.JiraXRayRestClientImpl;
//import com.capitalone.dashboard.core.client.JiraXRayRestClientSupplier;
//import com.capitalone.dashboard.core.client.TestExecutionRestClientImpl;
//import com.capitalone.dashboard.core.client.TestRunRestClientImpl;
//import com.capitalone.dashboard.core.client.testexecution.TestExecutionClient;
//import com.capitalone.dashboard.core.client.testexecution.TestExecutionClientImpl;
//import com.capitalone.dashboard.core.json.TestArrayJsonParser;
//import com.capitalone.dashboard.model.CollectorItem;
//import com.capitalone.dashboard.model.Feature;
//import com.capitalone.dashboard.repository.CollectorItemRepository;
//import com.capitalone.dashboard.repository.FeatureRepository;
//import com.capitalone.dashboard.repository.TestResultCollectorRepository;
//import com.capitalone.dashboard.repository.TestResultRepository;
//import org.bson.types.ObjectId;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Matchers;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//import org.powermock.modules.junit4.PowerMockRunnerDelegate;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.atlassian.util.concurrent.Promises.when;
//import static org.mockito.Matchers.any;
//
//class JiraXRayRestClientTest implements  JiraXRayRestClient {
//    public TestRestClient getTestClient() {
//        return null;
//    }
//    public TestExecutionRestClientImpl getTestExecutionClient() {
//        return new TestExecutionRestClient();
//    }
//    public TestRunRestClient getTestRunClient() {
//        return new TestRunRestClient();
//    }
//    public TestSetRestClient getTestSetClient() {
//        return null;
//    }
//
//}
//
//@RunWith(MockitoJUnitRunner.class)
//public class TestExecutionClientImplTest {
//
//    private TestResultSettings testResultSettings;
//    @Mock
//    private TestResultRepository testResultRepository;
//    @Mock
//    private TestResultCollectorRepository testResultCollectorRepository;
//    @Mock
//    private FeatureRepository featureRepository;
//    @Autowired
//    private CollectorItemRepository collectorItemRepository;
//
//    private JiraXRayRestClient restClient = new JiraXRayRestClientTest();
//    @Mock
//    private JiraXRayRestClientSupplier restClientSupplier;
//    @Mock
//    private DisposableHttpClient httpClient;
//    @Mock
//    private Promise pr;
//    @Mock
//    private TestExecution testExecution;
//    @Mock
//    private TestExecutionRestClient testExecutionRestClient;
//    @Mock
//    private TestExecutionRestClientImpl testExecutionRestClientImpl;
//
//    TestExecutionClientImpl testExecutionClientimpl;
//
//    @Before
//    public final void init(){
//        testResultSettings = new TestResultSettings();
//        testExecutionClientimpl = new TestExecutionClientImpl(testResultRepository, testResultCollectorRepository, featureRepository, collectorItemRepository, testResultSettings, restClientSupplier);
//        testResultSettings.setPageSize(20);
//        //Mockito.when(pr.claim()).thenReturn(createTests());
//        //restClient = new JiraXRayRestClientImpl(URI.create(""), httpClient);
//        //testExecutionRestClient = restClient.getTestExecutionClient();
//        //testExecutionRestClientImpl = new TestExecutionRestClientImpl(URI.create(""), httpClient);
//    }
//
//    @Test
//    public void updateInformation() throws Exception {
//        TestExecution testExecution = new TestExecution(URI.create(""), "ABC13", 12357L);
//        Mockito.when(restClient.getTestExecutionClient().getTests(any()).claim()).thenReturn(createTests());
//        Mockito.when(featureRepository.getStoryByType("Test Execution")).thenReturn(createFeature());
//
//
//        int cnt = testExecutionClientimpl.updateTestResultInformation();
//
//
//    }
//
//    private List<Feature> createFeature() {
//        List<Feature> features = new ArrayList<>();
//        Feature feature1 = new Feature();
//        //feature1.setsTeamID("503");
//        feature1.setsName("summary1001");
//        feature1.setsProjectName("Hygieia");
//        feature1.setsTypeName("Test Execution");
//        feature1.setsNumber("ABC123");
//        feature1.setsUrl("http://myurl.com");
//        feature1.setsId("123");
//        feature1.setsProjectName("Hygieia");
//        features.add(feature1);
//        return features;
//    }
//
//    private Iterable<TestExecution.Test> createTests(){
//        Iterable<TestExecution.Test> tests = new ArrayList<>();
//        TestExecution.Test test1 = new TestExecution.Test(URI.create("http://URL.com"), "DEF567", 12345L);
//        TestExecution.Test test2 = new TestExecution.Test(URI.create("http://myurl.com"), "FOX123", 78901L);
//        ((ArrayList<TestExecution.Test>) tests).add(test1);
//        ((ArrayList<TestExecution.Test>) tests).add(test2);
//
//
//        return tests;
//    }
//
//
//
//}