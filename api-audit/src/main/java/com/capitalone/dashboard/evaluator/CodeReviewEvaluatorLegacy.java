package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.common.CommonCodeReview;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.model.ServiceAccount;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.ServiceAccountRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.capitalone.dashboard.status.CodeReviewAuditStatus.COLLECTOR_ITEM_ERROR;

@Component
public class CodeReviewEvaluatorLegacy extends LegacyEvaluator {

    private final CommitRepository commitRepository;
    private final GitRequestRepository gitRequestRepository;
    protected final ApiSettings settings;
    private final ServiceAccountRepository serviceAccountRepository;

    @Autowired
    public CodeReviewEvaluatorLegacy(CommitRepository commitRepository, GitRequestRepository gitRequestRepository, ServiceAccountRepository serviceAccountRepository, ApiSettings settings) {
        this.commitRepository = commitRepository;
        this.gitRequestRepository = gitRequestRepository;
        this.settings = settings;
        this.serviceAccountRepository = serviceAccountRepository;
    }

    @Override
    public List<CodeReviewAuditResponse> evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection<?> data) {
        return getPeerReviewResponses(collectorItem, new ArrayList<>(), beginDate, endDate);
    }

    @Override
    public List<CodeReviewAuditResponse> evaluate(CollectorItem collectorItem, List<CollectorItem> collectorItemList,
                                                  long beginDate, long endDate, Collection<?> data) {
        return getPeerReviewResponses(collectorItem, collectorItemList, beginDate, endDate);
    }

    /**
     * Return an empty response in error situation
     *
     * @param repoItem  the repo item
     * @param scmBranch the scrm branch
     * @param scmUrl    the scm url
     * @return code review audit response
     */
    protected CodeReviewAuditResponse getErrorResponse(CollectorItem repoItem, String scmBranch, String scmUrl) {
        CodeReviewAuditResponse noPRsCodeReviewAuditResponse = new CodeReviewAuditResponse();
        noPRsCodeReviewAuditResponse.addAuditStatus(COLLECTOR_ITEM_ERROR);

        noPRsCodeReviewAuditResponse.setLastUpdated(repoItem.getLastUpdated());
        noPRsCodeReviewAuditResponse.setScmBranch(scmBranch);
        noPRsCodeReviewAuditResponse.setScmUrl(scmUrl);
        noPRsCodeReviewAuditResponse.setErrorMessage(repoItem.getErrors().get(0).getErrorMessage());
        return noPRsCodeReviewAuditResponse;
    }

    private List<CodeReviewAuditResponse> getPeerReviewResponses(CollectorItem repoItem,
                                                                 List<CollectorItem> collectorItemList,
                                                                 long beginDt, long endDt) {
        List<CodeReviewAuditResponse> allPeerReviews = new ArrayList<>();

        if (repoItem == null) {
            CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
            codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.REPO_NOT_CONFIGURED);

            allPeerReviews.add(codeReviewAuditResponse);
            return allPeerReviews;
        }

        String scmUrl = (String) repoItem.getOptions().get("url");
        String scmBranch = (String) repoItem.getOptions().get("branch");

        GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmUrl);

        String parsedUrl = gitHubParsed.getUrl(); //making sure we have a goot url

        if (StringUtils.isEmpty(scmBranch) || StringUtils.isEmpty(scmUrl)) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = getErrorResponse(repoItem, scmBranch, parsedUrl);
            allPeerReviews.add(noPRsCodeReviewAuditResponse);
            return allPeerReviews;
        }

        //if there are errors on the collector item
        if (!CollectionUtils.isEmpty(repoItem.getErrors())) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = getErrorResponse(repoItem, scmBranch, parsedUrl);
            allPeerReviews.add(noPRsCodeReviewAuditResponse);
            return allPeerReviews;
        }

        //if the collector item is pending data collection
        if (repoItem.getLastUpdated() == 0) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = new CodeReviewAuditResponse();
            noPRsCodeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.PENDING_DATA_COLLECTION);

            noPRsCodeReviewAuditResponse.setLastUpdated(repoItem.getLastUpdated());
            noPRsCodeReviewAuditResponse.setScmBranch(scmBranch);
            noPRsCodeReviewAuditResponse.setScmUrl(scmUrl);
            allPeerReviews.add(noPRsCodeReviewAuditResponse);
            return allPeerReviews;
        }

        List<GitRequest> pullRequests = gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(repoItem.getId(), beginDt-1, endDt+1);
        List<Commit> commits = commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(repoItem.getId(), beginDt-1, endDt+1);
        if (CollectionUtils.isEmpty(pullRequests)) {
            CodeReviewAuditResponse noPRsCodeReviewAuditResponse = new CodeReviewAuditResponse();
            noPRsCodeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
            allPeerReviews.add(noPRsCodeReviewAuditResponse);
        }

        //check for pr author <> pr merger
        //check to see if pr was reviewed
        //type of branching strategy
        List<String> allPrCommitShas = new ArrayList<>();
        List<String> mergeCommitShas = new ArrayList<>();
        pullRequests.stream().filter(pr -> "merged".equalsIgnoreCase(pr.getState())).forEach(pr -> {
            auditPullRequest(repoItem, pr, commits, mergeCommitShas, allPrCommitShas, allPeerReviews);
        });

        //check any commits not directly tied to pr
        CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
        List<Commit> commitsNotDirectlyTiedToPr = new ArrayList<>();
        commits.forEach(commit -> {

            if (!checkPrCommitsAndMergeCommits(allPrCommitShas, commit, mergeCommitShas)) { return; }

            if (isCommitEligibleForDirectCommitsForPushedRepo(repoItem, commit, collectorItemList, beginDt, endDt)
                 || isCommitEligibleForDirectCommitsForPulledRepo(repoItem, commit)) {
                commitsNotDirectlyTiedToPr.add(commit);
                // auditServiceAccountChecks includes - check for service account and increment version tag for service account on direct commits.
                auditServiceAccountChecks(codeReviewAuditResponse, commit);
            }
        });

        if (!commitsNotDirectlyTiedToPr.isEmpty()) {
            codeReviewAuditResponse.setCommits(commitsNotDirectlyTiedToPr);
            allPeerReviews.add(codeReviewAuditResponse);
        }

        //pull requests in date range, but merged prior to 14 days so no commits available in hygieia
        if (!CollectionUtils.isEmpty(pullRequests)) {
            if (allPeerReviews.isEmpty()) {
                CodeReviewAuditResponse prsButNoCommitsInRangeCodeReviewAuditResponse = new CodeReviewAuditResponse();
                prsButNoCommitsInRangeCodeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.NO_PULL_REQ_FOR_DATE_RANGE);
                allPeerReviews.add(prsButNoCommitsInRangeCodeReviewAuditResponse);
            }
        }

        allPeerReviews.forEach(peerReviewResponseList -> {
            peerReviewResponseList.setLastUpdated(repoItem.getLastUpdated());
            peerReviewResponseList.setScmBranch(scmBranch);
            peerReviewResponseList.setScmUrl(parsedUrl);
        });
        return allPeerReviews;
    }

    private boolean checkPrCommitsAndMergeCommits(List<String> allPrCommitShas, Commit commit, List<String> mergeCommitShas) {
        if ( (!allPrCommitShas.contains(commit.getScmRevisionNumber()))
                && (commit.getType() == CommitType.New)
                && (!mergeCommitShas.contains(commit.getScmRevisionNumber())) ) {
            return true;
        }

        return false;
    }

    private boolean isCommitEligibleForDirectCommitsForPushedRepo(CollectorItem repoItem, Commit commit,
                                                                  List<CollectorItem> collectorItemList,
                                                                  long beginDt, long endDt) {
        if (repoItem.isPushed()
                && !existsApprovedPROnAnotherBranch(repoItem, commit, collectorItemList, beginDt, endDt)) {
            return true;
        }
        return false;
    }

    private boolean isCommitEligibleForDirectCommitsForPulledRepo(CollectorItem repoItem, Commit commit) {
        if (!repoItem.isPushed() && StringUtils.isEmpty(commit.getPullNumber())) {
            return true;
        }
        return false;
    }

    protected void auditPullRequest(CollectorItem repoItem, GitRequest pr, List<Commit> commits,
                                    List<String> mergeCommitShas, List<String> allPrCommitShas,
                                    List<CodeReviewAuditResponse> allPeerReviews) {
        CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
        codeReviewAuditResponse.setPullRequest(pr);
        String mergeSha = pr.getScmRevisionNumber();

        Commit mergeCommit = Optional.ofNullable(commits)
                                .orElseGet(Collections::emptyList).stream()
                                .filter(c -> Objects.equals(c.getScmRevisionNumber(), mergeSha))
                                .findFirst().orElse(null);

        if (mergeCommit == null) {
            mergeCommit = Optional.ofNullable(commits)
                            .orElseGet(Collections::emptyList).stream()
                            .filter(c -> Objects.equals(c.getScmRevisionNumber(), pr.getScmMergeEventRevisionNumber()))
                            .findFirst().orElse(null);
        }

        List<Commit> commitsRelatedToPr = pr.getCommits();
        commitsRelatedToPr.sort(Comparator.comparing(e -> (e.getScmCommitTimestamp())));
        if (mergeCommit == null) {
            codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.MERGECOMMITER_NOT_FOUND);
        } else {
            mergeCommitShas.add(mergeCommit.getScmRevisionNumber());
            if (repoItem.isPushed()) {
                codeReviewAuditResponse.addAuditStatus(pr.getUserId().equalsIgnoreCase(mergeCommit.getScmCommitterLogin()) ? CodeReviewAuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER : CodeReviewAuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
            } else {
                codeReviewAuditResponse.addAuditStatus(pr.getUserId().equalsIgnoreCase(mergeCommit.getScmAuthorLogin()) ? CodeReviewAuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER : CodeReviewAuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
            }
        }
        codeReviewAuditResponse.setCommits(commitsRelatedToPr);

        allPrCommitShas.addAll(commitsRelatedToPr.stream().map(SCM::getScmRevisionNumber).collect(Collectors.toList()));

        boolean peerReviewed = CommonCodeReview.computePeerReviewStatus(pr, settings, codeReviewAuditResponse, commits, commitRepository, serviceAccountRepository);
        codeReviewAuditResponse.addAuditStatus(peerReviewed ? CodeReviewAuditStatus.PULLREQ_REVIEWED_BY_PEER : CodeReviewAuditStatus.PULLREQ_NOT_PEER_REVIEWED);
        String sourceRepo = pr.getSourceRepo();
        String targetRepo = pr.getTargetRepo();
        codeReviewAuditResponse.addAuditStatus(sourceRepo == null ? CodeReviewAuditStatus.GIT_FORK_STRATEGY : sourceRepo.equalsIgnoreCase(targetRepo) ? CodeReviewAuditStatus.GIT_BRANCH_STRATEGY : CodeReviewAuditStatus.GIT_FORK_STRATEGY);
        if (!StringUtils.isEmpty(pr.getMergeAuthorLDAPDN()) && (CommonCodeReview.checkForServiceAccount(pr.getMergeAuthorLDAPDN(), settings, getAllServiceAccounts(),pr.getMergeAuthor(),null,false,codeReviewAuditResponse))) {
            codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.MERGECOMMITER_EQ_SERVICEACCOUNT);
        }
        allPeerReviews.add(codeReviewAuditResponse);
    }

    protected boolean existsApprovedPROnAnotherBranch(CollectorItem repoItem, Commit commit, List<CollectorItem> collectorItemList,
                                                      long beginDt, long endDt) {
        CollectorItem collectorItem = Optional.ofNullable(collectorItemList)
                                        .orElseGet(Collections::emptyList).stream()
                                        .filter(ci -> existsApprovedPRForCollectorItem(repoItem, commit, ci, beginDt, endDt))
                                        .findFirst().orElse(null);
        return (collectorItem != null);
    }

    protected boolean existsApprovedPRForCollectorItem(CollectorItem repoItem, Commit commit, CollectorItem collectorItem,
                                                       long beginDt, long endDt) {
        List<GitRequest> mergedPullRequests
                = gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(collectorItem.getId(), beginDt-1, endDt+1);

        if (CollectionUtils.isEmpty(mergedPullRequests)) { return false; }

        List<Commit> commits
                = commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(collectorItem.getId(), beginDt-1, endDt+1);

        GitRequest mergedPullRequestFound
                = Optional.ofNullable(mergedPullRequests)
                    .orElseGet(Collections::emptyList).stream()
                    .filter(mergedPullRequest -> evaluateMergedPullRequest(repoItem, mergedPullRequest, commit, commits))
                    .findFirst().orElse(null);

        return (mergedPullRequestFound != null);
    }

    private boolean evaluateMergedPullRequest (CollectorItem repoItem, GitRequest mergedPullRequest,
                                               Commit commit, List<Commit> commits) {
        Commit matchingCommit = findAMatchingCommit(mergedPullRequest, commit, commits);
        if (matchingCommit == null) { return false; }

        List<String> allPrCommitShas = new ArrayList<>();
        List<String> mergeCommitShas = new ArrayList<>();
        List<CodeReviewAuditResponse> allPeerReviews = new ArrayList<>();
        auditPullRequest(repoItem, mergedPullRequest, commits, mergeCommitShas, allPrCommitShas, allPeerReviews);
        CodeReviewAuditResponse codeReviewAuditResponse = allPeerReviews.get(0);

        if ((codeReviewAuditResponse != null)
                && codeReviewAuditResponseCheck(codeReviewAuditResponse)) { return true; }

        return false;
    }

    protected boolean codeReviewAuditResponseCheck(CodeReviewAuditResponse codeReviewAuditResponse) {
        for (CodeReviewAuditStatus status : codeReviewAuditResponse.getAuditStatuses()) {
            if ((status == CodeReviewAuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER)
                    || (status == CodeReviewAuditStatus.PULLREQ_NOT_PEER_REVIEWED)) {
                return false;
            }
        }
        return true;
    }

    protected Commit findAMatchingCommit(GitRequest mergedPullRequest, Commit commitToBeFound, List<Commit> commitsOnTheRepo) {
        List<Commit> commitsRelatedToPr = mergedPullRequest.getCommits();

        // So, will find the matching commit based on the criteria below for "Merge Only" case.
        Commit matchingCommit
                = Optional.ofNullable(commitsRelatedToPr)
                    .orElseGet(Collections::emptyList).stream()
                    .filter(commitRelatedToPr -> checkIfCommitsMatch(commitRelatedToPr, commitToBeFound))
                    .findFirst().orElse(null);

        // For "Squash and Merge", or a "Rebase and Merge":
        // The merged commit will not be part of the commits in the PR.
        // The PR will only have the original commits when the PR was opened.
        // Search for the commit in the list of commits on the repo in the db
        if (matchingCommit == null) {
            String pullNumber = mergedPullRequest.getNumber();
            matchingCommit = Optional.ofNullable(commitsOnTheRepo)
                            .orElseGet(Collections::emptyList).stream()
                            .filter(commitOnRepo -> Objects.equals(pullNumber, commitToBeFound.getPullNumber())
                                                    && checkIfCommitsMatch(commitOnRepo, commitToBeFound))
                            .findFirst().orElse(null);
        }

        return matchingCommit;
    }

    protected boolean checkIfCommitsMatch(Commit commit1, Commit commit2) {
        if (Objects.equals(commit1.getScmRevisionNumber(), commit2.getScmRevisionNumber())
                && Objects.equals(commit1.getScmAuthor(), commit2.getScmAuthor())
                && Objects.equals(commit1.getScmCommitTimestamp(), commit2.getScmCommitTimestamp())
                && Objects.equals(commit1.getScmCommitLog(), commit2.getScmCommitLog())) {
            return true;
        }
        return false;
    }

    private void auditServiceAccountChecks(CodeReviewAuditResponse codeReviewAuditResponse, Commit commit) {
        // Check for improper login info and add appropriate status.
        if (StringUtils.isEmpty(commit.getScmAuthorLDAPDN())) {
            codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.SCM_AUTHOR_LOGIN_INVALID);
        }

        auditDirectCommits(codeReviewAuditResponse, commit);
    }

    @SuppressWarnings("Duplicates")
    private void auditDirectCommits(CodeReviewAuditResponse codeReviewAuditResponse, Commit commit) {

        Stream<String> combinedStream = Stream.of(commit.getFilesAdded(), commit.getFilesModified(),commit.getFilesRemoved()).filter(Objects::nonNull).flatMap(Collection::stream);
        List<String> collectionCombined = combinedStream.collect(Collectors.toList());
        if (CommonCodeReview.checkForServiceAccount(commit.getScmAuthorLDAPDN(), settings,getAllServiceAccounts(),commit.getScmAuthor(),collectionCombined,true,codeReviewAuditResponse)) {
            codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.COMMITAUTHOR_EQ_SERVICEACCOUNT);
            // check for increment version tag and flag Direct commit by Service Account.
            auditIncrementVersionTag(codeReviewAuditResponse, commit, CodeReviewAuditStatus.DIRECT_COMMIT_NONCODE_CHANGE_SERVICE_ACCOUNT);
        }else if (StringUtils.isBlank(commit.getScmAuthorLDAPDN())) {
            // Status for commits without login information.
            auditIncrementVersionTag(codeReviewAuditResponse, commit, CodeReviewAuditStatus.DIRECT_COMMIT_NONCODE_CHANGE);
        }  else {
            // check for increment version tag and flag Direct commit by User Account.
            auditIncrementVersionTag(codeReviewAuditResponse, commit, CodeReviewAuditStatus.DIRECT_COMMIT_NONCODE_CHANGE_USER_ACCOUNT);
        }
    }

    private void auditIncrementVersionTag(CodeReviewAuditResponse codeReviewAuditResponse, Commit commit, CodeReviewAuditStatus directCommitIncrementVersionTagStatus) {
        if (CommonCodeReview.matchIncrementVersionTag(commit.getScmCommitLog(), settings)) {
            codeReviewAuditResponse.addAuditStatus(directCommitIncrementVersionTagStatus);
        } else {
            codeReviewAuditResponse.addAuditStatus(commit.isFirstEverCommit() ? CodeReviewAuditStatus.DIRECT_COMMITS_TO_BASE_FIRST_COMMIT : CodeReviewAuditStatus.DIRECT_COMMITS_TO_BASE);
        }
    }

    public Map<String,String> getAllServiceAccounts(){
        List<ServiceAccount> serviceAccounts = (List<ServiceAccount>) serviceAccountRepository.findAll();
        return serviceAccounts.stream().collect(Collectors.toMap(ServiceAccount :: getServiceAccountName, ServiceAccount::getFileNames));
    }
}
