package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.cloudwatch.model.AwsLogCollectorItem;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by stevegal on 20/06/2018.
 */
public interface AwsLogCollectorItemRepository extends BaseCollectorItemRepository<AwsLogCollectorItem>{

    List<AwsLogCollectorItem> findByCollectorIdAndDescription(ObjectId collectorId, String description);
}
