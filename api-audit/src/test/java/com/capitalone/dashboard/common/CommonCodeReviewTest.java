package com.capitalone.dashboard.common;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.model.ServiceAccount;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ServiceAccountRepository;
import com.capitalone.dashboard.response.AuditReviewResponse;
import com.capitalone.dashboard.status.CodeReviewAuditStatus;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public class CommonCodeReviewTest {

    private ApiSettings apiSettings = new ApiSettings();
    @Mock
    private CommitRepository commitRepository;
    @Mock
    private ServiceAccountRepository serviceAccountRepository;
    @Test
    public void testCheckForServiceAccount() {

        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        Assert.assertEquals(true, CommonCodeReview.checkForServiceAccount("CN=hygieiaUser,OU=Service Accounts,DC=basic,DC=ds,DC=industry,DC=com", apiSettings,null,null,null,false,new AuditReviewResponse()));
    }

    @Test
    public void testCheckForServiceAccountForAllUsers() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        Assert.assertEquals(false, CommonCodeReview.checkForServiceAccount("CN=hygieiaUser,OU=Developers,OU=All Users,DC=basic,DC=ds,DC=industry,DC=com", apiSettings,null,null,null,false,new AuditReviewResponse()));
    }


    @Test
    public void testCheckForServiceAccountForAllowedServiceAccountsMatch() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        Map<String,String> allowedUsers =  Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("allowedUser1", "pom.xml,test.json"),
                new AbstractMap.SimpleEntry<>("allowedUser2", "test.java"))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
        Assert.assertEquals(true, CommonCodeReview.checkForServiceAccount("CN=hygieiaUser,OU=Developers,OU=All Users,DC=basic,DC=ds,DC=industry,DC=com", apiSettings,allowedUsers,"allowedUser1",Stream.of("test.json").collect(Collectors.toList()), true,new AuditReviewResponse()));
    }

    @Test
    public void testCheckForServiceAccountForAllowedServiceAccountsWildcardMatch() {
        AuditReviewResponse<CodeReviewAuditStatus> auditStatusAuditReviewResponse = new AuditReviewResponse();
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        Map<String,String> allowedUsers =  Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("allowedUser1", "pom.xml,test.json"),
                new AbstractMap.SimpleEntry<>("allowedUser2", "*.java"))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
        Assert.assertEquals(true, CommonCodeReview.checkForServiceAccount("CN=hygieiaUser,OU=Developers,OU=All Users,DC=basic,DC=ds,DC=industry,DC=com", apiSettings,allowedUsers,"allowedUser2",Stream.of("test.java").collect(Collectors.toList()), true,auditStatusAuditReviewResponse));
        Assert.assertEquals(true, auditStatusAuditReviewResponse.getAuditStatuses().toString().contains("DIRECT_COMMIT_CHANGE_WHITELISTED_ACCOUNT"));
    }

    @Test
    public void testCheckForServiceAccountForAllowedServiceAccountsNonMatch() {
        AuditReviewResponse<CodeReviewAuditStatus> auditStatusAuditReviewResponse = new AuditReviewResponse();
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        Map<String,String> allowedUsers =  Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("allowedUser1", "pom.xml,test.json"),
                new AbstractMap.SimpleEntry<>("allowedUser2", "*.java"))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
        Assert.assertEquals(false, auditStatusAuditReviewResponse.getAuditStatuses().toString().contains("DIRECT_COMMIT_CHANGE_WHITELISTED_ACCOUNT"));
        Assert.assertEquals(false, CommonCodeReview.checkForServiceAccount("CN=hygieiaUser,OU=Developers,OU=All Users,DC=basic,DC=ds,DC=industry,DC=com", apiSettings,allowedUsers,"allowedUser2",Stream.of("test.md").collect(Collectors.toList()), true,auditStatusAuditReviewResponse));
    }


    @Test
    public void testComputePeerReviewStatusForServiceAccount() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        apiSettings.setPeerReviewContexts("context");
        apiSettings.setPeerReviewApprovalText("approved by");
        Mockito.when(serviceAccountRepository.findAll()).thenReturn(Stream.of(makeServiceAccount()).collect(Collectors.toList()));
        AuditReviewResponse<CodeReviewAuditStatus> codeReviewAuditRequestAuditReviewResponse = new AuditReviewResponse<>();
        Assert.assertEquals(false, CommonCodeReview.computePeerReviewStatus(makeGitRequest("Service Accounts"), apiSettings, codeReviewAuditRequestAuditReviewResponse, Stream.of(makeCommit()).collect(Collectors.toList()),commitRepository,serviceAccountRepository));
        Assert.assertEquals(true, codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().toString().contains("PEER_REVIEW_BY_SERVICEACCOUNT"));
    }

    @Test
    public void testComputePeerReviewStatusForAllUsers() {
        apiSettings.setServiceAccountOU(TestConstants.SERVICE_ACCOUNTS);
        apiSettings.setPeerReviewContexts("context");
        apiSettings.setPeerReviewApprovalText("approved by");
        Mockito.when(serviceAccountRepository.findAll()).thenReturn(Stream.of(makeServiceAccount()).collect(Collectors.toList()));
        AuditReviewResponse<CodeReviewAuditStatus> codeReviewAuditRequestAuditReviewResponse = new AuditReviewResponse<>();
        Assert.assertEquals(false, CommonCodeReview.computePeerReviewStatus(makeGitRequest("All Users"), apiSettings, codeReviewAuditRequestAuditReviewResponse, Stream.of(makeCommit()).collect(Collectors.toList()),commitRepository,serviceAccountRepository));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_GHR));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_BY_SERVICEACCOUNT));
        Assert.assertEquals(Boolean.TRUE,codeReviewAuditRequestAuditReviewResponse.getAuditStatuses().contains(CodeReviewAuditStatus.PEER_REVIEW_GHR_SELF_APPROVAL));
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

    private ServiceAccount makeServiceAccount(){
        return new ServiceAccount("servUserName","pom.xml,test.json");
    }
}
