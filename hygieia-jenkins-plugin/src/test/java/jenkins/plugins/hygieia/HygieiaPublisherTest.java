package jenkins.plugins.hygieia;

import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import junit.framework.TestCase;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RunWith(Parameterized.class)
public class HygieiaPublisherTest extends TestCase {

    private HygieiaPublisherStub.DescriptorImplStub descriptor;
    private HygieiaServiceStub hygieiaServiceStub;
    private boolean responseBoolean;
    private HygieiaResponse hygieiaResponse;
    private FormValidation.Kind expectedResult;

    @Before
    @Override
    public void setUp() {
        descriptor = new HygieiaPublisherStub.DescriptorImplStub();
    }

    public HygieiaPublisherTest(HygieiaServiceStub hygieiaServiceStub, boolean responseBoolean, FormValidation.Kind expectedResult) {
        this.hygieiaServiceStub = hygieiaServiceStub;
        this.responseBoolean = responseBoolean;
//        this.responseString = responseString;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection businessTypeKeys() {
        return Arrays.asList(new Object[][]{
                {new HygieiaServiceStub(), true, FormValidation.Kind.OK},
                {new HygieiaServiceStub(), false, FormValidation.Kind.ERROR},
                {null, false, FormValidation.Kind.ERROR}
        });
    }

    @Test
    public void testDoTestConnection() throws Exception {
        if (hygieiaServiceStub != null) {
            hygieiaServiceStub.setResponse(responseBoolean);
            hygieiaServiceStub.setHygieiaResponse(hygieiaResponse);
        }
        descriptor.setHygieiaService(hygieiaServiceStub);
        try {
            FormValidation result = descriptor.doTestConnection("hygieaUrl", "authToken", "myname", "true");
            assertEquals(result.kind, expectedResult);
        } catch (Descriptor.FormException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public static class HygieiaServiceStub implements HygieiaService {

        private boolean responseBoolean;
        private HygieiaResponse hygieiaResponse;


        public void setResponse(boolean response) {
            this.responseBoolean = response;
        }

        public HygieiaResponse getHygieiaResponse() {
            return hygieiaResponse;
        }

        public void setHygieiaResponse(HygieiaResponse hygieiaResponse) {
            this.hygieiaResponse = hygieiaResponse;
        }

        public HygieiaResponse publishBuildData(BuildDataCreateRequest request) {
            return hygieiaResponse;
        }

        public HygieiaResponse publishArtifactData(BinaryArtifactCreateRequest request) {
            return hygieiaResponse;
        }

        public boolean testConnection() {
            return responseBoolean;
        }

        public HygieiaResponse publishTestResults(TestDataCreateRequest request) {
            return hygieiaResponse;
        }

        public HygieiaResponse publishSonarResults(CodeQualityCreateRequest request) {
            return hygieiaResponse;
        }

        public HygieiaResponse publishDeployData(DeployDataCreateRequest request) {
            return hygieiaResponse;
        }

        public List<JSONObject> getCollectorItemOptions(String type) {
            return null;
        }

        public Set<String> getDeploymentEnvironments(String appName) {
            return null;
        }

    }
}
