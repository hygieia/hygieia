package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@SuppressWarnings("PMD.UnnecessaryFullyQualifiedName") // Will need to rename com.capitalone.dashboard.Component as it conflicts with Spring.
@Component
public class JenkinsCucumberTestCollectorTask extends
        CollectorTask<JenkinsCucumberTestCollector> {

    private final JenkinsCucumberTestCollectorRepository jenkinsCucumberTestCollectorRepository;
    private final JenkinsCucumberTestJobRepository jenkinsCucumberTestJobRepository;
    private final TestResultRepository testResultRepository;
    private final JenkinsClient jenkinsClient;
    private final JenkinsSettings jenkinsCucumberTestSettings;
    private final ComponentRepository dbComponentRepository;
    private static final int CLEANUP_INTERVAL = 3600000;

    @Autowired
    public JenkinsCucumberTestCollectorTask(
            TaskScheduler taskScheduler,
            JenkinsCucumberTestCollectorRepository jenkinsCucumberTestCollectorRepository,
            JenkinsCucumberTestJobRepository jenkinsCucumberTestJobRepository,
            TestResultRepository testResultRepository,
            JenkinsClient jenkinsCucumberTestClient,
            JenkinsSettings jenkinsCucumberTestSettings,
            ComponentRepository dbComponentRepository) {
        super(taskScheduler, "JenkinsCucumberTest");
        this.jenkinsCucumberTestCollectorRepository = jenkinsCucumberTestCollectorRepository;
        this.jenkinsCucumberTestJobRepository = jenkinsCucumberTestJobRepository;
        this.testResultRepository = testResultRepository;
        this.jenkinsClient = jenkinsCucumberTestClient;
        this.jenkinsCucumberTestSettings = jenkinsCucumberTestSettings;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public JenkinsCucumberTestCollector getCollector() {
        return JenkinsCucumberTestCollector
                .prototype(jenkinsCucumberTestSettings.getServers());
    }

    @Override
    public BaseCollectorRepository<JenkinsCucumberTestCollector> getCollectorRepository() {
        return jenkinsCucumberTestCollectorRepository;
    }

    @Override
    public String getCron() {
        return jenkinsCucumberTestSettings.getCron();
    }

    @Override
    public void collect(JenkinsCucumberTestCollector collector) {

        long start = System.currentTimeMillis();

        // Clean up every hour
        if ((start - collector.getLastExecuted()) > CLEANUP_INTERVAL) {
            clean(collector);
        }

        for (String instanceUrl : collector.getBuildServers()) {
            logBanner(instanceUrl);

            Map<JenkinsJob, Set<Build>> buildsByJob = jenkinsClient
                    .getInstanceJobs(instanceUrl);
            log("Fetched jobs", start);

            addNewJobs(buildsByJob.keySet(), collector);

            addNewTestSuites(enabledJobs(collector, instanceUrl));

            log("Finished", start);
        }

    }

    /**
     * Clean up unused hudson/jenkins collector items
     *
     * @param collector the collector
     */

    private void clean(JenkinsCucumberTestCollector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<>();
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
                .findAll()) {
            if (!CollectionUtils.isEmpty(comp.getCollectorItems())) {
                List<CollectorItem> itemList = comp.getCollectorItems().get(
                        CollectorType.Test);
                for (CollectorItem ci : itemList) {
                    if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                        uniqueIDs.add(ci.getId());
                    }
                }
            }
        }
        List<JenkinsJob> jobList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        for (JenkinsJob job : jenkinsCucumberTestJobRepository
                .findByCollectorIdIn(udId)) {
            if (job != null) {
                job.setEnabled(uniqueIDs.contains(job.getId()));
                jobList.add(job);
            }
        }
        jenkinsCucumberTestJobRepository.save(jobList);
    }

    // Jenkins Helper methods

    private List<JenkinsJob> enabledJobs(
            JenkinsCucumberTestCollector collector, String instanceUrl) {
        return jenkinsCucumberTestJobRepository.findEnabledJenkinsJobs(
                collector.getId(), instanceUrl);
    }

    /**
     * Adds new {@link JenkinsJob}s to the database as disabled jobs.
     *
     * @param jobs      list of {@link JenkinsJob}s
     * @param collector the {@link JenkinsCucumberTestCollector}
     */
    private void addNewJobs(Set<JenkinsJob> jobs,
                            JenkinsCucumberTestCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (JenkinsJob job : jobs) {
            if (jenkinsClient.buildHasCucumberResults(job.getJobUrl())
                    && isNewJob(collector, job)) {
                job.setCollectorId(collector.getId());
                job.setEnabled(false); // Do not enable for collection. Will be
                // enabled when added to dashboard
                job.setDescription(job.getJobName());
                jenkinsCucumberTestJobRepository.save(job);
                count++;
            }
        }
        log("New jobs", start, count);
    }

    private void addNewTestSuites(List<JenkinsJob> enabledJobs) {
        long start = System.currentTimeMillis();
        int count = 0;
        for (JenkinsJob job : enabledJobs) {
            Build buildSummary = jenkinsClient.getLastSuccessfulBuild(job.getJobUrl());
            if (isNewCucumberResult(job, buildSummary)) {
                // Obtain the Test Result
                TestResult result = jenkinsClient
                        .getCucumberTestResult(job.getJobUrl());
                if (result != null) {
                    result.setCollectorItemId(job.getId());
                    result.setTimestamp(System.currentTimeMillis());
                    testResultRepository.save(result);
                    count++;
                }
            }
        }
        log("New test suites", start, count);
    }

    private boolean isNewJob(JenkinsCucumberTestCollector collector,
                             JenkinsJob job) {
        return jenkinsCucumberTestJobRepository.findJenkinsJob(
                collector.getId(), job.getInstanceUrl(), job.getJobName()) == null;
    }

    private boolean isNewCucumberResult(JenkinsJob job, Build build) {
        return testResultRepository.findByCollectorItemIdAndExecutionId(
                job.getId(), build.getNumber()) == null;
    }

    private Set<Build> nullSafe(Set<Build> builds) {
        return builds == null ? new HashSet<Build>() : builds;
    }
}
