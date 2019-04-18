package hygieia.transformer;

import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestSuiteType;
import com.capitalone.dashboard.model.quality.MochaJsSpecReport;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by stevegal on 2019-03-25.
 */
public class MochaSpecToTestCapabilityTransformerTest {
    ObjectMapper mapper;

    MochaSpecToTestCapabilityTransformer sut;
    private BuildDataCreateRequest mockBuildDataRequest;

    @Before
    public void setup(){
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mockBuildDataRequest = mock(BuildDataCreateRequest.class);

        sut = new MochaSpecToTestCapabilityTransformer(mockBuildDataRequest,"testDescription");
    }

    @Test
    public void producesCucumberTestResult() throws Exception {

        when(mockBuildDataRequest.getNumber()).thenReturn("aBuildNumber");

        MochaJsSpecReport testReport = this.mapper.readValue(this.getClass().getResource("/mochjsspec.json"), MochaJsSpecReport.class);

        TestCapability capability = sut.convert(testReport);

        // 2 test suites
        assertThat(capability.getExecutionId(),is(equalTo("aBuildNumber")));
        assertThat(capability.getDescription(),is(equalTo("testDescription")));
        assertThat(capability.getType(), is(TestSuiteType.Functional));
        assertThat(capability.getSuccessTestSuiteCount(),is(equalTo(1)));
        assertThat(capability.getFailedTestSuiteCount(),is(equalTo(2)));
        assertThat(capability.getDuration(), is(equalTo(12L)));
        assertThat(capability.getTestSuites().size(), is(equalTo(3)));

    }
}