package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GerritRepo;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GerritRepoRepository;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CollectorTask that fetches Commit information from GitHub
 */
@Component
public class GerritCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(GerritCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final GerritRepoRepository gerritRepoRepository;
    private final CommitRepository commitRepository;
    private final GerritSettings gerritSettings;
    private final ComponentRepository dbComponentRepository;
    private final static  String GERRIT_TIME_FORMAT = "yyyy-MM-dd[HH:mm:ss[.sss][Z]]";

    @Autowired
    public GerritCollectorTask(TaskScheduler taskScheduler,
                               BaseCollectorRepository<Collector> collectorRepository,
                               GerritRepoRepository gerritRepoRepository,
                               CommitRepository commitRepository,
                               GerritSettings gerritSettings,
                               ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Gerrit");
        this.collectorRepository = collectorRepository;
        this.gerritRepoRepository = gerritRepoRepository;
        this.commitRepository = commitRepository;
        this.gerritSettings = gerritSettings;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("Gerrit");
        protoType.setCollectorType(CollectorType.SCM);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }

    @Override
    public BaseCollectorRepository<Collector> getCollectorRepository() {
        return collectorRepository;
    }

    @Override
    public String getCron() {
        return gerritSettings.getCron();
    }

    /**
     * Clean up unused deployment collector items
     *
     * @param collector the {@link Collector}
     */
    private void clean(Collector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
        /**
         * Logic: For each component, retrieve the collector item list of the type SCM.
         * Store their IDs in a unique set ONLY if their collector IDs match with Gerrit collectors ID.
         */
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
            if (CollectionUtils.isEmpty(comp.getCollectorItems())) continue;
            List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.SCM);
            if (CollectionUtils.isEmpty(itemList)) continue;
            for (CollectorItem ci : itemList) {
                if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                    uniqueIDs.add(ci.getId());
                }
            }
        }
        /**
         * Logic: Get all the collector items from the collector_item collection for this collector.
         * If their id is in the unique set (above), keep them enabled; else, disable them.
         */
        List<GerritRepo> repoList = new ArrayList<>();
        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(collector.getId());
        for (GerritRepo repo : gerritRepoRepository.findByCollectorIdIn(gitID)) {
            if (repo == null) continue;
            repo.setEnabled(uniqueIDs.contains(repo.getId()));
            repoList.add(repo);
        }
        gerritRepoRepository.save(repoList);
    }


    @Override
    public void collect(Collector collector) {

        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
        int commitCount = 0;

        clean(collector);
        for (GerritRepo repo : enabledRepos(collector)) {
            repo.setLastUpdated(System.currentTimeMillis());
            repo.removeLastUpdateDate();  //moved last update date to collector item. This is to clean old data.
            gerritRepoRepository.save(repo);
            LOG.debug(repo.getOptions().toString() + "::" + repo.getBranch());

            for (Commit commit : getCommits(repo)) {
                LOG.debug(commit.getTimestamp() + ":::" + commit.getScmCommitLog());
                if (isNewCommit(repo, commit)) {
                    commit.setCollectorItemId(repo.getId());
                    commitRepository.save(commit);
                    commitCount++;
                }
            }
            repoCount++;
        }
        log("Repo Count", start, repoCount);
        log("New Commits", start, commitCount);
        log("Finished", start);
    }

    private List<Commit> getCommits(GerritRepo repo) {
        List<Commit> commits = new ArrayList<>();
        List<ChangeInfo> changes = getChanges(repo);
        for (ChangeInfo ci : changes) {
            Commit commit = new Commit();
            commit.setTimestamp(System.currentTimeMillis());
            commit.setScmUrl(gerritSettings.getHost() + "/" + repo.getProject() + "/" + repo.getBranch());
            commit.setScmRevisionNumber(ci.changeId);
            commit.setScmAuthor(ci.owner.name);
            commit.setScmCommitLog(ci.subject);
            commit.setScmCommitTimestamp(ci.updated.getTime());
            commit.setNumberOfChanges(ci._number);
            commits.add(commit);

        }
        return commits;
    }


    private List<ChangeInfo> getChanges(GerritRepo repo) {
        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        GerritAuthData.Basic authData = new GerritAuthData.Basic(gerritSettings.getHost(), gerritSettings.getUser(), gerritSettings.getPassword());
        GerritApi gerritApi = gerritRestApiFactory.create(authData);
        try {
            return gerritApi.changes().query("status:" + gerritSettings.getStatusToCollect() + "+project:"
                    + repo.getProject() + "+branch:" + repo.getBranch() + "+since:" + getDateTimeSince(repo.getLastUpdated())).get();
        } catch (RestApiException e) {
            log("Error Getting Gerrit Changes." + e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<GerritRepo> enabledRepos(Collector collector) {
        return gerritRepoRepository.findEnabledGitHubRepos(collector.getId());
    }

    private boolean isNewCommit(GerritRepo repo, Commit commit) {
        return commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), commit.getScmRevisionNumber()) == null;
    }

    private String getDateTimeSince(long start) {

        Date startDateTime;
        if (start == 0) {
            startDateTime = DateUtils.addDays(new Date(), -gerritSettings.getFirstRunHistoryDays());
        } else {
            startDateTime = DateUtils.addMinutes(new Date(start), -gerritSettings.getCollectionOffsetMins());
        }
        SimpleDateFormat format = new SimpleDateFormat(GERRIT_TIME_FORMAT);
        return format.format(startDateTime);
    }
}
