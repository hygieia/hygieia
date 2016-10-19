package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.JenkinsPredicate;
import com.capitalone.dashboard.jenkins.JenkinsSettings;
import com.capitalone.dashboard.model.JenkinsCodeQualityJob;
import com.capitalone.dashboard.model.JunitXmlReport;
import com.capitalone.dashboard.repository.JenkinsCodeQualityCollectorRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityJobRepository;
import com.capitalone.dashboard.utils.CodeQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
@Component
public class JenkinsCodeQualityCollectorTask extends CollectorTask<JenkinsCodeQualityCollector> {


    private JenkinsCodeQualityCollectorRepository collectorRepository;
    private JenkinsCodeQualityJobRepository jobRepository;
    private JenkinsSettings settings;
    private JenkinsClient jenkinsClient;
    private CodeQualityService codeQualityService;

    @Autowired
    public JenkinsCodeQualityCollectorTask(TaskScheduler taskScheduler, JenkinsCodeQualityCollectorRepository repository,
                                           JenkinsCodeQualityJobRepository jobRepository, JenkinsSettings settings,
                                           JenkinsClient jenkinsClient, CodeQualityService codeQualityService) {
        super(taskScheduler,"JenkinsCodeQuality");
        this.collectorRepository = repository;
        this.jobRepository = jobRepository;
        this.settings = settings;
        this.jenkinsClient = jenkinsClient;
        this.codeQualityService = codeQualityService;
    }

    public JenkinsCodeQualityCollector getCollector() {
        return JenkinsCodeQualityCollector.prototype(this.settings.getServers());
    }

    @Override
    public JenkinsCodeQualityCollectorRepository getCollectorRepository() {
         return this.collectorRepository;
    }

    @Override
    public String getCron() {
        return this.settings.getCron();
    }

    @Override
    public void collect(JenkinsCodeQualityCollector collector) {
        final List<String> buildServers = collector.getBuildServers();
        final List<JenkinsJob> jobs = this.jenkinsClient.getJobs(buildServers);
        if (null == jobs) {
            return;
        }

        this.cleanupPreviousJobsFromRepo(collector,jobs);

        List<Pattern> matchingJobPatterns = Arrays.asList(Pattern.compile(".*\\.xml"));

        List<JenkinsJob> interestingJobs = jobs.stream().filter(JenkinsPredicate.artifactInJobContaining(matchingJobPatterns)).collect(Collectors.toList());

        this.createAnyNewJobs(collector, interestingJobs);

        List<JenkinsCodeQualityJob> allJobs = this.jobRepository.findAllByCollectorId(collector.getId());
        if (null != allJobs) {
            final Map<String, JenkinsCodeQualityJob> jenkinsCodeQualityJobMap = allJobs.stream().collect(Collectors.toMap(JenkinsCodeQualityJob::getJenkinsServer, o -> o));

            for (JenkinsJob job : interestingJobs) {
                this.log("found an job of interest matching the artifact pattern.");
                List<JunitXmlReport> reportArtifacts = this.jenkinsClient.getLatestArtifacts(JunitXmlReport.class, job, matchingJobPatterns);
                this.codeQualityService.storeJob(job.getName(), jenkinsCodeQualityJobMap.get(job.getUrl()), reportArtifacts);
            }
        }

    }

    private void cleanupPreviousJobsFromRepo(JenkinsCodeQualityCollector collector,List<JenkinsJob> jobs) {
        List<String> configuredServers = jobs.stream().map(job -> job.getUrl()).collect(Collectors.toList());
        List<JenkinsCodeQualityJob> allRepoJobs = new ArrayList(this.jobRepository.findAllByCollectorId(collector.getId()));
        List<JenkinsCodeQualityJob> jobsToKeep=allRepoJobs.stream().filter(job->configuredServers.contains(job.getJenkinsServer())).collect(Collectors.toList());
        allRepoJobs.removeAll(jobsToKeep);
        allRepoJobs.forEach(job->{
            this.jobRepository.delete(job);
        });
    }

    private void createAnyNewJobs(JenkinsCodeQualityCollector collector, List<JenkinsJob> buildServerJobs) {
        List<JenkinsCodeQualityJob> allRepoJobs = new ArrayList<>(this.jobRepository.findAllByCollectorId(collector.getId()));

        List<JenkinsJob> newJobs = new ArrayList<>(buildServerJobs).stream().filter(jenkinsJob ->
                        !allRepoJobs.stream().anyMatch(
                                repoJob ->
                                        repoJob.getJenkinsServer().equals(jenkinsJob.getUrl())
                        )
        ).collect(Collectors.toList());

        newJobs.forEach(job -> {
            JenkinsCodeQualityJob newJob = JenkinsCodeQualityJob.newBuilder().
                    collectorId(collector.getId()).jobName(job.getName()).jenkinsServer(job.getUrl()).build();
            this.jobRepository.save(newJob);
        });
    }
}
