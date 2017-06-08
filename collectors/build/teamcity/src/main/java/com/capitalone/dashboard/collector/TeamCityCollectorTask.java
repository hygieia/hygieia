package com.capitalone.dashboard.collector;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.TeamCityCollector;
import com.capitalone.dashboard.model.TeamCityJob;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.TeamCityCollectorRepository;
import com.capitalone.dashboard.repository.TeamCityJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * CollectorTask that fetches Build information from TeamCity
 */
@Component
public class TeamCityCollectorTask extends CollectorTask<TeamCityCollector> {
    @SuppressWarnings("PMD.UnusedPrivateField")
//    private static final Log LOG = LogFactory.getLog(TeamCityCollectorTask.class);

    private final TeamCityCollectorRepository teamcityCollectorRepository;
    private final TeamCityJobRepository teamcityJobRepository;
    private final BuildRepository buildRepository;
    private final TeamCityClient teamcityClient;
    private final TeamCitySettings teamcitySettings;
    private final ComponentRepository dbComponentRepository;
    private static final Logger LOG = LoggerFactory.getLogger(TeamCityCollectorTask.class);

    
    
    @Autowired
    public TeamCityCollectorTask(TaskScheduler taskScheduler,
                               TeamCityCollectorRepository teamcityCollectorRepository,
                               TeamCityJobRepository teamcityJobRepository,
                               BuildRepository buildRepository, TeamCityClient teamcityClient,
                               TeamCitySettings teamcitySettings,
                               ComponentRepository dbComponentRepository) {
        super(taskScheduler, "TeamCity");
        this.teamcityCollectorRepository = teamcityCollectorRepository;
        this.teamcityJobRepository = teamcityJobRepository;
        this.buildRepository = buildRepository;
        this.teamcityClient = teamcityClient;
        this.teamcitySettings = teamcitySettings;
        this.dbComponentRepository = dbComponentRepository;
        LOG.debug("**********************************  Finished Constructor Work ***************************************");
    }

    @Override
    public TeamCityCollector getCollector() {
        return TeamCityCollector.prototype(teamcitySettings.getServers(), teamcitySettings.getNiceNames());
    }

    @Override
    public BaseCollectorRepository<TeamCityCollector> getCollectorRepository() {
        return teamcityCollectorRepository;
    }

    @Override
    public String getCron() {
        return teamcitySettings.getCron();
    }

    @Override
    public void collect(TeamCityCollector collector) {
        long start = System.currentTimeMillis();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        List<TeamCityJob> existingJobs = teamcityJobRepository.findByCollectorIdIn(udId);
        List<TeamCityJob> activeJobs = new ArrayList<>();
        List<String> activeServers = new ArrayList<>();
        activeServers.addAll(collector.getBuildServers());

        clean(collector, existingJobs);

        for (String instanceUrl : collector.getBuildServers()) {
            logBanner(instanceUrl);
            try {
                Map<TeamCityJob, Set<Build>> buildsByJob = teamcityClient
                        .getInstanceJobs(instanceUrl);
                log("Fetched Projects", start);
                activeJobs.addAll(buildsByJob.keySet());
                addNewJobs(buildsByJob.keySet(), existingJobs, collector);
                addNewBuilds(enabledJobs(collector, instanceUrl), buildsByJob);
                log("Finished", start);
            } catch (RestClientException rce) {
                activeServers.remove(instanceUrl); // since it was a rest exception, we will not delete this job  and wait for
                // rest exceptions to clear up at a later run.
                log("Error getting jobs for: " + instanceUrl, start);
            }
        }
        // Delete jobs that will be no longer collected because servers have moved etc.
        deleteUnwantedJobs(activeJobs, existingJobs, activeServers, collector);
    }

    /**
     * Clean up unused teamcity collector items
     *
     * @param collector    the {@link TeamCityCollector}
     * @param existingJobs
     */

    private void clean(TeamCityCollector collector, List<TeamCityJob> existingJobs) {
        Set<ObjectId> uniqueIDs = new HashSet<>();
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
                .findAll()) {

            if (CollectionUtils.isEmpty(comp.getCollectorItems())) continue;

            List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.Build);

            if (CollectionUtils.isEmpty(itemList)) continue;

