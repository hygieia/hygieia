package co.leantechniques.hygieia.rally;

import co.leantechniques.hygieia.rally.domain.HierarchyRequirement;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RallyClientTest {

    private RallyClient rallyClient;

    @Before
    public void before(){
        rallyClient = new RallyClient(new RallyRestApiFake());
    }

    @Test
    public void getHeirarchicalRequirementsFromFake() throws Exception {
        HierarchyRequirement actual = rallyClient.getHierarchicalRequirements().get(0);

        HierarchyRequirement expected = RallyRestApiFake.getExpectedRequirement();
        assertThat(actual.getFormattedID(), is(expected.getFormattedID()));
        assertThat(actual.getName(), is(expected.getName()));
        assertThat(actual.getScheduleState(), is(expected.getScheduleState()));
        assertThat(actual.getPlanEstimate(), is(expected.getPlanEstimate()));
        assertThat(actual.getUuid(), is(expected.getUuid()));
   }
}