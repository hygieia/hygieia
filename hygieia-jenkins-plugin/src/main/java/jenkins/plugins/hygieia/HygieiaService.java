package jenkins.plugins.hygieia;

import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import com.capitalone.dashboard.request.MetadataCreateRequest;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Set;

public interface HygieiaService {
    HygieiaResponse publishBuildData(BuildDataCreateRequest request);

    HygieiaResponse publishBuildDataV3(BuildDataCreateRequest request);

    HygieiaResponse publishArtifactData(BinaryArtifactCreateRequest request);

    boolean testConnection();

    HygieiaResponse publishTestResults(TestDataCreateRequest request);

    HygieiaResponse publishSonarResults(CodeQualityCreateRequest request);

    HygieiaResponse publishDeployData(DeployDataCreateRequest request);

    HygieiaResponse publishGenericCollectorItemData(GenericCollectorItemCreateRequest request);

    HygieiaResponse publishGenericArtifactData(GenericCollectorItemCreateRequest request);

    HygieiaResponse publishMetaData(MetadataCreateRequest request);


    List<JSONObject> getCollectorItemOptions(String type);

    Set<String> getDeploymentEnvironments(String appName);

    RestCall.RestCallResponse getStageResponse(String url, String jenkinsUser, String token);
}
