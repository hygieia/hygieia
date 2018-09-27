package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;

public interface GenericCollectorItemService {


    String create(GenericCollectorItemCreateRequest request) throws HygieiaException;
}
