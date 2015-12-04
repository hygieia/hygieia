package co.leantechniques.hygieia.rally.collector;

import co.leantechniques.hygieia.rally.RallyClient;
import co.leantechniques.hygieia.rally.repository.FeatureCollectorRepository;
import co.leantechniques.hygieia.rally.util.RallySettings;
import com.capitalone.dashboard.collector.CollectorTask;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

public class RallyCollectorTask extends CollectorTask<RallyFeatureCollector> {
    private static final Log LOG = LogFactory.getLog(RallyCollectorTask.class);
    private final RallySettings rallySettings;
    FeatureCollectorRepository featureCollectorRepository;
    private final FeatureRepository featureRepository;
    private RallyClient rallyClient;

    @Autowired
    protected RallyCollectorTask(TaskScheduler taskScheduler,
                                 RallySettings rallySettings,
                                 FeatureCollectorRepository featureCollectorRepository, FeatureRepository featureRepository,
                                 RallyClient rallyClient) {
        super(taskScheduler, "Rally");
        this.rallySettings = rallySettings;
        this.featureCollectorRepository = featureCollectorRepository;
        this.featureRepository = featureRepository;
        this.rallyClient = rallyClient;
    }

    @Override
    public RallyFeatureCollector getCollector() {
        RallyFeatureCollector prototype = new RallyFeatureCollector();
        prototype.setCollectorType(CollectorType.Feature);
        prototype.setName("Rally");
        prototype.setEnabled(true);
        prototype.setOnline(true);
        return prototype;
    }

    @Override
    public BaseCollectorRepository<RallyFeatureCollector> getCollectorRepository() {
        return featureCollectorRepository;
    }

    @Override
    public String getCron() {
        return rallySettings.getCron();
    }

    @Override
    public void collect(RallyFeatureCollector collector) {
        ObjectId rallyCollectorId = featureCollectorRepository.findByName("Rally").getId();
        new RallyFeatureImporter(rallyCollectorId, featureRepository, rallySettings, rallyClient).execute();
//        featureRepository.getFeatureMaxChangeDate(rallyCollectorId, )
//        featureRepository.save(new Feature());
//        repository.findByCollectorType()
    }

}
