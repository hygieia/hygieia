package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.UDeployApplicationRepository;
import com.capitalone.dashboard.repository.UDeployCollectorRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Collects {@link EnvironmentComponent} and {@link EnvironmentStatus} data from
 * {@link UDeployApplication}s.
 */
@Component
public class UDeployCollectorTask extends CollectorTask<UDeployCollector> {
    private static final Log LOG = LogFactory.getLog(UDeployCollectorTask.class);

    private final UDeployCollectorRepository uDeployCollectorRepository;
    private final UDeployApplicationRepository uDeployApplicationRepository;
    private final UDeployClient uDeployClient;
    private final UDeploySettings uDeploySettings;
    private final EnvironmentComponentUpdater componentUpdater;
    private final EnvironmentStatusUpdater environmentStatusUpdater;

    @Autowired
    public UDeployCollectorTask(TaskScheduler taskScheduler,
                                UDeployCollectorRepository uDeployCollectorRepository,
                                UDeployApplicationRepository uDeployApplicationRepository,
                                UDeploySettings uDeploySettings,
                                UDeployClient uDeployClient,
                                EnvironmentComponentUpdater componentUpdater,
                                EnvironmentStatusUpdater environmentStatusUpdater) {
        super(taskScheduler, "UDeploy");
        this.uDeployCollectorRepository = uDeployCollectorRepository;
        this.uDeployApplicationRepository = uDeployApplicationRepository;
        this.uDeploySettings = uDeploySettings;
        this.uDeployClient = uDeployClient;
        this.componentUpdater = componentUpdater;
        this.environmentStatusUpdater = environmentStatusUpdater;
    }

    @Override
    public UDeployCollector getCollector() {
        return UDeployCollector.prototype(uDeploySettings.getServers());
    }

    @Override
    public BaseCollectorRepository<UDeployCollector> getCollectorRepository() {
        return uDeployCollectorRepository;
    }

    @Override
    public String getCron() {
        return uDeploySettings.getCron();
    }

    @Override
    public void collect(UDeployCollector collector) {
        for (String instanceUrl : collector.getUdeployServers()) {

            logInstanceBanner(instanceUrl);

            long start = System.currentTimeMillis();

            addNewApplications(uDeployClient.getApplications(instanceUrl), collector);

            refreshData(enabledApplications(collector, instanceUrl));

            log("Finished", start);
        }
    }

    /**
     * For each {@link UDeployApplication}, update the current {@link EnvironmentComponent}s
     * and {@link EnvironmentStatus}.
     *
     * @param uDeployApplications list of {@link UDeployApplication}s
     */
    private void refreshData(List<UDeployApplication> uDeployApplications) {
        long start = System.currentTimeMillis();

        log("Updating", start, uDeployApplications.size());

        for (UDeployApplication application : uDeployApplications) {
            long startApp = System.currentTimeMillis();

            for (Environment environment : uDeployClient.getEnvironments(application)) {
                componentUpdater.update(application, environment);
                environmentStatusUpdater.update(application, environment);
            }

            log(" " + application.getApplicationName(), startApp);
        }

        log("Updated", start);
    }

    private List<UDeployApplication> enabledApplications(UDeployCollector collector, String instanceUrl) {
        return uDeployApplicationRepository.findEnabledApplications(collector.getId(), instanceUrl);
    }

    /**
     * Add any new {@link UDeployApplication}s.
     *
     * @param applications list of {@link UDeployApplication}s
     * @param collector the {@link UDeployCollector}
     */
    private void addNewApplications(List<UDeployApplication> applications, UDeployCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;

        log("All apps", start, applications.size());
        for (UDeployApplication application : applications) {

            if (isNewApplication(collector, application)) {
                application.setCollectorId(collector.getId());
                application.setEnabled(false);
                application.setDescription(application.getApplicationName());
                uDeployApplicationRepository.save(application);
                count++;
            }

        }
        log("New apps", start, count);
    }

    private boolean isNewApplication(UDeployCollector collector, UDeployApplication application) {
        return uDeployApplicationRepository.findUDeployApplication(
                collector.getId(), application.getInstanceUrl(), application.getApplicationId()) == null;
    }

    private void log(String marker, long start) {
        log(marker, start, null);
    }

    private void log(String text, long start, Integer count) {
        long end = System.currentTimeMillis();
        int maxWidth = 25;
        String elapsed = ((end - start) / 1000) + "s";
        String token2 = "";
        String token3;
        if (count == null) {
            token3 = StringUtils.leftPad(elapsed, 30 - text.length());
        } else {
            maxWidth = 17;
            String countStr = count.toString();
            token2 = StringUtils.leftPad(countStr, 20 - text.length() );
            token3 = StringUtils.leftPad(elapsed, 10 );
        }
        LOG.info(StringUtils.abbreviate(text, maxWidth) + token2 + token3);
    }

    private void logInstanceBanner(String instanceUrl) {
        LOG.info("------------------------------");
        LOG.info(instanceUrl);
        LOG.info("------------------------------");
    }
}
