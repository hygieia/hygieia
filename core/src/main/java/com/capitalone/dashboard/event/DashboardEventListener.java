package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

/**
 * Listens for Dashboard lifecycle events to create and delete Product collector CollectorItems
 * whenever a Team Dashboard is created or deleted.
 */
@Component
public class DashboardEventListener extends AbstractMongoEventListener<Dashboard> {

    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;

    @Autowired
    public DashboardEventListener(CollectorRepository collectorRepository,
                                  CollectorItemRepository collectorItemRepository) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
    }

    /**
     * Creates a collector item for new team dashboards
     * @param event
     */
    @Override
    public void onAfterSave(AfterSaveEvent<Dashboard> event) {
        Dashboard dashboard = event.getSource();
        // Ignore product dashboards
        if (DashboardType.Product.equals(dashboard.getType())) {
            return;
        }

        Collector productCollector = getProductCollector();
        CollectorItem item = getDashboardCollectorItem(dashboard.getId().toString(), productCollector.getId());

        if (item == null) {
            // Create a new Collector Item
            item = new CollectorItem();
            item.setCollectorId(productCollector.getId());
            item.setDescription(dashboard.getTitle());
            item.getOptions().put("dashboardId", dashboard.getId().toString());
        } else {
            // Update the title of the existing Collector Item in case it changed
            item.setDescription(dashboard.getTitle());
        }

        collectorItemRepository.save(item);
    }

    /**
     * Removes the collector item for deleted dashboards
     * @param event
     */
    @Override
    public void onAfterDelete(AfterDeleteEvent<Dashboard> event) {
        DBObject dbo = event.getDBObject();
        String dashboardId = dbo.get("id").toString();

        CollectorItem item = getDashboardCollectorItem(dashboardId, getProductCollector().getId());
        if (item != null) {
            collectorItemRepository.delete(item);
        }
    }

    private CollectorItem getDashboardCollectorItem(String dashboardId, ObjectId id) {
        return collectorItemRepository.findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(
                id, dashboardId);
    }

    private Collector getProductCollector() {
        return collectorRepository.findByName("Product");
    }
}
