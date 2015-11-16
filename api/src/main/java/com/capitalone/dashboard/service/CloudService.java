package com.capitalone.dashboard.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.DataResponse;

public interface CloudService {

	DataResponse<CloudComputeData> getAggregatedData(ObjectId id);
	DataResponse<List<CloudComputeInstanceData>> getInstanceDetails(ObjectId id);

}