package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.SubversionRepoRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * CollectorTask that fetches Commit information from Subversion
 */
@Component
public class SubversionCollectorTask extends CollectorTask<Collector> {

    private static final Log LOG = LogFactory.getLog(SubversionCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final SubversionRepoRepository subversionRepoRepository;
    private final CommitRepository commitRepository;
    private final SubversionClient subversionClient;
    private final SubversionSettings subversionSettings;

    @Autowired
    public SubversionCollectorTask(TaskScheduler taskScheduler,
                                   BaseCollectorRepository<Collector> collectorRepository,
                                   SubversionRepoRepository subversionRepoRepository,
                                   CommitRepository commitRepository,
                                   SubversionClient subversionClient,
                                   SubversionSettings subversionSettings) {
        super(taskScheduler, "Subversion");
        this.collectorRepository = collectorRepository;
        this.subversionRepoRepository = subversionRepoRepository;
        this.commitRepository = commitRepository;
        this.subversionClient = subversionClient;
        this.subversionSettings = subversionSettings;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("Subversion");
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
        return subversionSettings.getCron();
    }

    @Override
    public void collect(Collector collector) {

        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
        int commitCount = 0;

        for (SubversionRepo repo : enabledRepos(collector)) {
            for (Commit commit : subversionClient.getCommits(repo, startRevision(repo))) {
                if (isNewCommit(repo, commit)) {
                    commit.setCollectorItemId(repo.getId());
                    commitRepository.save(commit);
                    commitCount++;

                    long revisionNumber = Long.valueOf(commit.getScmRevisionNumber());
                    if (revisionNumber > repo.getLatestRevision()) {
                        repo.setLatestRev(revisionNumber);
                    }
                }

                // Save the repo in case the latestRevision changed
                subversionRepoRepository.save(repo);

            }
            repoCount++;
        }
        log("Repo Count", start, repoCount);
        log("New Commits", start, commitCount);
        log("Finished", start);
    }

    private long startRevision(SubversionRepo repo) {
        Date revisionDate = new DateTime().minusDays(subversionSettings.getCommitThresholdDays()).toDate();
        long revisionLimit = subversionClient.getRevisionClosestTo(repo.getUrl(), revisionDate);
        return revisionLimit > repo.getLatestRevision() ? revisionLimit : repo.getLatestRevision();
    }

    private List<SubversionRepo> enabledRepos(Collector collector) {
        return subversionRepoRepository.findEnabledSubversionRepos(collector.getId());
    }

    private boolean isNewCommit(SubversionRepo repo, Commit commit) {
        return commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), commit.getScmRevisionNumber()) == null;
    }

    private void log(String marker, long start) {
        log(marker, start, null);
    }

    private void log(String text, long start, Integer count) {
        long end = System.currentTimeMillis();
        String elapsed = ((end - start) / 1000) + "s";
        String token2 = "";
        String token3;
        if (count == null) {
            token3 = StringUtils.leftPad(elapsed, 30 - text.length() );
        } else {
            String countStr = count.toString();
            token2 = StringUtils.leftPad(countStr, 20 - text.length() );
            token3 = StringUtils.leftPad(elapsed, 10 );
        }
        LOG.info(text + token2 + token3);
    }

    private void logBanner(String instanceUrl) {
        LOG.info("------------------------------");
        LOG.info(instanceUrl);
        LOG.info("------------------------------");
    }
}
