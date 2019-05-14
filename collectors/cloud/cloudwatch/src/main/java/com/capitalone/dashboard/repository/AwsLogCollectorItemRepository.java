package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.cloudwatch.model.AwsLogCollectorItem;
import org.bson.types.ObjectId;

import java.util.List;

public interface AwsLogCollectorItemRepository extends BaseCollectorItemRepository<AwsLogCollectorItem>{

    List<AwsLogCollectorItem> findByCollectorIdAndDescription(ObjectId collectorId, String description);
}
