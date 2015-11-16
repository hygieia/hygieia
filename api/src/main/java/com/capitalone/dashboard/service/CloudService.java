package com.capitalone.dashboard.service;

<<<<<<< HEAD
import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.DataResponse;
=======
import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.DataResponse;
import org.bson.types.ObjectId;

import java.util.List;
>>>>>>> origin/cloud-changes

public interface CloudService {

	DataResponse<CloudComputeData> getAggregatedData(ObjectId id);
	DataResponse<List<CloudComputeInstanceData>> getInstanceDetails(ObjectId id);

}