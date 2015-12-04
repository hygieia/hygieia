package co.leantechniques.hygieia.rally.collector;

import co.leantechniques.hygieia.rally.domain.HierarchyRequirement;
import com.capitalone.dashboard.model.Feature;

import java.util.ArrayList;
import java.util.List;

public class RallyToFeatureMapper {
    public List<Feature> map(Iterable<HierarchyRequirement> hierarchyRequirements) {
        final ArrayList<Feature> features = new ArrayList<>();
        for (HierarchyRequirement req : hierarchyRequirements){
            features.add(new Feature());
        }
        return features;
    }
}
