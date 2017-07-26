package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.BambooCollector;
import com.capitalone.dashboard.model.BambooJob;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BambooCollectorRepository;
import com.capitalone.dashboard.repository.BambooJobRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CollectorTask that fetches Build information from Bamboo
 */
@Component
public class BambooCollectorTask extends CollectorTask<BambooCollector> {
    @SuppressWarnings("PMD.UnusedPrivateField")
    private static final Logger LOG = LoggerFactory.getLogger(BambooCollectorTask.class);


    private final BambooCollectorRepository bambooCollectorRepository;
    private final BambooJobRepository bambooJobRepository;
    private final BuildRepository buildRepository;
    private final BambooClient bambooClient;
    private final BambooSettings bambooSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public BambooCollectorTask(TaskScheduler taskScheduler,
                               BambooCollectorRepository bambooCollectorRepository,
                               BambooJobRepository bambooJobRepository,
                               BuildRepository buildRepository, BambooClient bambooClient,
                               BambooSettings bambooSettings,
                               ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Bamboo");
        this.bambooCollectorRepository = bambooCollectorRepository;
        this.bambooJobRepository = bambooJobRepository;
        this.buildRepository = buildRepository;
        this.bambooClient = bambooClient;
        this.bambooSettings = bambooSettings;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public BambooCollector getCollector() {
        return BambooCollector.prototype(bambooSettings.getServers(), bambooSettings.getNiceNames());
    }

    @Override
    public BaseCollectorRepository<BambooCollector> getCollectorRepository() {
        return bambooCollectorRepository;
    }

    @Override
    public String getCron() {
        return bambooSettings.getCron();
    }

    @Override
    public void collect(BambooCollector collector) {
        long start = System.currentTimeMillis();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        List<BambooJob> existingJobs = bambooJobRepository.findByCollectorIdIn(udId);
        List<BambooJob> activeJobs = new ArrayList<>();
        List<String> activeServers = new ArrayList<>();
        activeServers.addAll(collector.getBuildServers());

        clean(collector, existingJobs);

        for (String instanceUrl : collector.getBuildServers()) {
            logBanner(instanceUrl);
            try {
                Map<BambooJob, Set<Build>> buildsByJob = bambooClient
                        .getInstanceJobs(instanceUrl);
                log("Fetched jobs", start);
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
     * Clean up unused bamboo/jenkins collector items
     *
     * @param collector    the {@link BambooCollector}
     * @param existingJobs
     */

    private void clean(BambooCollector collector, List<BambooJob> existingJobs) {
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
        List<BambooJob> stateChangeJobList = new ArrayList<>();
        for (BambooJob job : existingJobs) {
            if ((job.isEnabled() && !uniqueIDs.contains(job.getId())) ||  // if it was enabled but not on a dashboard
                    (!job.isEnabled() && uniqueIDs.contains(job.getId()))) { // OR it was disabled and now on a dashboard
                job.setEnabled(uniqueIDs.contains(job.getId()));
                stateChangeJobList.add(job);
            }
        }
        if (!CollectionUtils.isEmpty(stateChangeJobList)) {
            bambooJobRepository.save(stateChangeJobList);
        }
    }

    /**
     * Delete orphaned job collector items
     *
     * @param activeJobs
     * @param existingJobs
     * @param activeServers
     * @param collector
     */
    private void deleteUnwantedJobs(List<BambooJob> activeJobs, List<BambooJob> existingJobs, List<String> activeServers, BambooCollector collector) {

        List<BambooJob> deleteJobList = new ArrayList<>();
        for (BambooJob job : existingJobs) {
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
            bambooJobRepository.delete(deleteJobList);
        }
    }

    /**
     * Iterates over the enabled build jobs and adds new builds to the database.
     *
     * @param enabledJobs list of enabled {@link BambooJob}s
     * @param buildsByJob maps a {@link BambooJob} to a set of {@link Build}s.
     */
    private void addNewBuilds(List<BambooJob> enabledJobs,
                              Map<BambooJob, Set<Build>> buildsByJob) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (BambooJob job : enabledJobs) {
            if (job.isPushed()) {
                LOG.info("Job Pushed already: " + job.getJobName());
                continue;
            }
            // process new builds in the order of their build numbers - this has implication to handling of commits in BuildEventListener
            ArrayList<Build> builds = Lists.newArrayList(nullSafe(buildsByJob.get(job)));
            builds.sort((Build b1, Build b2) -> Integer.valueOf(b1.getNumber()) - Integer.valueOf(b2.getNumber()));
            for (Build buildSummary : builds) {
                if (isNewBuild(job, buildSummary)) {
                    Build build = bambooClient.getBuildDetails(buildSummary
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
     * Adds new {@link BambooJob}s to the database as disabled jobs.
     *
     * @param jobs         list of {@link BambooJob}s
     * @param existingJobs
     * @param collector    the {@link BambooCollector}
     */
    private void addNewJobs(Set<BambooJob> jobs, List<BambooJob> existingJobs, BambooCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;

        List<BambooJob> newJobs = new ArrayList<>();
        for (BambooJob job : jobs) {
            BambooJob existing = null;
            if (!CollectionUtils.isEmpty(existingJobs) && (existingJobs.contains(job))) {
                existing = existingJobs.get(existingJobs.indexOf(job));
            }

            String niceName = getNiceName(job, collector);
            if (existing == null) {
                job.setCollectorId(collector.getId());
                job.setEnabled(false); // Do not enable for collection. Will be enabled when added to dashboard
                job.setDescription(job.getJobName());
                if (StringUtils.isNotEmpty(niceName)) {
                    job.setNiceName(niceName);
                }
                newJobs.add(job);
                count++;
            } else if (StringUtils.isEmpty(existing.getNiceName()) && StringUtils.isNotEmpty(niceName)) {
                existing.setNiceName(niceName);
                bambooJobRepository.save(existing);
            }
        }
        //save all in one shot
        if (!CollectionUtils.isEmpty(newJobs)) {
            bambooJobRepository.save(newJobs);
        }
        log("New jobs", start, count);
    }

    private String getNiceName(BambooJob job, BambooCollector collector) {
        if (CollectionUtils.isEmpty(collector.getBuildServers())) return "";
        List<String> servers = collector.getBuildServers();
        List<String> niceNames = collector.getNiceNames();
        if (CollectionUtils.isEmpty(niceNames)) return "";
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).equalsIgnoreCase(job.getInstanceUrl()) && (niceNames.size() > (i + 1))) {
                return niceNames.get(i);
            }
        }
        return "";
    }

    private List<BambooJob> enabledJobs(BambooCollector collector,
                                        String instanceUrl) {
        return bambooJobRepository.findEnabledJobs(collector.getId(),
                instanceUrl);
    }

    @SuppressWarnings("unused")
    private BambooJob getExistingJob(BambooCollector collector, BambooJob job) {
        return bambooJobRepository.findJob(collector.getId(),
                job.getInstanceUrl(), job.getJobName());
    }

    private boolean isNewBuild(BambooJob job, Build build) {
        return buildRepository.findByCollectorItemIdAndNumber(job.getId(),
                build.getNumber()) == null;
    }
}
