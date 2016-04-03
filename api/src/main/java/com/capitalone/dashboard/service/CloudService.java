package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.DataResponse;
import org.bson.types.ObjectId;

import java.util.List;

public interface CloudService {

    DataResponse<CloudComputeData> getAggregatedData(ObjectId id);

    DataResponse<List<CloudComputeInstanceData>> getInstanceDetails(ObjectId id);

}