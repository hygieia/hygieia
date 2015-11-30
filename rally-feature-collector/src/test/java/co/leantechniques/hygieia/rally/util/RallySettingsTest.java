package co.leantechniques.hygieia.rally.util;

import co.leantechniques.hygieia.rally.Application;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource("/rally-feature-collector.properties")
public class RallySettingsTest {

    @Autowired
    RallySettings rallySettings;
    private String apiKey;

    @Before
    public void setUp() throws Exception {
        apiKey = System.getenv("RALLY_API_KEY");
    }

    @Test
    public void prefixIsRally() throws Exception {
        assertThat(rallySettings.getCron(), is("0 * * * * *"));
    }

    @Test
    public void getTeamInfo() throws Exception {
        assertThat("RALLY_API_KEY environment variable not set", apiKey, is(not(nullValue())));
    }

    @Test
    public void spikeRallyApi() throws Exception {
        RallyRestApi restApi = new RallyRestApi(new URI("https://rally1.rallydev.com"), apiKey);
        try {

            System.out.println("Querying for top 5 highest priority unfixed defects...");

            QueryRequest defects = new QueryRequest("hierarchicalrequirement");
            defects.setFetch(new Fetch("FormattedID", "Name", "ScheduleState"));
//            defects.setQueryFilter(new QueryFilter("State", "<", "Fixed"));
            defects.setQueryFilter(new QueryFilter("ScheduleState", "<", "Accepted"));
            defects.setOrder("FormattedID ASC");
            //Return up to 5, 1 per page
            defects.setPageSize(1);
            defects.setLimit(5);
            String herdingCatsId = "https://rally1.rallydev.com/slm/webservice/v2.0/project/23831544379";
            defects.setProject(herdingCatsId);
            defects.setScopedUp(false);
            defects.setScopedDown(true);

            QueryResponse queryResponse = restApi.query(defects);
            if (queryResponse.wasSuccessful()) {
                System.out.println(String.format("\nTotal results: %d", queryResponse.getTotalResultCount()));
                System.out.println("Top 5:");
                for (JsonElement result : queryResponse.getResults()) {
                    JsonObject defect = result.getAsJsonObject();
                    System.out.println(String.format("\t%s - %s: ScheduleState=%s",
                            defect.get("FormattedID").getAsString(),
                            defect.get("Name").getAsString(),
                            defect.get("ScheduleState").getAsString()));
                }
            } else {
                System.err.println("The following errors occurred: ");
                for (String err : queryResponse.getErrors()) {
                    System.err.println("\t" + err);
                }
            }

        } finally {
            //Release resources
            restApi.close();
        }
    }
}