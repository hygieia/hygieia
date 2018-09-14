package com.capitalone.dashboard.common;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.response.AuditReviewResponse;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonCodeReviewTest {

    private ApiSettings apiSettings = new ApiSettings();
    @Mock
    private CommitRepository commitRepository;
    @Test
    public void testCheckForServiceAccount() {

        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        Assert.assertEquals(true, CommonCodeReview.checkForServiceAccount("CN=hygieiaUser,OU=Service Accounts,DC=basic,DC=ds,DC=industry,DC=com", apiSettings));
    }

    @Test
    public void testCheckForServiceAccountForAllUsers() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        Assert.assertEquals(false, CommonCodeReview.checkForServiceAccount("CN=hygieiaUser,OU=Developers,OU=All Users,DC=basic,DC=ds,DC=industry,DC=com", apiSettings));
    }


    @Test
    public void testComputePeerReviewStatusForServiceAccount() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        apiSettings.setPeerReviewContexts("context");
        apiSettings.setPeerReviewApprovalText("approved by");
        AuditReviewResponse<CodeReviewAuditStatus> codeReviewAuditRequestAuditReviewResponse = new AuditReviewResponse<>();
        Assert.assertEquals(false, CommonCodeReview.computePeerReviewStatus(makeGitRequest("Service Accounts", true, "success"), apiSettings, codeReviewAuditRequestAuditReviewResponse, Stream.of(makeCommit()).collect(Collectors.toList()),commitRepository));
        Assert.assertEquals(true, codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().toString().contains("PEER_REVIEW_BY_SERVICEACCOUNT"));
    }

    @Test
    public void testComputePeerReviewStatusForAllUsers() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        apiSettings.setPeerReviewContexts("context");
        apiSettings.setPeerReviewApprovalText("approved by");
        AuditReviewResponse<CodeReviewAuditStatus> codeReviewAuditRequestAuditReviewResponse = new AuditReviewResponse<>();
        Assert.assertEquals(false, CommonCodeReview.computePeerReviewStatus(makeGitRequest("All Users", true, "success"), apiSettings, codeReviewAuditRequestAuditReviewResponse, Stream.of(makeCommit()).collect(Collectors.toList()),commitRepository));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_GHR));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_BY_SERVICEACCOUNT));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_GHR_SELF_APPROVAL));
    }

    @Test
    public void testComputePeerReviewStatusForAllUsers_LGTM_Statuses() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        apiSettings.setPeerReviewContexts("context");
        apiSettings.setPeerReviewApprovalText("approved by");
        AuditReviewResponse<CodeReviewAuditStatus> codeReviewAuditRequestAuditReviewResponse = new AuditReviewResponse<>();
        Assert.assertEquals(false, CommonCodeReview.computePeerReviewStatus(makeGitRequest("All Users", false, "pending,error,success,unknown"), apiSettings, codeReviewAuditRequestAuditReviewResponse, Stream.of(makeCommit()).collect(Collectors.toList()),commitRepository));

        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_LGTM_PENDING));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_LGTM_ERROR));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_LGTM_SUCCESS));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_LGTM_UNKNOWN));

        Assert.assertEquals(Boolean.FALSE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_GHR));
        Assert.assertEquals(Boolean.FALSE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_BY_SERVICEACCOUNT));
        Assert.assertEquals(Boolean.FALSE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_GHR_SELF_APPROVAL));
    }

    private GitRequest makeGitRequest(String userAccount, boolean withReviews, String status) {
        GitRequest pr = new GitRequest();
        pr.setCommitStatuses(makeCommitStatuses(status));
        if (withReviews) {
            pr.setReviews(Stream.of(makeReview()).collect(Collectors.toList()));
        }
        pr.setMergeAuthor("hygieiaUser");
        pr.setMergeAuthorLDAPDN("CN=hygieiaUser,OU=" + userAccount + ",DC=basic,DC=ds,DC=industry,DC=com");
        pr.setCommits(Stream.of(makeCommit()).collect(Collectors.toList()));
        pr.setScmBranch("master");
        pr.setSourceBranch("branch");

        return pr;
    }

    private Commit makeCommit() {
        Commit c = new Commit();
        c.setId(ObjectId.get());
        c.setScmCommitLog("Merge branch master into branch");
        c.setScmAuthorLDAPDN("CN=hygieiaUser,OU=Service Accounts,DC=basic,DC=ds,DC=industry,DC=com");
        return c;
    }

    private List<CommitStatus> makeCommitStatuses(String status) {
        List<CommitStatus> commitStatuses = new ArrayList<>();
        if (StringUtils.isEmpty(status)) {
            commitStatuses.add(makeCommitStatus("success"));
            return commitStatuses;
        }
        String[] statuses = status.trim().split(",");
        for (String st: statuses) {
            commitStatuses.add(makeCommitStatus(st));
        }
        return commitStatuses;
    }

    private CommitStatus makeCommitStatus(String status) {
        CommitStatus cs = new CommitStatus();
        cs.setState(status);
        cs.setContext("context");
        cs.setDescription("approved by hygieiaUser");
        return cs;
    }

    private Review makeReview() {
        Review r = new Review();
        r.setState("approved");
        r.setAuthorLDAPDN("CN=hygieiaUser,OU=Service Accounts,DC=basic,DC=ds,DC=industry,DC=com");
        return r;
    }
}
