package com.capitalone.dashboard.common;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.response.AuditReviewResponse;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonCodeReviewTest {

    private ApiSettings apiSettings = new ApiSettings();
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
        Assert.assertEquals(false, CommonCodeReview.computePeerReviewStatus(makeGitRequest("Service Accounts"), apiSettings, codeReviewAuditRequestAuditReviewResponse, Stream.of(makeCommit()).collect(Collectors.toList())));
        Assert.assertEquals(true, codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().toString().contains("PEER_REVIEW_BY_SERVICEACCOUNT"));
    }

    @Test
    public void testComputePeerReviewStatusForAllUsers() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        apiSettings.setPeerReviewContexts("context");
        apiSettings.setPeerReviewApprovalText("approved by");
        AuditReviewResponse<CodeReviewAuditStatus> codeReviewAuditRequestAuditReviewResponse = new AuditReviewResponse<>();
        Assert.assertEquals(false, CommonCodeReview.computePeerReviewStatus(makeGitRequest("All Users"), apiSettings, codeReviewAuditRequestAuditReviewResponse, Stream.of(makeCommit()).collect(Collectors.toList())));
        Assert.assertEquals(false, codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().toString().contains("PEER_REVIEW_BY_SERVICEACCOUNT"));
    }

    private GitRequest makeGitRequest(String userAccount) {
        GitRequest pr = new GitRequest();
        pr.setCommitStatuses(Stream.of(makeCommitStatus()).collect(Collectors.toList()));
        pr.setReviews(Stream.of(makeReview()).collect(Collectors.toList()));
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

    private CommitStatus makeCommitStatus() {
        CommitStatus cs = new CommitStatus();
        cs.setState("success");
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
