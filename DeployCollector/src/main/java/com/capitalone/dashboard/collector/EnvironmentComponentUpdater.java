package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.UDeployApplication;

/**
 * Adds new EnvironmentComponents or updates existing EnvironmentComponents
 */
public interface EnvironmentComponentUpdater {

    /**
     * Detects or new or changed EnvironmentComponents and adds or updates.
     *
     * @param application an {@link UDeployApplication}
     * @param environment an {@link Environment}
     */
    void update(UDeployApplication application, Environment environment);
}
