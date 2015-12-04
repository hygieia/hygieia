package co.leantechniques.hygieia.rally.collector;

import co.leantechniques.hygieia.rally.RallyClient;
import co.leantechniques.hygieia.rally.domain.HierarchyRequirement;
import co.leantechniques.hygieia.rally.util.RallySettings;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureRepository;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RallyFeatureImporterTest {

    private FeatureRepository featureRepo;
    private RallySettings settings;
    private RallyClient client;
    private RallyFeatureImporter importer;
    private RallyToFeatureMapper mapper;

    @Before
    public void before() {
        featureRepo = mock(FeatureRepository.class);
        mapper = mock(RallyToFeatureMapper.class);
        settings = new RallySettings();
        client = mock(RallyClient.class);
        importer = new RallyFeatureImporter(new ObjectId(), featureRepo, settings, client, mapper);
    }

    @Test
    public void shouldUpdateForEachRecord() throws Exception {
        HierarchyRequirement req1 = new HierarchyRequirement();
        HierarchyRequirement req2 = new HierarchyRequirement();
        List<HierarchyRequirement> rallyRequirements = Arrays.asList(req1, req2);
        when(mapper.map(rallyRequirements)).thenReturn(Arrays.asList(new Feature(), new Feature()));

        when(client.getHierarchicalRequirements()).thenReturn(rallyRequirements);

        importer.execute();

        verify(featureRepo).save(any(Iterable.class));
    }

}