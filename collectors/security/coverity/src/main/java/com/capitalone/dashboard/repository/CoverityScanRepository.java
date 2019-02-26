package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.CoverityScan;

public interface CoverityScanRepository extends CrudRepository<CoverityScan, ObjectId> {

    CoverityScan findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);

    List<CoverityScan> findByCollectorItemId(ObjectId collectorItemId);
}
