package com.capitalone.dashboard.service;

import java.util.List;

import com.capitalone.dashboard.model.CloudAggregatedData;
import com.capitalone.dashboard.model.CloudRawData;
import com.capitalone.dashboard.model.DataResponse;


public interface CloudService {

	DataResponse<CloudAggregatedData> getAccount();

	DataResponse<List<CloudRawData>> getInstanceDetail();

	boolean authenticate(String username);


}