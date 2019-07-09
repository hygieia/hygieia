package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.BaseModel;
import com.capitalone.dashboard.model.CollectionError;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitHubRateLimit;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitHubRepoRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CollectorTask that fetches Commit information from GitHub
 */
@Component
public class GitHubCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(GitHubCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final GitHubRepoRepository gitHubRepoRepository;
    private final CommitRepository commitRepository;
    private final GitRequestRepository gitRequestRepository;
    private final GitHubClient gitHubClient;
    private final GitHubSettings gitHubSettings;
    private final ComponentRepository dbComponentRepository;
    private static final long FOURTEEN_DAYS_MILLISECONDS = 14 * 24 * 60 * 60 * 1000;

    @Autowired
    public GitHubCollectorTask(TaskScheduler taskScheduler,
                               BaseCollectorRepository<Collector> collectorRepository,
                               GitHubRepoRepository gitHubRepoRepository,
                               CommitRepository commitRepository,
                               GitRequestRepository gitRequestRepository,
                               GitHubClient gitHubClient,
                               GitHubSettings gitHubSettings,
                               ComponentRepository dbComponentRepository) {
        super(taskScheduler, "GitHub");
        this.collectorRepository = collectorRepository;
        this.gitHubRepoRepository = gitHubRepoRepository;
        this.commitRepository = commitRepository;
        this.gitHubClient = gitHubClient;
        this.gitHubSettings = gitHubSettings;
        this.dbComponentRepository = dbComponentRepository;
        this.gitRequestRepository = gitRequestRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("GitHub");
        protoType.setCollectorType(CollectorType.SCM);
        protoType.setOnline(true);
        protoType.setEnabled(true);

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(GitHubRepo.REPO_URL, "");
        allOptions.put(GitHubRepo.BRANCH, "");
        allOptions.put(GitHubRepo.USER_ID, "");
        allOptions.put(GitHubRepo.PASSWORD, "");
        allOptions.put(GitHubRepo.PERSONAL_ACCESS_TOKEN, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(GitHubRepo.REPO_URL, "");
        uniqueOptions.put(GitHubRepo.BRANCH, "");
        protoType.setUniqueFields(uniqueOptions);
        return protoType;
    }

    @Override
    public BaseCollectorRepository<Collector> getCollectorRepository() {
        return collectorRepository;
    }

    @Override
    public String getCron() {
        return gitHubSettings.getCron();
    }

    /**
     * Clean up unused deployment collector items
     *
     * @param collector the {@link Collector}
     */
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed, fixme
    private void clean(Collector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<>();
        /*
          Logic: For each component, retrieve the collector item list of the type SCM.
          Store their IDs in a unique set ONLY if their collector IDs match with GitHub collectors ID.
         */
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
            if (!MapUtils.isEmpty(comp.getCollectorItems())) {
                List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.SCM);
                if (itemList != null) {
                    itemList.stream().filter(ci -> ci != null && Objects.equals(ci.getCollectorId(), collector.getId())).map(BaseModel::getId).forEach(uniqueIDs::add);
                }
            }
        }
        /*
          Logic: Get all the collector items from the collector_item collection for this collector.
          If their id is in the unique set (above), keep them enabled; else, disable them.
         */
        List<GitHubRepo> repoList = new ArrayList<>();
        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(collector.getId());
        gitHubRepoRepository.findByCollectorIdIn(gitID).stream().filter(Objects::nonNull).forEach(repo -> {
            if (repo.isPushed()) {return;}
            repo.setEnabled(uniqueIDs.contains(repo.getId()));
            repoList.add(repo);
        });
        gitHubRepoRepository.save(repoList);
    }


    @Override
    @SuppressWarnings({"PMD.AvoidDeeplyNestedIfStmts"})
    public void collect(Collector collector) {

        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
        int commitCount = 0;
        int pullCount = 0;
        int issueCount = 0;

        setupProxy();

        clean(collector);
        List<GitHubRepo> enabledRepos = enabledRepos(collector);
        for (GitHubRepo repo : enabledRepos) {
            LOG.info("Starting collection: " + (repoCount + 1) + " of " + enabledRepos.size() + ": " + repo.getRepoUrl() + "/tree/" + repo.getBranch());

            boolean firstRun = ((repo.getLastUpdated() == 0) || ((start - repo.getLastUpdated()) > FOURTEEN_DAYS_MILLISECONDS));

            if (repo.checkErrorOrReset(gitHubSettings.getErrorResetWindow(), gitHubSettings.getErrorThreshold())) {

                try {
                    if (gitHubSettings.isCheckRateLimit() &&  !isUnderRateLimit(repo)) {
                        LOG.error("GraphQL API rate limit reached. Stopping processing");
                        continue;
                    }

                    List<GitRequest> allRequests = gitRequestRepository.findRequestNumberAndLastUpdated(repo.getId());

                    Map<Long, String> existingPRMap = allRequests.stream().filter(r -> Objects.equals(r.getRequestType(), "pull")).collect(
                            Collectors.toMap(GitRequest::getUpdatedAt, GitRequest::getNumber,
                                    (oldValue, newValue) -> oldValue
                            )
                    );

                    Map<Long, String> existingIssueMap = allRequests.stream().filter(r -> Objects.equals(r.getRequestType(), "issue")).collect(
                            Collectors.toMap(GitRequest::getUpdatedAt, GitRequest::getNumber,
                                    (oldValue, newValue) -> oldValue
                            )
                    );


                    gitHubClient.fireGraphQL(repo, firstRun, existingPRMap, existingIssueMap);

                    // Get all the commits
                    commitCount += processCommits(repo);

                    //Get all the Pull Requests
                    pullCount += processPRorIssueList(repo, allRequests.stream().filter(r -> Objects.equals(r.getRequestType(), "pull")).collect(Collectors.toList()), "pull");
                    //Get all the Issues
                    issueCount += processPRorIssueList(repo, allRequests.stream().filter(r -> Objects.equals(r.getRequestType(), "issue")).collect(Collectors.toList()), "issue");

                    // Due to timing of PRs and Commits in PR merge event, some commits may not be included in the response and will not be connected to a PR.
                    // This is the place attempting to re-connect the commits and PRs in case they were missed during previous run.

                    processOrphanCommits(repo);

                    repo.setLastUpdated(System.currentTimeMillis());
                    // if everything went alright, there should be no error!
                    repo.getErrors().clear();

                } catch (HttpStatusCodeException hc) {
                    LOG.error("Error fetching commits for:" + repo.getRepoUrl(), hc);
                    CollectionError error = new CollectionError(hc.getStatusCode().toString(), hc.getMessage());
                    repo.getErrors().add(error);
                } catch (RestClientException | MalformedURLException ex) {
                    LOG.error("Error fetching commits for:" + repo.getRepoUrl(), ex);
                    CollectionError error = new CollectionError(CollectionError.UNKNOWN_HOST, ex.getMessage());
                    repo.getErrors().add(error);
                } catch (HygieiaException he) {
                    LOG.error("Error fetching commits for:" + repo.getRepoUrl(), he);
                    CollectionError error = new CollectionError(String.valueOf(he.getErrorCode()), he.getMessage());
                    repo.getErrors().add(error);
                }
                gitHubRepoRepository.save(repo);
            } else {
                LOG.info(repo.getRepoUrl()+ "::" + repo.getBranch() + ":: errorThreshold exceeded");
            }
            repoCount++;
        }
        log("Repo Count", start, repoCount);
        log("New Commits", start, commitCount);
        log("New Pulls", start, pullCount);
        log("New Issues", start, issueCount);
        log("Finished", start);

    }

    private void setupProxy() {
        String proxyUrl = gitHubSettings.getProxyUrl();
        String proxyPort = gitHubSettings.getProxyPort();
        String proxyUser = gitHubSettings.getProxyUser();
        String proxyPassword = gitHubSettings.getProxyPassword();

        if (!StringUtils.isEmpty(proxyUrl) && !StringUtils.isEmpty(proxyPort)) {
            System.setProperty("http.proxyHost", proxyUrl);
            System.setProperty("https.proxyHost", proxyUrl);
            System.setProperty("http.proxyPort", proxyPort);
            System.setProperty("https.proxyPort", proxyPort);

            if (!StringUtils.isEmpty(proxyUser) && !StringUtils.isEmpty(proxyPassword)) {
                System.setProperty("http.proxyUser", proxyUser);
                System.setProperty("https.proxyUser", proxyUser);
                System.setProperty("http.proxyPassword", proxyPassword);
                System.setProperty("https.proxyPassword", proxyPassword);
            }
        }
    }

    // Retrieves a st of previous commits and Pulls and tries to reconnect them
    private void processOrphanCommits(GitHubRepo repo) {
        long refTime = Math.min(System.currentTimeMillis() - gitHubSettings.getCommitPullSyncTime(), gitHubClient.getRepoOffsetTime(repo));
        List<Commit> orphanCommits = commitRepository.findCommitsByCollectorItemIdAndTimestampAfterAndPullNumberIsNull(repo.getId(), refTime);
        List<GitRequest> pulls = gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(repo.getId(), refTime, System.currentTimeMillis());
        orphanCommits = CommitPullMatcher.matchCommitToPulls(orphanCommits, pulls);
        List<Commit> orphanSaveList = orphanCommits.stream().filter(c -> !StringUtils.isEmpty(c.getPullNumber())).collect(Collectors.toList());
        orphanSaveList.forEach( c -> LOG.info( "Updating orphan " + c.getScmRevisionNumber() + " " +
                new DateTime(c.getScmCommitTimestamp()).toString("yyyy-MM-dd hh:mm:ss.SSa") + " with pull " + c.getPullNumber()));
        commitRepository.save(orphanSaveList);
    }

    /**
     * Process commits
     *
     * @param repo
     * @return count added
     */
    private int processCommits(GitHubRepo repo) {
        int count = 0;
        Long existingCount = commitRepository.countCommitsByCollectorItemId(repo.getId());
        if (existingCount == 0) {
            List<Commit> newCommits = gitHubClient.getCommits();
            newCommits.forEach(c -> c.setCollectorItemId(repo.getId()));
            Iterable<Commit> saved = commitRepository.save(newCommits);
            count = saved != null ? Lists.newArrayList(saved).size() : 0;
        } else {
            Collection<Commit> nonDupCommits = gitHubClient.getCommits().stream()
                    .<Map<String, Commit>> collect(HashMap::new,(m,c)->m.put(c.getScmRevisionNumber(), c), Map::putAll)
                    .values();
            for (Commit commit : nonDupCommits) {
                LOG.debug(commit.getTimestamp() + ":::" + commit.getScmCommitLog());
                if (isNewCommit(repo, commit)) {
                    commit.setCollectorItemId(repo.getId());
                    commitRepository.save(commit);
                    count++;
                }
            }
        }
        LOG.info("-- Saved Commits = " + count);
        return count;
    }


    private boolean isUnderRateLimit(GitHubRepo repo) throws MalformedURLException, HygieiaException {
        GitHubRateLimit rateLimit = null;
        try {
            rateLimit = gitHubClient.getRateLimit(repo);
            if(rateLimit!=null){
                LOG.info("Remaining " + rateLimit.getRemaining() + " of limit " + rateLimit.getLimit()
                        + " resetTime " + new DateTime(rateLimit.getResetTime()).toString("yyyy-MM-dd hh:mm:ss.SSa"));
            }else{
                LOG.info("Rate limit is null");
            }

        } catch (HttpClientErrorException hce) {
            LOG.error("getRateLimit returned " + hce.getStatusCode() + " " + hce.getMessage() + " " + hce);
            return false;
        }
        return rateLimit != null && (rateLimit.getRemaining() > gitHubSettings.getRateLimitThreshold());
    }

    private int processPRorIssueList(GitHubRepo repo, List<GitRequest> existingList, String type) {
        int count = 0;

        List<GitRequest> entries = "pull".equalsIgnoreCase(type) ? gitHubClient.getPulls() : gitHubClient.getIssues();

        if (CollectionUtils.isEmpty(entries)) return 0;

        for (GitRequest entry : entries) {
            Optional<GitRequest> existingOptional = existingList.stream().filter(r -> Objects.equals(r.getNumber(), entry.getNumber())).findFirst();
            GitRequest existing = existingOptional.orElse(null);

            if (existing == null) {
                entry.setCollectorItemId(repo.getId());
                count++;
            } else {
                entry.setId(existing.getId());
                entry.setCollectorItemId(repo.getId());
            }
            gitRequestRepository.save(entry);
        }
        LOG.info("-- Saved " + type  + ":" + count);
        return count;
    }


    private List<GitHubRepo> enabledRepos(Collector collector) {
        List<GitHubRepo> repos = gitHubRepoRepository.findEnabledGitHubRepos(collector.getId());

        List<GitHubRepo> pulledRepos
                = Optional.ofNullable(repos)
                    .orElseGet(Collections::emptyList).stream()
                    .filter(pulledRepo -> !pulledRepo.isPushed())
                    .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(pulledRepos)) { return new ArrayList<>(); }

        pulledRepos.sort(Comparator.comparing(GitHubRepo::getLastUpdated));

        return pulledRepos;
    }

    private boolean isNewCommit(GitHubRepo repo, Commit commit) {
        return commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), commit.getScmRevisionNumber()) == null;
    }
}
