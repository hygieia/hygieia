package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.JenkinsPredicate;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityCollectorRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityJobRepository;
import com.capitalone.dashboard.utils.CodeQualityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsCodeQualityCollectorTask extends CollectorTask<JenkinsCodeQualityCollector> {


    private JenkinsCodeQualityCollectorRepository collectorRepository;
    private JenkinsCodeQualityJobRepository jobRepository;
    private String cronSchedule;
    private JenkinsClient jenkinsClient;
    private CodeQualityConverter codeQualityConverter;
    private CodeQualityRepository codeQualityRepository;

    @Autowired
    public JenkinsCodeQualityCollectorTask(TaskScheduler taskScheduler, JenkinsCodeQualityCollectorRepository repository, JenkinsCodeQualityJobRepository jobRepository,String cronSchedule, JenkinsClient jenkinsClient, CodeQualityConverter codeQualityConverter, CodeQualityRepository codeQualityRepository) {
        super(taskScheduler,"JenkinsCodeQuality");
        this.collectorRepository = repository;
        this.jobRepository = jobRepository;
        this.cronSchedule = cronSchedule;
        this.jenkinsClient = jenkinsClient;
        this.codeQualityConverter = codeQualityConverter;
        this.codeQualityRepository = codeQualityRepository;
    }

    public JenkinsCodeQualityCollector getCollector() {
        return new JenkinsCodeQualityCollector();
    }

    @Override
    public JenkinsCodeQualityCollectorRepository getCollectorRepository() {
         return this.collectorRepository;
    }

    @Override
    public String getCron() {
        return this.cronSchedule;
    }

    @Override
    public void collect(JenkinsCodeQualityCollector collector) {
        final List<String> buildServers = collector.getBuildServers();
        final List<JenkinsJob> jobs = this.jenkinsClient.getJobs(buildServers);

        this.cleanupPreviousJobsFromRepo(collector,jobs);

        List<Pattern> matchingJobPatterns = Arrays.asList(Pattern.compile(".*\\.xml"));

        List<JenkinsJob> interestingJobs = jobs.stream().filter(JenkinsPredicate.artifactContaining(matchingJobPatterns)).collect(Collectors.toList());

        for (JenkinsJob job : interestingJobs) {
            this.log("found an job of interest matching the artifact pattern.");
            List<JunitXmlReport> reportArtifacts = this.jenkinsClient.getLatestArtifacts(JunitXmlReport.class, job, matchingJobPatterns);


            CodeQuality currentJobQuality = computeMetricsForJob(reportArtifacts);
            currentJobQuality.setCollectorItemId(collector.getId());

            // store the data
            codeQualityRepository.save(currentJobQuality);
        }

    }

    private void cleanupPreviousJobsFromRepo(JenkinsCodeQualityCollector collector,List<JenkinsJob> jobs) {
        List<String> configuredServers = jobs.stream().map(job->job.getJenkinsServer()).collect(Collectors.toList());
        List<JenkinsCodeQualityJob> allRepoJobs = this.jobRepository.findAllByCollectorId(collector.getId());
        List<JenkinsCodeQualityJob> jobsToKeep=allRepoJobs.stream().filter(job->configuredServers.contains(job.getJenkinsServer())).collect(Collectors.toList());
        allRepoJobs.removeAll(jobsToKeep);
        allRepoJobs.forEach(job->{
            this.jobRepository.delete(job);
        });
    }

    private CodeQuality computeMetricsForJob(List<JunitXmlReport> reportArtifacts) {
        CodeQuality qualityForJob = new CodeQuality();
        Map<String, CodeQualityMetric> currentMetrics = new HashMap<>();
        for (JunitXmlReport reportArtifact : reportArtifacts) {
            Set<CodeQualityMetric> codeQualityMetrics = this.codeQualityConverter.analyse(reportArtifact);
            Map<String, CodeQualityMetric> reportMetricsMap = codeQualityMetrics.stream().collect(Collectors.toMap(CodeQualityMetric::getName, Function.identity()));

            // for all the metrics we have, combine and add where necessary
            reportMetricsMap.forEach((key, value) -> {
                CodeQualityMetric currentValue = currentMetrics.get(key);
                CodeQualityMetric newValue;
                if (null == currentValue) {
                    newValue = value;
                } else {
                    // do the sum
                    newValue = new CodeQualityMetric(key);
                    newValue.setValue((int)currentValue.getValue()+(int)value.getValue());
                    newValue.setFormattedValue(String.valueOf((int)currentValue.getValue()+ (int)value.getValue()));
                    int newOrdinal = Math.max(value.getStatus().ordinal(),currentValue.getStatus().ordinal());
                    newValue.setStatus(CodeQualityMetricStatus.values()[newOrdinal]);
                    String concatMessage = concatStrings(currentValue.getStatusMessage(),value.getStatusMessage());
                    newValue.setStatusMessage(concatMessage);
                }
                currentMetrics.put(key, newValue);
            });

        }
        currentMetrics.forEach((key, value) -> {
            qualityForJob.addMetric(value);
        });
        return qualityForJob;
    }

    private String concatStrings(String statusMessage, String endMessage) {
        String result=null;
        if (statusMessage!=null && !statusMessage.isEmpty()) {
            result=statusMessage;
        }
        if(endMessage!=null && !endMessage.isEmpty()) {
            result= result!=null?result+","+endMessage:endMessage;
        }
        return result;
    }
}
