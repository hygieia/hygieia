package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.JenkinsPredicate;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.JunitXmlReport;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityRepository;
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


    private JenkinsCodeQualityRepository repository;
    private String cronSchedule;
    private JenkinsClient jenkinsClient;
    private CodeQualityConverter codeQualityConverter;
    private CodeQualityRepository codeQualityRepository;

    @Autowired
    public JenkinsCodeQualityCollectorTask(TaskScheduler taskScheduler, JenkinsCodeQualityRepository repository, String cronSchedule, JenkinsClient jenkinsClient, CodeQualityConverter codeQualityConverter, CodeQualityRepository codeQualityRepository) {
        super(taskScheduler,"JenkinsCodeQuality");
        this.repository = repository;
        this.cronSchedule = cronSchedule;
        this.jenkinsClient = jenkinsClient;
        this.codeQualityConverter = codeQualityConverter;
        this.codeQualityRepository = codeQualityRepository;
    }

    public JenkinsCodeQualityCollector getCollector() {
        return new JenkinsCodeQualityCollector();
    }

    @Override
    public JenkinsCodeQualityRepository getCollectorRepository() {
         return this.repository;
    }

    @Override
    public String getCron() {
        return this.cronSchedule;
    }

    @Override
    public void collect(JenkinsCodeQualityCollector collector) {
        final List<String> buildServers = collector.getBuildServers();
        final List<JenkinsJob> jobs = this.jenkinsClient.getJobs(buildServers);

        List<Pattern> matchingJobPatterns = Arrays.asList(Pattern.compile(".*\\.xml"));

        List<JenkinsJob> interestingJobs = jobs.stream().filter(JenkinsPredicate.artifactContaining(matchingJobPatterns)).collect(Collectors.toList());

        for (JenkinsJob job : interestingJobs) {
            this.log("found an job of interest matching the artifact pattern.");
            List<JunitXmlReport> reportArtifacts = this.jenkinsClient.getLatestArtifacts(JunitXmlReport.class, job, matchingJobPatterns);


            CodeQuality currentJobQuality = computeMetricsForJob(reportArtifacts);

            // store the data
            codeQualityRepository.save(currentJobQuality);
        }

    }

    private CodeQuality computeMetricsForJob(List<JunitXmlReport> reportArtifacts) {
        CodeQuality qualityForJob = new CodeQuality();
        Map<String, CodeQualityMetric> currentMetrics = new HashMap<>();
        for (JunitXmlReport reportArtifact : reportArtifacts) {
            Set<CodeQualityMetric> codeQualityMetrics = this.codeQualityConverter.analyse(reportArtifact);
            Map<String, CodeQualityMetric> reportMetricsMap = codeQualityMetrics.stream().collect(Collectors.toMap(CodeQualityMetric::getName, Function.identity()));
            // get cuurent value
            // create a new metric (or mutate current :( ) that is the sum of the cuurent vakue and the new value
            reportMetricsMap.forEach((key, value) -> {
                CodeQualityMetric currentValue = currentMetrics.get(key);
                CodeQualityMetric newValue;
                if (null == currentValue) {
                    newValue = value;
                } else {
                    // do the sum
                    newValue = new CodeQualityMetric(key);
                    newValue.setFormattedValue(String.valueOf(Integer.parseInt(currentValue.getFormattedValue()) + Integer.parseInt(value.getFormattedValue())));
                }
                currentMetrics.put(key, newValue);
            });

        }
        currentMetrics.forEach((key, value) -> {
            qualityForJob.addMetric(value);
        });
        return qualityForJob;
    }
}
