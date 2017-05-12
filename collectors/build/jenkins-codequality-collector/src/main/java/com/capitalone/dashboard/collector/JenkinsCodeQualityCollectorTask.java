package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.JenkinsPredicate;
import com.capitalone.dashboard.jenkins.JenkinsSettings;
import com.capitalone.dashboard.jenkins.model.JenkinsCodeQualityJob;
import com.capitalone.dashboard.model.quality.*;
import com.capitalone.dashboard.repository.JenkinsCodeQualityCollectorRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityJobRepository;
import com.capitalone.dashboard.utils.CodeQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class JenkinsCodeQualityCollectorTask extends CollectorTask<JenkinsCodeQualityCollector> {

    private static final String DESCRIPTION_FORMAT = "%s (%s)";

    private JenkinsCodeQualityCollectorRepository collectorRepository;
    private JenkinsCodeQualityJobRepository jobRepository;
    private JenkinsSettings settings;
    private JenkinsClient jenkinsClient;
    private CodeQualityService codeQualityService;

    @Autowired
    public JenkinsCodeQualityCollectorTask(TaskScheduler taskScheduler, JenkinsCodeQualityCollectorRepository repository,
                                           JenkinsCodeQualityJobRepository jobRepository, JenkinsSettings settings,
                                           JenkinsClient jenkinsClient, CodeQualityService codeQualityService) {
        super(taskScheduler, "JenkinsCodeQuality");
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

        List<JenkinsJob> allJobsOnServer = jobs.stream().flatMap(JenkinsJob::streamJobs).collect(Collectors.toList());
        this.cleanupPreviousJobsFromRepo(collector, allJobsOnServer);


        List<Pattern> matchingJobPatterns = new ArrayList<>();
        Map<ArtifactType, Pattern> artifactTypePatternMap = new HashMap<>();
        this.settings.getArtifactRegex().forEach(
                (key, uncompiledPatternList) -> uncompiledPatternList.stream().forEach(
                        unCompiledPattern -> {
                            Pattern compiledPattern = Pattern.compile(unCompiledPattern);
                            matchingJobPatterns.add(compiledPattern);
                            artifactTypePatternMap.put(key, compiledPattern);
                        }
                )
        );

        List<JenkinsJob> interestingJobs = jobs.stream().flatMap(JenkinsJob::streamJobs).filter(JenkinsPredicate.artifactInJobContaining(matchingJobPatterns)).collect(Collectors.toList());

        this.createAnyNewJobs(collector, interestingJobs);

        List<JenkinsCodeQualityJob> allJobs = this.jobRepository.findAllByCollectorId(collector.getId());
        int storedJobs =0;
        if (null != allJobs) {
            final Map<String, JenkinsCodeQualityJob> jenkinsCodeQualityJobMap = allJobs.stream().collect(Collectors.toMap(JenkinsCodeQualityJob::getJenkinsServer, Function.identity()));

            for (JenkinsJob job : interestingJobs) {
                List<CodeQualityVisitee> allTypes = new ArrayList<>();
                artifactTypePatternMap.forEach((type, pattern) -> {
                            switch (type) {
                                case junit:
                                    allTypes.addAll(this.jenkinsClient.getLatestArtifacts(JunitXmlReport.class, job, pattern));
                                    break;
                                case findbugs:
                                    allTypes.addAll(this.jenkinsClient.getLatestArtifacts(FindBugsXmlReport.class, job, pattern));
                                    break;
                                case jacoco:
                                    allTypes.addAll(this.jenkinsClient.getLatestArtifacts(JacocoXmlReport.class,job,pattern));
                                    break;
                                case pmd:
                                    allTypes.addAll(this.jenkinsClient.getLatestArtifacts(PmdReport.class,job,pattern));
                                    break;
                                case checkstyle:
                                    allTypes.addAll(this.jenkinsClient.getLatestArtifacts(CheckstyleReport.class,job,pattern));
                                    break;
                                default:
                                    this.log("not collecting data for "+type+ " yet");
                                    break;
                            }
                        }
                );
                boolean stored = this.codeQualityService.storeJob(job, jenkinsCodeQualityJobMap.get(job.getUrl()), allTypes);
                if (stored) {
                    storedJobs++;
                }
            }
        }

        log("examined jobs",System.currentTimeMillis(),null==allJobs?0:allJobs.size());
        log("new data",System.currentTimeMillis(),storedJobs);
        log("Finished",System.currentTimeMillis());

    }

    private void cleanupPreviousJobsFromRepo(JenkinsCodeQualityCollector collector, List<JenkinsJob> jobs) {
        List<String> configuredServers = jobs.stream().map(job -> job.getUrl()).collect(Collectors.toList());
        List<JenkinsCodeQualityJob> allRepoJobs = new ArrayList(this.jobRepository.findAllByCollectorId(collector.getId()));
        List<JenkinsCodeQualityJob> jobsToKeep = allRepoJobs.stream().filter(
            job ->
                configuredServers.contains(job.getJenkinsServer())
        ).collect(Collectors.toList());
        allRepoJobs.removeAll(jobsToKeep);
        allRepoJobs.forEach(job -> {
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
                    collectorId(collector.getId()).jobName(job.getName()).jenkinsServer(job.getUrl()).
                    description(String.format(DESCRIPTION_FORMAT, job.getName(), job.getUrl())).build();
            this.jobRepository.save(newJob);
        });
    }
}
