package co.leantechniques.hygieia.rally.collector;

import co.leantechniques.hygieia.rally.RallyClient;
import co.leantechniques.hygieia.rally.domain.HierarchyRequirement;
import co.leantechniques.hygieia.rally.util.RallySettings;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureRepository;
import org.bson.types.ObjectId;

import java.util.List;

public class RallyFeatureImporter {
    private final ObjectId rallyCollectorId;
    private final FeatureRepository featureRepository;
    private final RallySettings rallySettings;
    private final RallyClient rallyClient;
    private RallyToFeatureMapper mapper;

    public RallyFeatureImporter(ObjectId rallyCollectorId, FeatureRepository featureRepository, RallySettings rallySettings, RallyClient rallyClient) {
        this(rallyCollectorId, featureRepository, rallySettings, rallyClient, new RallyToFeatureMapper());
    }
    public RallyFeatureImporter(ObjectId rallyCollectorId, FeatureRepository featureRepository, RallySettings rallySettings, RallyClient rallyClient, RallyToFeatureMapper mapper) {
        this.rallyCollectorId = rallyCollectorId;
        this.featureRepository = featureRepository;
        this.rallySettings = rallySettings;
        this.rallyClient = rallyClient;
        this.mapper = mapper;
    }
    public void execute() {
        List<HierarchyRequirement> requirements = rallyClient.getHierarchicalRequirements();
        List<Feature> features = mapper.map(requirements);
        featureRepository.save(features);
    }
}
