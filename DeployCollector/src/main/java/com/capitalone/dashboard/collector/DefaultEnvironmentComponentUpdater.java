package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.UDeployApplication;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultEnvironmentComponentUpdater implements EnvironmentComponentUpdater {

    private final UDeployClient uDeployClient;
    private final EnvironmentComponentRepository componentRepository;

    @Autowired
    public DefaultEnvironmentComponentUpdater(UDeployClient uDeployClient,
                                              EnvironmentComponentRepository componentRepository) {
        this.uDeployClient = uDeployClient;
        this.componentRepository = componentRepository;
    }

    @Override
    public void update(UDeployApplication application, Environment environment) {
        List<EnvironmentComponent> existingComponents = componentRepository.findByCollectorItemId(application.getId());

        for (EnvironmentComponent component : uDeployClient.getEnvironmentComponents(application, environment)) {
            EnvironmentComponent existing = findExistingComponent(component, existingComponents);

            if (existing == null) {
                // Add new
                component.setCollectorItemId(application.getId());
                componentRepository.save(component);
            } else if (changed(component, existing)) {
                // Update date and deployment status of existing
                existing.setAsOfDate(component.getAsOfDate());
                existing.setDeployed(component.isDeployed());
                componentRepository.save(existing);
            }
        }
    }

    private boolean changed(EnvironmentComponent component, EnvironmentComponent existing) {
        return existing.isDeployed() != component.isDeployed() || existing.getAsOfDate() != component.getAsOfDate();
    }

    private EnvironmentComponent findExistingComponent(final EnvironmentComponent proposed,
                                                       List<EnvironmentComponent> existingComponents) {

        return Iterables.tryFind(existingComponents, new Predicate<EnvironmentComponent>() {
            @Override
            public boolean apply(EnvironmentComponent existing) {
                return existing.getEnvironmentName().equals(proposed.getEnvironmentName()) &&
                        existing.getComponentName().equals(proposed.getComponentName()) &&
                        existing.getComponentVersion().equals(proposed.getComponentVersion());
            }
        }).orNull();
    }
}
