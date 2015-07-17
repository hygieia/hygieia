package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.model.UDeployApplication;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultEnvironmentStatusUpdater implements EnvironmentStatusUpdater {
    private final UDeployClient uDeployClient;
    private final EnvironmentStatusRepository environmentStatusRepository;

    @Autowired
    public DefaultEnvironmentStatusUpdater(UDeployClient uDeployClient,
                                           EnvironmentStatusRepository environmentStatusRepository) {
        this.uDeployClient = uDeployClient;
        this.environmentStatusRepository = environmentStatusRepository;
    }

    @Override
    public void update(UDeployApplication application, Environment environment) {
        List<EnvironmentStatus> existingStatuses = environmentStatusRepository.findByCollectorItemId(application.getId());

        for (EnvironmentStatus status : uDeployClient.getEnvironmentStatusData(application, environment)) {
            EnvironmentStatus existing = findExistingStatus(status, existingStatuses);

            if (existing == null) {
                // Add new
                status.setCollectorItemId(application.getId());
                environmentStatusRepository.save(status);
            } else if (changed(status, existing)) {
                // Update online status of existing
                existing.setOnline(status.isOnline());
                environmentStatusRepository.save(existing);
            }
        }
    }

    private boolean changed(EnvironmentStatus status, EnvironmentStatus existing) {
        return existing.isOnline() != status.isOnline();
    }

    private EnvironmentStatus findExistingStatus(final EnvironmentStatus proposed,
                                                 List<EnvironmentStatus> existingStatuses) {

        return Iterables.tryFind(existingStatuses, new Predicate<EnvironmentStatus>() {
            @Override
            public boolean apply(EnvironmentStatus existing) {
                return existing.getEnvironmentName().equals(proposed.getEnvironmentName()) &&
                        existing.getComponentName().equals(proposed.getComponentName()) &&
                        existing.getResourceName().equals(proposed.getResourceName());
            }
        }).orNull();
    }
}
