package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import org.bson.types.ObjectId;

import java.util.List;

public interface DeployService {

    /**
     * A snapshot of the deployment status of each DeployableUnit and Server
     * in all environments.
     *
     * @param componentId id of Component
     * @return list of Environments
     */
    DataResponse<List<Environment>> getDeployStatus(ObjectId componentId);

    String create(DeployDataCreateRequest request) throws HygieiaException;

    DataResponse<List<Environment>> getDeployStatus(String applicationName);
}
