package hygieia.transformer;

import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.quality.CucumberJsonReport;
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

/**
 * Created by stevegal on 2019-03-25.
 */
public class TestResultVisitorTest {

    private BuildDataCreateRequest mockBuidDataCreateRequest;
    private TestResultVisitor sut;

    private CucumberJsonReport cucumberTestReport;
    private MochaJsSpecReport mochaTestReport;

    @Before
    public void setup() throws Exception{

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        cucumberTestReport = mapper.readValue(this.getClass().getResource("/cucumber.json"), CucumberJsonReport.class);
        mochaTestReport = mapper.readValue(this.getClass().getResource("/mochjsspec.json"), MochaJsSpecReport.class);
        mockBuidDataCreateRequest = mock(BuildDataCreateRequest.class);
        sut = new TestResultVisitor("functional",mockBuidDataCreateRequest);
    }

    @Test
    public void convertsCucumber() {
        sut.visit(cucumberTestReport);

        TestResult report = sut.produceResult();

        assertThat(report.getTotalCount(),is(equalTo(1)));
    }

    @Test
    public void convertsMocha() {
        sut.visit(mochaTestReport);

        TestResult report = sut.produceResult();

        assertThat(report.getTotalCount(),is(equalTo(1)));
    }

    @Test
    public void combinesResultsFromMultiples() {

        sut.visit(cucumberTestReport);
        sut.visit(mochaTestReport);

        TestResult report = sut.produceResult();

        assertThat(report.getTotalCount(),is(equalTo(2)));

    }

}