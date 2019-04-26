package hygieia.transformer;

import com.capitalone.dashboard.model.quality.CucumberJsonReport;
import com.capitalone.dashboard.model.quality.MochaJsSpecReport;
import com.capitalone.dashboard.model.quality.QualityVisitee;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Created by stevegal on 2019-03-25.
 */
public class CodeQualityVisiteeDeserializerTest {

    private ObjectMapper mapper;

    @Before
    public void setup(){

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(QualityVisitee.class, new QualityVisiteeDeserializer());
        mapper.registerModule(module);
    }

    @Test
    public void deserializesCucumber() throws Exception {
        QualityVisitee cucumberTestReport = mapper.readValue(this.getClass().getResource("/cucumber.json"), QualityVisitee.class);

        assertThat(cucumberTestReport,is(instanceOf(CucumberJsonReport.class)));
    }

    @Test
    public void deserializesMocha() throws Exception {
        QualityVisitee cucumberTestReport = mapper.readValue(this.getClass().getResource("/mochjsspec.json"), QualityVisitee.class);

        assertThat(cucumberTestReport,is(instanceOf(MochaJsSpecReport.class)));
    }

}