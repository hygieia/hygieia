package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;

public interface GenericCollectorItemService {
    String create(GenericCollectorItemCreateRequest request) throws HygieiaException;
    String createGenericBinaryArtifactData(GenericCollectorItemCreateRequest request) throws HygieiaException;
}
