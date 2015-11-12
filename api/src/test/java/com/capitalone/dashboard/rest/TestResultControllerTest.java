package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.request.TestResultRequest;
import com.capitalone.dashboard.service.TestResultService;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class TestResultControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired private TestResultService testResultService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testSuites() throws Exception {
        TestResult testResult = makeTestResult();
        Iterable<TestResult> results = Arrays.asList(testResult);
        DataResponse<Iterable<TestResult>> response = new DataResponse<>(results, 1);
        TestSuite testSuite = testResult.getTestSuites().iterator().next();
        TestCase testCase = testSuite.getTestCases().iterator().next();

        when(testResultService.search(Mockito.any(TestResultRequest.class))).thenReturn(response);

        mockMvc.perform(get("/quality/test?componentId=" + ObjectId.get()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$result", hasSize(1)))
                .andExpect(jsonPath("$result[0].id", is(testResult.getId().toString())))
                .andExpect(jsonPath("$result[0].collectorItemId", is(testResult.getCollectorItemId().toString())))
                .andExpect(jsonPath("$result[0].timestamp", is(intVal(testResult.getTimestamp()))))
                .andExpect(jsonPath("$result[0].executionId", is(testResult.getExecutionId())))
                .andExpect(jsonPath("$result[0].description", is(testResult.getDescription())))
                .andExpect(jsonPath("$result[0].url", is(testResult.getUrl())))
                .andExpect(jsonPath("$result[0].startTime", is(intVal(testResult.getStartTime()))))
                .andExpect(jsonPath("$result[0].endTime", is(intVal(testResult.getEndTime()))))
                .andExpect(jsonPath("$result[0].duration", is(intVal(testResult.getDuration()))))
                .andExpect(jsonPath("$result[0].failureCount", is(intVal(testResult.getFailureCount()))))
                .andExpect(jsonPath("$result[0].errorCount", is(intVal(testResult.getErrorCount()))))
                .andExpect(jsonPath("$result[0].skippedCount", is(intVal(testResult.getSkippedCount()))))
                .andExpect(jsonPath("$result[0].totalCount", is(intVal(testResult.getTotalCount()))))
                .andExpect(jsonPath("$result[0].testSuites", hasSize(1)))

                .andExpect(jsonPath("$result[0].testSuites[0].description", is(testSuite.getDescription())))
                .andExpect(jsonPath("$result[0].testSuites[0].type", is(testSuite.getType().toString())))
                .andExpect(jsonPath("$result[0].testSuites[0].startTime", is(intVal(testResult.getStartTime()))))
                .andExpect(jsonPath("$result[0].testSuites[0].endTime", is(intVal(testResult.getEndTime()))))
                .andExpect(jsonPath("$result[0].testSuites[0].duration", is(intVal(testResult.getDuration()))))
                .andExpect(jsonPath("$result[0].testSuites[0].failureCount", is(intVal(testResult.getFailureCount()))))
                .andExpect(jsonPath("$result[0].testSuites[0].errorCount", is(intVal(testResult.getErrorCount()))))
                .andExpect(jsonPath("$result[0].testSuites[0].skippedCount", is(intVal(testResult.getSkippedCount()))))
                .andExpect(jsonPath("$result[0].testSuites[0].totalCount", is(intVal(testResult.getTotalCount()))))

                .andExpect(jsonPath("$result[0].testSuites[0].testCases", hasSize(1)))
                .andExpect(jsonPath("$result[0].testSuites[0].testCases[0].id", is(testCase.getId())))
                .andExpect(jsonPath("$result[0].testSuites[0].testCases[0].description", is(testCase.getDescription())))
                .andExpect(jsonPath("$result[0].testSuites[0].testCases[0].duration", is(intVal(testCase.getDuration()))))
                .andExpect(jsonPath("$result[0].testSuites[0].testCases[0].status", is(testCase.getStatus().toString())));
    }

    private TestResult makeTestResult() {
        TestResult result = new TestResult();
        result.setId(ObjectId.get());
        result.setCollectorItemId(ObjectId.get());
        result.setDescription("description");
        result.setDuration(1l);
        result.setExecutionId("execution ID");
        result.setStartTime(2l);
        result.setEndTime(3l);
        result.setUrl("http://foo.com");
        result.setFailureCount(1);
        result.setErrorCount(2);
        result.setSkippedCount(0);
        result.setTotalCount(3);

        TestSuite suite = new TestSuite();
        suite.setDescription("description");
        suite.setDuration(1l);
        suite.setStartTime(2l);
        suite.setEndTime(3l);
        suite.setType(TestSuiteType.Functional);
        suite.setFailureCount(1);
        suite.setErrorCount(2);
        suite.setSkippedCount(0);
        suite.setTotalCount(3);

        result.getTestSuites().add(suite);

        TestCase testCase = new TestCase();
        testCase.setId("id");
        testCase.setDescription("description");
        testCase.setStatus(TestCaseStatus.Failure);
        testCase.setDuration(20l);

        suite.getTestCases().add(testCase);

        return result;
    }

    private int intVal(long value) {
        return Long.valueOf(value).intValue();
    }

}
