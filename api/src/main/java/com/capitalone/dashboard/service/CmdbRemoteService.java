package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.request.CmdbRequest;

public interface CmdbRemoteService {
    /**
     * Creates new Cmdb Item
     * @param request
     * @return newly created Cmdb Item
     * @throws HygieiaException
     */
    Cmdb remoteCreate(CmdbRequest request ) throws HygieiaException;
}




