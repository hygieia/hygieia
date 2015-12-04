package co.leantechniques.hygieia.rally;

import co.leantechniques.hygieia.rally.domain.HierarchyRequirement;
import com.google.gson.*;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Arrays;

public class RallyRestApiFake extends RallyRestApi {
    public RallyRestApiFake() {
        super(null, null);
    }

    @Override
    public QueryResponse query(QueryRequest request) throws IOException {
        return getExpectedQueryResponse();
    }

    private static QueryResponse getExpectedQueryResponse() throws IOException {
        String json = IOUtils.toString(RallyRestApiFake.class.getResourceAsStream("/data/hierarchicalRequirements.json"));
        return new QueryResponse(json);
    }

    public static HierarchyRequirement getExpectedRequirement() throws IOException {
        QueryResponse queryResponse = getExpectedQueryResponse();
//        return Arrays.asList(
                return new Gson().fromJson(queryResponse.getResults(), HierarchyRequirement[].class)[0];//).get(0);
    }


}
