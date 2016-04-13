package com.capitalone.dashboard.service;

import com.capitalone.dashboard.response.CloudInstanceDataResponse;
import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.DataResponse;
import org.bson.types.ObjectId;

import java.util.List;

public interface CloudService {

    DataResponse<CloudInstanceDataResponse> getAggregatedData(ObjectId id);

    DataResponse<List<CloudInstance>> getInstanceDetails(ObjectId id);

}