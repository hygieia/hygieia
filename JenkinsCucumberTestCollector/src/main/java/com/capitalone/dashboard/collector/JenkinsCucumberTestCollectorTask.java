package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.JenkinsCucumberTestCollector;
import com.capitalone.dashboard.model.JenkinsJob;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kyle Heide on 2/12/15.
 */
@Component
public class JenkinsCucumberTestCollectorTask extends CollectorTask<JenkinsCucumberTestCollector> {

    private static final Log LOG = LogFactory.getLog(JenkinsCucumberTestCollector.class);

    private final JenkinsCucumberTestCollectorRepository jenkinsCucumberTestCollectorRepository;
    private final JenkinsCucumberTestJobRepository jenkinsCucumberTestJobRepository;
    private final TestResultRepository testResultRepository;
    private final JenkinsClient jenkinsClient;
    private final JenkinsSettings jenkinsCucumberTestSettings;

    @Autowired
    public JenkinsCucumberTestCollectorTask(TaskScheduler taskScheduler,
                                            JenkinsCucumberTestCollectorRepository jenkinsCucumberTestCollectorRepository,
                                            JenkinsCucumberTestJobRepository jenkinsCucumberTestJobRepository,
                                            TestResultRepository testResultRepository,
                                            JenkinsClient jenkinsCucumberTestClient,
                                            JenkinsSettings jenkinsCucumberTestSettings) {
        super(taskScheduler, "JenkinsCucumberTest");
        this.jenkinsCucumberTestCollectorRepository = jenkinsCucumberTestCollectorRepository;
        this.jenkinsCucumberTestJobRepository = jenkinsCucumberTestJobRepository;
        this.testResultRepository = testResultRepository;
        this.jenkinsClient = jenkinsCucumberTestClient;
        this.jenkinsCucumberTestSettings = jenkinsCucumberTestSettings;
    }

    @Override
    public JenkinsCucumberTestCollector getCollector() {
        return JenkinsCucumberTestCollector.prototype(jenkinsCucumberTestSettings.getServers());
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

        for (String instanceUrl : collector.getBuildServers()) {
            logInstanceBanner(instanceUrl);

            long start = System.currentTimeMillis();

            Map<JenkinsJob, Set<Build>> buildsByJob = jenkinsClient.getInstanceJobs(instanceUrl);
            log("Fetched jobs", start);

            addNewJobs(buildsByJob.keySet(), collector);

            addNewTestSuites(enabledJobs(collector, instanceUrl), buildsByJob);

            log("Finished", start);
        }

    }


    // Jenkins Helper methods

    private List<JenkinsJob> enabledJobs(JenkinsCucumberTestCollector collector, String instanceUrl) {
        return jenkinsCucumberTestJobRepository.findEnabledJenkinsJobs(collector.getId(), instanceUrl);
    }

    /**
     * Adds new {@link JenkinsJob}s to the database as disabled jobs.
     *
     * @param jobs list of {@link JenkinsJob}s
     * @param collector the {@link JenkinsCucumberTestCollector}
     */
    private void addNewJobs(Set<JenkinsJob> jobs, JenkinsCucumberTestCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (JenkinsJob job : jobs) {

            if (isNewJob(collector, job)) {
                job.setCollectorId(collector.getId());
                job.setEnabled(false); // Do not enable for collection. Will be enabled when added to dashboard
                job.setDescription(job.getJobName());
                jenkinsCucumberTestJobRepository.save(job);
                count++;
            }

        }
        log("New jobs", start, count);
    }

    private void addNewTestSuites(List<JenkinsJob> enabledJobs, Map<JenkinsJob, Set<Build>> buildsByJob) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (JenkinsJob job : enabledJobs) {

            for (Build buildSummary : nullSafe(buildsByJob.get(job))) {

                if (jenkinsClient.buildHasCucumberResults(buildSummary.getBuildUrl())
                        && isNewCucumberResult(job, buildSummary)) {

                    // Obtain the Test Result
                    TestResult result = jenkinsClient.getCucumberTestResult(buildSummary.getBuildUrl());
                    if (result != null) {
                        result.setCollectorItemId(job.getId());
                        result.setTimestamp(System.currentTimeMillis());
                        testResultRepository.save(result);
                        count++;
                    }
                }
            }
        }
        log("New test suites", start, count);
    }

    private boolean isNewJob(JenkinsCucumberTestCollector collector, JenkinsJob job) {
        return jenkinsCucumberTestJobRepository.findJenkinsJob(
                collector.getId(), job.getInstanceUrl(), job.getJobName()) == null;
    }

    private boolean isNewCucumberResult(JenkinsJob job, Build build) {
        return testResultRepository.findByCollectorItemIdAndExecutionId(job.getId(), build.getNumber()) == null;
    }


    private Set<Build> nullSafe(Set<Build> builds) {
        return builds == null ? new HashSet<Build>() : builds;
    }


    // Helper Log Methods TODO: these should be moved to the super class in core

    private void log(String marker, long start) {
        log(marker, start, null);
    }

    private void log(String text, long start, Integer count) {
        long end = System.currentTimeMillis();
        String elapsed = ((end - start) / 1000) + "s";
        String token2 = "";
        String token3;
        if (count == null) {
            token3 = StringUtils.leftPad(elapsed, 30 - text.length());
        } else {
            String countStr = count.toString();
            token2 = StringUtils.leftPad(countStr, 20 - text.length() );
            token3 = StringUtils.leftPad(elapsed, 10 );
        }
        LOG.info(text + token2 + token3);
    }

    private void logInstanceBanner(String instanceUrl) {
        LOG.info("------------------------------");
        LOG.info(instanceUrl);
        LOG.info("------------------------------");
    }

}
