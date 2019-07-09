package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.request.MetadataCreateRequest;

public interface MetadataService {

    String create(MetadataCreateRequest request) throws HygieiaException;

}