            for (CollectorItem ci : itemList) {
                if (collector.getId().equals(ci.getCollectorId())) {
                    uniqueIDs.add(ci.getId());
                }
            }
        }
        List<TeamCityJob> stateChangeJobList = new ArrayList<>();
        for (TeamCityJob job : existingJobs) {
            if ((job.isEnabled() && !uniqueIDs.contains(job.getId())) ||  // if it was enabled but not on a dashboard
                    (!job.isEnabled() && uniqueIDs.contains(job.getId()))) { // OR it was disabled and now on a dashboard
                job.setEnabled(uniqueIDs.contains(job.getId()));
                stateChangeJobList.add(job);
            }
        }
        if (!CollectionUtils.isEmpty(stateChangeJobList)) {
            teamcityJobRepository.save(stateChangeJobList);
        }
    }

    /**
     * Delete orphaned job collector items
     * @param activeJobs
     * @param existingJobs
     * @param activeServers
     * @param collector
     */
    private void deleteUnwantedJobs(List<TeamCityJob> activeJobs, List<TeamCityJob> existingJobs, List<String> activeServers, TeamCityCollector collector) {

        List<TeamCityJob> deleteJobList = new ArrayList<>();
        for (TeamCityJob job : existingJobs) {
            if (job.isPushed()) continue; // build servers that push jobs will not be in active servers list by design

            // if we have a collector item for the job in repository but it's build server is not what we collect, remove it.
            if (!collector.getBuildServers().contains(job.getInstanceUrl())) {
                deleteJobList.add(job);
            }

            //if the collector id of the collector item for the job in the repo does not match with the collector ID, delete it.
            if (!job.getCollectorId().equals(collector.getId())) {
                deleteJobList.add(job);
            }

            // this is to handle jobs that have been deleted from build servers. Will get 404 if we don't delete them.
            if (activeServers.contains(job.getInstanceUrl()) && !activeJobs.contains(job)) {
                deleteJobList.add(job);
            }

        }
        if (!CollectionUtils.isEmpty(deleteJobList)) {
            teamcityJobRepository.delete(deleteJobList);
        }
    }

    /**
     * Iterates over the enabled build jobs and adds new builds to the database.
     *
     * @param enabledJobs list of enabled {@link TeamCityJob}s
     * @param buildsByJob maps a {@link TeamCityJob} to a set of {@link Build}s.
     */
    private void addNewBuilds(List<TeamCityJob> enabledJobs,
                              Map<TeamCityJob, Set<Build>> buildsByJob) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (TeamCityJob job : enabledJobs) {
            if (job.isPushed()) continue;
            for (Build buildSummary : nullSafe(buildsByJob.get(job))) {
                if (isNewBuild(job, buildSummary)) {

                    Build build = teamcityClient.getBuildDetails(buildSummary
                            .getBuildUrl(), job.getInstanceUrl());
                    if (build != null) {
                        build.setCollectorItemId(job.getId());
                        buildRepository.save(build);
                        count++;
                    }
                }
            }
        }
        log("New builds", start, count);
    }

    private Set<Build> nullSafe(Set<Build> builds) {
        return builds == null ? new HashSet<Build>() : builds;
    }

    /**
     * Adds new {@link TeamCityJob}s to the database as disabled jobs.
     *
     * @param jobs         list of {@link TeamCityJob}s
     * @param existingJobs
     * @param collector    the {@link TeamCityCollector}
     */
    private void addNewJobs(Set<TeamCityJob> jobs, List<TeamCityJob> existingJobs, TeamCityCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;

        List<TeamCityJob> newJobs = new ArrayList<>();
        for (TeamCityJob job : jobs) {
            TeamCityJob existing = null;
            if (!CollectionUtils.isEmpty(existingJobs) && (existingJobs.contains(job))) {
                existing = existingJobs.get(existingJobs.indexOf(job));
            }

            String niceName = getNiceName(job, collector);
            if (existing == null) {
                job.setCollectorId(collector.getId());
                job.setEnabled(true); // Do not enable for collection. Will be enabled when added to dashboard
                job.setDescription(job.getJobName());
                if (StringUtils.isNotEmpty(niceName)) {
                    job.setNiceName(niceName);
                }
                newJobs.add(job);
                count++;
            } else if (StringUtils.isEmpty(existing.getNiceName()) && StringUtils.isNotEmpty(niceName)) {
                existing.setNiceName(niceName);
                teamcityJobRepository.save(existing);
            }
        }
        //save all in one shot
        if (!CollectionUtils.isEmpty(newJobs)) {
            teamcityJobRepository.save(newJobs);
        }
        log("New jobs", start, count);
    }

    private String getNiceName(TeamCityJob job, TeamCityCollector collector) {
        if (CollectionUtils.isEmpty(collector.getBuildServers())) return "";
        List<String> servers = collector.getBuildServers();
        List<String> niceNames = collector.getNiceNames();
        if (CollectionUtils.isEmpty(niceNames)) return "";
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).equalsIgnoreCase(job.getInstanceUrl()) && (niceNames.size() > i)) {
                return niceNames.get(i);
            }
        }
        return "";
    }

    private List<TeamCityJob> enabledJobs(TeamCityCollector collector,
                                        String instanceUrl) {
        return teamcityJobRepository.findEnabledJobs(collector.getId(),
                instanceUrl);
    }

    @SuppressWarnings("unused")
	private TeamCityJob getExistingJob(TeamCityCollector collector, TeamCityJob job) {
        return teamcityJobRepository.findJob(collector.getId(),
                job.getInstanceUrl(), job.getJobName());
    }

    private boolean isNewBuild(TeamCityJob job, Build build) {
        return buildRepository.findByCollectorItemIdAndNumber(job.getId(),
                build.getNumber()) == null;
    }
}
