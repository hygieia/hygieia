package co.leantechniques.hygieia.rally.collector;

import co.leantechniques.hygieia.rally.util.RallySettings;
import com.capitalone.dashboard.collector.CollectorTask;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

public class RallyFeatureCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(RallyFeatureCollectorTask.class);
    private final RallySettings rallySettings;

    @Autowired
    protected RallyFeatureCollectorTask(TaskScheduler taskScheduler, RallySettings rallySettings) {
        super(taskScheduler, "Rally");
        this.rallySettings = rallySettings;
    }

    @Override
    public Collector getCollector() {
        Collector prototype = new Collector();
        prototype.setCollectorType(CollectorType.Feature);
        prototype.setName("Rally");
        prototype.setEnabled(true);
        prototype.setOnline(true);
        return prototype;
    }

    @Override
    public BaseCollectorRepository<Collector> getCollectorRepository() {
        return null;
    }

    @Override
    public String getCron() {
        return rallySettings.getCron();
    }

    @Override
    public void collect(Collector collector) {

    }
}
