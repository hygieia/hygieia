package jenkins.plugins.hygieia;

import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Set;

public interface HygieiaService {
    HygieiaResponse publishBuildData(BuildDataCreateRequest request);

    HygieiaResponse publishArtifactData(BinaryArtifactCreateRequest request);

    boolean testConnection();

    HygieiaResponse publishTestResults(TestDataCreateRequest request);

    HygieiaResponse publishSonarResults(CodeQualityCreateRequest request);

    HygieiaResponse publishDeployData(DeployDataCreateRequest request);

    List<JSONObject> getCollectorItemOptions(String type);

    Set<String> getDeploymentEnvironments(String appName);
}
