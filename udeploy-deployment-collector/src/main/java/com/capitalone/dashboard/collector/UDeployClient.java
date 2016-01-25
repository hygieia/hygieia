package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.model.UDeployApplication;
import com.capitalone.dashboard.model.UDeployEnvResCompData;

import java.util.List;

/**
 * Client for fetching information from UDeploy.
 */
public interface UDeployClient {

    /**
     * Fetches all {@link UDeployApplication}s for a given instance URL.
     *
     * @param instanceUrl instance URL
     * @return list of {@link UDeployApplication}s
     */
    List<UDeployApplication> getApplications(String instanceUrl);

    /**
     * Fetches all {@link Environment}s for a given {@link UDeployApplication}.
     *
     * @param application a {@link UDeployApplication}
     * @return list of {@link Environment}s
     */
    List<Environment> getEnvironments(UDeployApplication application);

    /**
     * Fetches all {@link EnvironmentComponent}s for a given {@link UDeployApplication} and {@link Environment}.
     *
     * @param application a {@link UDeployApplication}
     * @param environment an {@link Environment}
     * @return list of {@link EnvironmentComponent}s
     */
    List<EnvironmentComponent> getEnvironmentComponents(UDeployApplication application, Environment environment);

    /**
     * Fetches all {@link EnvironmentStatus}es for a given {@link UDeployApplication} and {@link Environment}.
     *
     * @param application a {@link UDeployApplication}
     * @param environment an {@link Environment}
     * @return list of {@link EnvironmentStatus}es
     */
    List<UDeployEnvResCompData> getEnvironmentResourceStatusData(UDeployApplication application, Environment environment);
}
