package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollItemCfgHist;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CollItemCfgHistRepository extends CrudRepository<CollItemCfgHist, ObjectId>  {
    CollItemCfgHist findByCollectorItemIdAndJobAndTimestamp(ObjectId collectorItemId, String job, long timestamp);

    //List<CollItemCfgHist> findByCollectorItemIdAndJobAndJobUrlAndTimestampGreaterThanEqualAndTimestampLessThanEqualOrderByTimestampDesc(ObjectId collectorItemId, String job, String jobUrl, long beginDt, long endDt);

    List<CollItemCfgHist> findByCollectorItemIdAndJobAndJobUrlAndTimestampBetweenOrderByTimestampDesc(ObjectId collectorItemId, String job, String jobUrl, long beginDt, long endDt);
}
