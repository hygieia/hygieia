package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.UDeployApplication;

/**
 * Adds new EnvironmentStatuses or updates existing EnvironmentStatuses
 */
public interface EnvironmentStatusUpdater {

    /**
     * Detects or new or changed EnvironmentStatuses and adds or updates.
     *
     * @param application an {@link UDeployApplication}
     * @param environment an {@link Environment}
     */
    void update(UDeployApplication application, Environment environment);
}
