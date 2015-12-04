package co.leantechniques.hygieia.rally;

import co.leantechniques.hygieia.rally.collector.RallyToFeatureMapper;
import co.leantechniques.hygieia.rally.domain.HierarchyRequirement;
import com.capitalone.dashboard.model.Feature;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RallyToFeatureMapperTest {

    @Test
    public void mapsListOfHeirarchyRequirements() throws Exception {
        HierarchyRequirement req1 = new HierarchyRequirement();
        HierarchyRequirement req2 = new HierarchyRequirement();

        List<Feature> actual = new RallyToFeatureMapper().map(Arrays.asList(req1, req2));
        assertThat(actual.size(), is(2));
    }

    @Test
    public void mapsRallyHeirarchyRequirementToHygieiaFeature() throws Exception {
        Feature mappedFeature = new Feature();
        HierarchyRequirement hierarchyRequirement = new HierarchyRequirement();

        mappedFeature.setChangeDate(null);
        mappedFeature.setCollectorId(null);
        mappedFeature.setIsDeleted(null);

        mappedFeature.setsEpicIsDeleted(null);
        mappedFeature.setsEpicAssetState(null);
        mappedFeature.setsEpicBeginDate(null);
        mappedFeature.setsEpicChangeDate(null);
        mappedFeature.setsEpicEndDate(null);
        mappedFeature.setsEpicHPSMReleaseID(null);
        mappedFeature.setsEpicID(null);
        mappedFeature.setsEpicName(null);
        mappedFeature.setsEpicNumber(null);
        mappedFeature.setsEpicPDD(null);
        mappedFeature.setsEpicType(null);

        mappedFeature.setsEstimate(hierarchyRequirement.getPlanEstimate());
        mappedFeature.setsId(hierarchyRequirement.getFormattedID());
        mappedFeature.setsName(hierarchyRequirement.getName());
        mappedFeature.setsNumber(null);

//        mappedFeature.setsOwnersChangeDate(null);
//        mappedFeature.setsOwnersFullName(null);
//        mappedFeature.setsOwnersID(null);
        mappedFeature.setsProjectBeginDate(null);
        mappedFeature.setsProjectChangeDate(null);
        mappedFeature.setsProjectEndDate(null);
        mappedFeature.setsProjectID(null);
        mappedFeature.setsProjectIsDeleted(null);
        mappedFeature.setsProjectName(null);
        mappedFeature.setsProjectPath(null);
        mappedFeature.setsProjectState(null);

        mappedFeature.setsState(hierarchyRequirement.getScheduleState());
        mappedFeature.setsStatus(null);

        mappedFeature.setsSprintAssetState(null);
        mappedFeature.setsSprintBeginDate(null);
        mappedFeature.setsSprintChangeDate(null);
        mappedFeature.setsSprintEndDate(null);
        mappedFeature.setsSprintID(null);
        mappedFeature.setsSprintIsDeleted(null);
        mappedFeature.setsSprintName(null);

        mappedFeature.setsTeamAssetState(null);
        mappedFeature.setsTeamChangeDate(null);
        mappedFeature.setsTeamID(null);
        mappedFeature.setsTeamIsDeleted(null);
        mappedFeature.setsTeamName(null);

    }
}
