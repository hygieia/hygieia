package com.capitalone.dashboard.collecteur;

import com.capitalone.dashboard.collector.CollectorTask;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitlabGitRepo;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitlabGitCollectorRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benathmane on 23/06/16.
 */

/**
 * CollectorTask that fetches Commit information from Gitlab
 */
@Component
@SuppressWarnings("PMD")
public class GitlabGitCollectorTask  extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(GitlabGitCollectorTask.class);
    private final BaseCollectorRepository<Collector> collectorRepository;
    private final GitlabGitCollectorRepository gitlabGitCollectorRepository;
    private final GitlabSettings gitlabSettings;
    private final DefaultGitlabGitClient defaultGitlabGitClient;
    private final ComponentRepository dbComponentRepository;
    private final CommitRepository commitRepository;


    @Autowired
    public GitlabGitCollectorTask(TaskScheduler taskScheduler,
                                  BaseCollectorRepository<Collector> collectorRepository,
                                  GitlabSettings gitlabSettings,
                                  CommitRepository commitRepository,
                                  GitlabGitCollectorRepository gitlabGitCollectorRepository,
                                  DefaultGitlabGitClient defaultGitlabGitClient,
                                  ComponentRepository dbComponentRepository
    ) {
        super(taskScheduler, "Gitlab");
        this.collectorRepository = collectorRepository;
        this.gitlabSettings = gitlabSettings;
        this.commitRepository = commitRepository;
        this.gitlabGitCollectorRepository = gitlabGitCollectorRepository;
        this.defaultGitlabGitClient = defaultGitlabGitClient;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public BaseCollectorRepository<Collector> getCollectorRepository() {
        return collectorRepository;
    }

    @Override
    public String getCron() {
        return gitlabSettings.getCron();
    }

    @Override
    public void collect(Collector collector) {
        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
        int commitCount = 0;
        clean(collector);
        for (GitlabGitRepo repo : enabledRepos(collector)) {
            List<Commit> commits  = defaultGitlabGitClient.getCommits(repo);
            for (Commit commit : commits) {
                LOG.debug(commit.getTimestamp()+":::"+commit.getScmCommitLog());
                commit.setCollectorItemId(repo.getId());
                commitRepository.save(commit);
                commitCount++;
            }
            repoCount++;
        }
        log("Repo Count", start, repoCount);
        log("New Commits", start, commitCount);
        log("Finished", start);
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("Gitlab");
        protoType.setCollectorType(CollectorType.SCM);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }

    /**
     * TO DO
     */
    private void clean(Collector collector) {
        List<Commit> all = (List<Commit>) commitRepository.findAll();
        List<Commit> commits = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            if(all.get(i).getNumberOfChanges() == 159753){
                    commits.add(all.get(i));
            }
        }
        /*for (int i = 0; i < all.size(); i++) {
            if(all.get(i).getTimestamp() <= collector.getLastExecuted()){
                    commits.add(all.get(i));
            }
        }*/

        commitRepository.delete(commits);
    }



    private List<GitlabGitRepo> enabledRepos(Collector collector) {
        return gitlabGitCollectorRepository.findEnabledGitlabRepos(collector.getId());
    }
}
