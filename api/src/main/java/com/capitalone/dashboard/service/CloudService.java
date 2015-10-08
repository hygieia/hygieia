package com.capitalone.dashboard.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.CloudComputeAggregatedData;
import com.capitalone.dashboard.model.CloudComputeRawData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.DataResponse;

public interface CloudService {

	DataResponse<CloudComputeAggregatedData> getAggregatedData(ObjectId id);
	DataResponse<List<CloudComputeRawData>> getInstanceDetails(ObjectId id);
	CollectorItem createCloudConfigCollectorItem(CollectorItem item);

}