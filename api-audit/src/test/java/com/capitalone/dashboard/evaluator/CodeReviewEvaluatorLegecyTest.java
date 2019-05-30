package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.common.TestConstants;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.model.ServiceAccount;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.ServiceAccountRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CodeReviewEvaluatorLegecyTest {

    @InjectMocks
    private CodeReviewEvaluatorLegacy codeReviewEvaluatorLegacy;
    @Mock
    private CommitRepository commitRepository;
    @Mock
    private GitRequestRepository gitRequestRepository;
    @Mock
    private ServiceAccountRepository serviceAccountRepository;

    @Mock
    private ApiSettings apiSettings;

    @Test
    public void evaluate_REPO_NOT_CONFIGURED() {
        CollectorItem c = null;
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(c,125634536,6235263,null);
        Assert.assertEquals(true,responseV2.get(0).getAuditStatuses().toString().contains("REPO_NOT_CONFIGURED"));
    }

    @Test
    public void evaluate_PENDING_DATA_COLLECTION(){
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(0, "master"),125634536,6235263,null);
        Assert.assertEquals(true,responseV2.get(0).getAuditStatuses().toString().contains("PENDING_DATA_COLLECTION"));
    }

    @Test
    public void evaluate_NO_PULL_REQ_FOR_DATE_RANGE() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(new ArrayList<Commit>());
        List<CodeReviewAuditResponse> responseV2 = codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1, "master"),125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.get(0).getAuditStatuses().toString().contains("NO_PULL_REQ_FOR_DATE_RANGE"));
    }

    @Test
    public void evaluate_COMMITAUTHOR_EQ_SERVICEACCOUNT() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(Stream.of(makeCommit("Merge branch master into branch", "scmRevisionNumber1", null, null,0L)).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountOU()).thenReturn(TestConstants.SERVICE_ACCOUNTS);
        when(serviceAccountRepository.findAll()).thenReturn(Stream.of(makeServiceAccount()).collect(Collectors.toList()));
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1, "master"),125634536,6235263,null);
        Assert.assertEquals(false, responseV2.get(0).getAuditStatuses().toString().contains("COMMITAUTHOR_EQ_SERVICEACCOUNT"));
    }

    @Test
    public void evaluate_COMMITAUTHOR_EQ_SERVICEACCOUNT_WithAllowedUsers() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(Stream.of(makeCommit("Merge branch master into branch","servUserName",null, null,0L)).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountOU()).thenReturn(TestConstants.SERVICE_ACCOUNTS);
        when(serviceAccountRepository.findAll()).thenReturn(Stream.of(makeServiceAccount()).collect(Collectors.toList()));

        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1, "master"),125634536,6235263,null);
        Assert.assertEquals(true, responseV2.get(1).getAuditStatuses().toString().contains("COMMITAUTHOR_EQ_SERVICEACCOUNT"));
    }

    @Test
    public void evaluate_DIRECT_COMMIT_INCREMENT_VERSION_TAG_SERVICE_ACCOUNT() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(Stream.of(makeCommit("[Increment_Version_Tag] preparing 1.5.6", "scmRevisionNumber1", null, null,0L)).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountOU()).thenReturn(TestConstants.SERVICE_ACCOUNTS);
        when(apiSettings.getCommitLogIgnoreAuditRegEx()).thenReturn("(.)*(Increment_Version_Tag)(.)*");
        when(serviceAccountRepository.findAll()).thenReturn(Stream.of(makeServiceAccount()).collect(Collectors.toList()));

        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1, "master"),125634536,6235263,null);
        Assert.assertEquals(true, responseV2.get(1).getAuditStatuses().toString().contains("DIRECT_COMMIT_NONCODE_CHANGE_SERVICE_ACCOUNT"));
    }

    @Test
    public void evaluate_DIRECT_COMMIT_INCREMENT_VERSION_TAG_NON_SERVICE_ACCOUNT() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(Stream.of(makeCommit("[Increment_Version_Tag] preparing 1.5.6", "scmRevisionNumber1", null, null,0L)).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountOU()).thenReturn(TestConstants.USER_ACCOUNTS);
        when(apiSettings.getCommitLogIgnoreAuditRegEx()).thenReturn("(.)*(Increment_Version_Tag)(.)*");
        when(serviceAccountRepository.findAll()).thenReturn(Stream.of(makeServiceAccount()).collect(Collectors.toList()));

        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1, "master"),125634536,6235263,null);
        Assert.assertEquals(true, responseV2.get(1).getAuditStatuses().toString().contains("DIRECT_COMMIT_NONCODE_CHANGE_USER_ACCOUNT"));
    }

    @Test
    public void evaluate_DIRECT_COMMIT_TO_BASE() {
        List<CollectorItem> collectorItemList = new ArrayList<>();
        collectorItemList.add(makeCollectorItem(2, "feature"));
        collectorItemList.add(makeCollectorItem(3, "develop"));

        List<GitRequest> pullRequestList = makePullRequests(true);
        List<Commit> commitsList = makeCommits();

        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(pullRequestList);
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(commitsList);
        when(apiSettings.getServiceAccountOU()).thenReturn(TestConstants.USER_ACCOUNTS);
        when(apiSettings.getCommitLogIgnoreAuditRegEx()).thenReturn("(.)*(Increment_Version_Tag)(.)*");
        when(serviceAccountRepository.findAll()).thenReturn(Stream.of(makeServiceAccount()).collect(Collectors.toList()));

        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1, "master"), collectorItemList,125634536,6235263,null);
        Assert.assertTrue(responseV2.get(0).getAuditStatuses().contains(CodeReviewAuditStatus.DIRECT_COMMITS_TO_BASE));

        pullRequestList.get(0).setUserId("NotAuthor1");
        responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1, "master"), collectorItemList,125634536,6235263,null);
        Assert.assertFalse(responseV2.get(0).getAuditStatuses().contains(CodeReviewAuditStatus.DIRECT_COMMITS_TO_BASE));
    }

    @Test
    public void existsApprovedPRForCollectorItemTest() {
        List<GitRequest> pullRequestList = makePullRequests(true);
        List<Commit> commitsList = makeCommits();
        Commit commit = makeCommit("Merge branch 'master' into branch", "CommitOid1", "Author1", "Author1",12345678L);
        CollectorItem collectorItem = makeCollectorItem(12345678, "master");

        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(pullRequestList);
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(commitsList);
        when(serviceAccountRepository.findAll()).thenReturn(Stream.of(makeServiceAccount()).collect(Collectors.toList()));

        CodeReviewEvaluatorLegacy codeReviewEvaluatorLegacyInstance = new CodeReviewEvaluatorLegacy(commitRepository, gitRequestRepository, serviceAccountRepository, apiSettings);

        pullRequestList.get(0).setUserId("NotAuthor1");
        boolean result = codeReviewEvaluatorLegacyInstance.existsApprovedPRForCollectorItem(collectorItem, commit, collectorItem, 12345678L, 12345679L);
        Assert.assertTrue(result);

        pullRequestList = makePullRequests(false);
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(pullRequestList);

        result = codeReviewEvaluatorLegacyInstance.existsApprovedPRForCollectorItem(collectorItem, commit, collectorItem, 12345678L, 12345679L);
        Assert.assertFalse(result);
    }

    @Test
    public void codeReviewAuditResponseCheckTest() {
        CodeReviewAuditResponse codeReviewAuditResponse = new CodeReviewAuditResponse();
        codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.COMMITAUTHOR_EQ_MERGECOMMITER);
        codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.PULLREQ_REVIEWED_BY_PEER);

        Assert.assertFalse(codeReviewEvaluatorLegacy.codeReviewAuditResponseCheck(codeReviewAuditResponse));

        codeReviewAuditResponse = new CodeReviewAuditResponse();
        codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.COMMITAUTHOR_NE_MERGECOMMITER);
        codeReviewAuditResponse.addAuditStatus(CodeReviewAuditStatus.PULLREQ_REVIEWED_BY_PEER);

        Assert.assertTrue(codeReviewEvaluatorLegacy.codeReviewAuditResponseCheck(codeReviewAuditResponse));
    }

    @Test
    public void findAMatchingCommitTest() {
        List<GitRequest> pullRequestList = makePullRequests(false);
        Commit commit = makeCommit("Merge branch 'master' into branch", "CommitOid1", "Author1", "Author1",12345678L);

        Commit matchingCommit = codeReviewEvaluatorLegacy.findAMatchingCommit(pullRequestList.get(0), commit, null);
        Assert.assertNotNull(matchingCommit);

        commit = makeCommit("Commit 3", "CommitOid3", "Author3", "Author3",12345678L);
        List<Commit> commitList = makeCommits();
        matchingCommit = codeReviewEvaluatorLegacy.findAMatchingCommit(pullRequestList.get(0), commit, commitList);
        Assert.assertNotNull(matchingCommit);
    }

    @Test
    public void checkIfCommitsMatchTest() {
        Commit commit1 = makeCommit("Commit 21", "CommitOid21", "Author12", "Author12",12345678L);
        Commit commit2 = makeCommit("Commit 31", "CommitOid21", "Author12", "Author12",12345678L);

        Assert.assertFalse(codeReviewEvaluatorLegacy.checkIfCommitsMatch(commit1, commit2));

        commit1 = makeCommit("Commit 31", "CommitOid21", "Author12", "Author12",12345678L);
        Assert.assertTrue(codeReviewEvaluatorLegacy.checkIfCommitsMatch(commit1, commit2));
    }

    private List<GitRequest> makePullRequests(boolean withApprovedReview) {
        List<GitRequest> pullRequestList = new ArrayList<>();

        GitRequest pr1 = new GitRequest();
        pullRequestList.add(pr1);
        pr1.setUserId("Author1");
        pr1.setScmBranch("master");
        pr1.setSourceBranch("branch");
        pr1.setScmRevisionNumber("CommitOid1");

        List<Commit> commitsList1 = new ArrayList<>();
        pr1.setCommits(commitsList1);

        commitsList1.add(makeCommit("Merge branch 'master' into branch", "CommitOid1", "Author1", "Author1",12345678L));

        if (withApprovedReview) {
            pr1.setReviews(makeReviews());
        }
        return pullRequestList;
    }

    private List<Review> makeReviews() {
        List<Review> reviewList = new ArrayList<>();
        Review review = new Review();
        reviewList.add(review);
        review.setState("approved");
        return reviewList;
    }

    private List<Commit> makeCommits() {
        List<Commit> commitsList = new ArrayList<>();
        commitsList.add(makeCommit("Merge branch master into branch", "CommitOid1", "Author1", "Author1",12345678L));
        commitsList.add(makeCommit("Commit 21", "CommitOid21", "Author12", "Author12",12345678L));
        commitsList.add(makeCommit("Commit 3", "CommitOid3", "Author3", "Author3",12345678L));

        return commitsList;
    }

    private CollectorItem makeCollectorItem(int lastUpdated, String branch){
        CollectorItem item = new CollectorItem();
        item.setCollectorId(ObjectId.get());
        item.setEnabled(true);
        item.setPushed(true);
        item.getOptions().put("url","http://github.com/capone/hygieia");
        item.getOptions().put("branch",branch);
        item.setLastUpdated(lastUpdated);
        return item;

    }

    private Commit makeCommit(String message, String scmRevisionNumber, String author, String committer, long timeStamp){
        Commit c = new Commit();
        c.setId(ObjectId.get());
        c.setScmCommitLog(message);
        c.setScmAuthorLDAPDN("CN=hygieiaUser,OU=Service Accounts,DC=basic,DC=ds,DC=industry,DC=com");
        c.setScmRevisionNumber(scmRevisionNumber);
        c.setType(CommitType.New);
        c.setScmAuthor(author);
        c.setScmAuthorLogin(author);
        c.setScmCommitterLogin(committer);
        c.setScmCommitTimestamp(timeStamp);
        c.setFilesModified(Arrays.asList("pom.xml"));
        c.setFilesRemoved(Arrays.asList("cucumber.xml"));
        c.setFilesAdded(Arrays.asList("package.json"));
        c.setNumberOfChanges(1);
        return c;
    }

    private ServiceAccount makeServiceAccount(){
        return  new ServiceAccount("servUserName","pom.xml,cucumber.json");
    }

    private Commit makeCommitWithNoLDAP(String message){
        Commit c = new Commit();
        c.setId(ObjectId.get());
        c.setScmCommitLog(message);
        c.setScmRevisionNumber("scmRevisionNumber1");
        c.setType(CommitType.New);
        return c;
    }
}
