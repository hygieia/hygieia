package co.leantechniques.hygieia.rally;

import co.leantechniques.hygieia.rally.domain.HierarchyRequirement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class RallyClient {
    private RallyClient(URI rallyUri, String userName, String password) {
        //noinspection deprecation
        this(new RallyRestApi(rallyUri, userName, password));
    }

    public static RallyClient create(String rallyUri, String username, String password) {
        URI uri;
        try {
            uri = new URI(rallyUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new RallyClient(uri, username, password);
    }

    public RallyClient(RallyRestApi restApi) {
        this.restApi = restApi;
    }

    private RallyRestApi restApi;


    public List<HierarchyRequirement> getHierarchicalRequirements() {
        JsonArray jsonArray = getHierarchicalRequirementAsJson();
        return Arrays.asList(new Gson().fromJson(jsonArray, HierarchyRequirement[].class));
    }

    JsonArray getHierarchicalRequirementAsJson() {
        QueryRequest defects = new QueryRequest("hierarchicalrequirement");
        defects.setFetch(new Fetch("FormattedID", "Name", "ScheduleState", "PlanEstimate", "Iteration", "Owner", "Feature", "Release"));
//            defects.setQueryFilter(new QueryFilter("State", "<", "Fixed"));
        defects.setQueryFilter(new QueryFilter("ScheduleState", "=", "Accepted").and(new QueryFilter("FormattedID", "=", "US8683")));
        defects.setOrder("FormattedID ASC");
        //Return up to 5, 1 per page
//        defects.setPageSize(1);
//        defects.setLimit(5);
        String herdingCatsId = "https://rally1.rallydev.com/slm/webservice/v2.0/project/23831544379";
        defects.setProject(herdingCatsId);
        defects.setScopedUp(false);
        defects.setScopedDown(true);

        try {
            QueryResponse queryResponse = restApi.query(defects);
            if (!queryResponse.wasSuccessful()) {
                throw new RallyServiceException(queryResponse.getErrors());
            }
            return queryResponse.getResults();
        } catch (IOException e) {
            throw new RallyServiceException(e);
        }
    }
}
