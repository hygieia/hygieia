package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CucumberJsonToTestResultTransformerTest {
    CucumberJsonToTestResultTransformer transformer = new CucumberJsonToTestResultTransformer();

    @Test
    public void testTransform() throws Exception {
        String json = getJson("two-features.json");

        Iterable<TestSuite> suites = transformer.transformer(json);
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

        //TODO get data with two feature files

        /*
        suite = suiteIt.next();
        testCaseIt = suite.getTestCases().iterator();
        assertSuite(suite, "Failing", 1, 0, 0, 1, 860l);
        assertTestCase(testCaseIt.next(), "a failing step", 860l, TestCaseStatus.Failure);
        assertThat(testCaseIt.hasNext(), is(false));

        suite = suiteIt.next();
        testCaseIt = suite.getTestCases().iterator();
        assertSuite(suite, "I say hi", 0, 0, 0, 1, 2002476l);
        assertTestCase(testCaseIt.next(), "You said hi", 2002476l, TestCaseStatus.Success);
        assertThat(testCaseIt.hasNext(), is(false));

        suite = suiteIt.next();
        testCaseIt = suite.getTestCases().iterator();
        assertSuite(suite, "You say hi", 0, 0, 0, 1, 3001381l);
        assertTestCase(testCaseIt.next(), "Something happened", 3001381l, TestCaseStatus.Success);
        assertThat(testCaseIt.hasNext(), is(false));

        suite = suiteIt.next();
        testCaseIt = suite.getTestCases().iterator();
        assertSuite(suite, "Eating Cucumbers", 0, 0, 0, 0, 0l);
        assertTestCase(testCaseIt.next(), "there are <start> cucumbers", 0l, TestCaseStatus.Unknown);
        assertTestCase(testCaseIt.next(), "I eat <eat> cucumbers", 0l, TestCaseStatus.Unknown);
        assertTestCase(testCaseIt.next(), "I should have <left> cucumbers", 0l, TestCaseStatus.Unknown);
        assertThat(testCaseIt.hasNext(), is(false));

        assertThat(suiteIt.hasNext(), is(false));
        */
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

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = CucumberJsonToTestResultTransformerTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}
