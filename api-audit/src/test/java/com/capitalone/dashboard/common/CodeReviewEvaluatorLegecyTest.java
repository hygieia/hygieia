package com.capitalone.dashboard.common;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.evaluator.CodeReviewEvaluatorLegacy;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
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
    private ApiSettings apiSettings;

    @Test
    public void evaluate_REPO_NOT_CONFIGURED(){
        CollectorItem c = null;
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(c,125634536,6235263,null);
        Assert.assertEquals(true,responseV2.get(0).getAuditStatuses().toString().contains("REPO_NOT_CONFIGURED"));
    }

    @Test
    public void evaluate_PENDING_DATA_COLLECTION(){
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(0),125634536,6235263,null);
        Assert.assertEquals(true,responseV2.get(0).getAuditStatuses().toString().contains("PENDING_DATA_COLLECTION"));
    }


    @Test
    public void evaluate_NO_PULL_REQ_FOR_DATE_RANGE() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(new ArrayList<Commit>());
        List<CodeReviewAuditResponse> responseV2 = codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1), 125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.get(0).getAuditStatuses().toString().contains("NO_PULL_REQ_FOR_DATE_RANGE"));
    }

    @Test
    public void evaluate_COMMITAUTHOR_EQ_SERVICEACCOUNT() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(Stream.of(makeCommit("Merge branch master into branch")).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountOU()).thenReturn("Service Accounts");
        when(apiSettings.getServiceAccountOU()).thenReturn("Service Accounts");
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1),125634536,6235263,null);
        Assert.assertEquals(false, responseV2.get(0).getAuditStatuses().toString().contains("COMMITAUTHOR_EQ_SERVICEACCOUNT"));
    }

    @Test
    public void evaluate_DIRECT_COMMIT_INCREMENT_VERSION_TAG_SERVICE_ACCOUNT() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(Stream.of(makeCommit("[Increment_Version_Tag] preparing 1.5.6")).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountOU()).thenReturn("Service Accounts");
        when(apiSettings.getServiceAccountOU()).thenReturn("Service Accounts");
        when(apiSettings.getCommitLogIgnoreAuditRegEx()).thenReturn("(.)*(Increment_Version_Tag)(.)*");
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1),125634536,6235263,null);
        Assert.assertEquals(true, responseV2.get(1).getAuditStatuses().toString().contains("DIRECT_COMMIT_NONCODE_CHANGE_SERVICE_ACCOUNT"));
    }

    @Test
    public void evaluate_DIRECT_COMMIT_INCREMENT_VERSION_TAG_NON_SERVICE_ACCOUNT() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(Stream.of(makeCommit("[Increment_Version_Tag] preparing 1.5.6")).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountOU()).thenReturn("User Accounts");
        when(apiSettings.getServiceAccountOU()).thenReturn("User Accounts");
        when(apiSettings.getCommitLogIgnoreAuditRegEx()).thenReturn("(.)*(Increment_Version_Tag)(.)*");
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1),125634536,6235263,null);
        Assert.assertEquals(true, responseV2.get(1).getAuditStatuses().toString().contains("DIRECT_COMMIT_NONCODE_CHANGE_USER_ACCOUNT"));
    }

    @Test
    public void evaluate_DIRECT_COMMIT_TO_BASE() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class),any(Long.class), any(Long.class))).thenReturn(Stream.of(makeCommitWithNoLDAP("[Increment_Version_Tag] preparing 1.5.6")).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountOU()).thenReturn("User Accounts");
        when(apiSettings.getServiceAccountOU()).thenReturn("User Accounts");
        when(apiSettings.getCommitLogIgnoreAuditRegEx()).thenReturn("(.)*(Increment_Version_Tag)(.)*");
        List<CodeReviewAuditResponse> responseV2 =  codeReviewEvaluatorLegacy.evaluate(makeCollectorItem(1),125634536,6235263,null);
        Assert.assertEquals(true, responseV2.get(1).getAuditStatuses().toString().contains("DIRECT_COMMITS_TO_BASE"));
    }



    private CollectorItem makeCollectorItem(int lastUpdated){
        CollectorItem item = new CollectorItem();
        item.setCollectorId(ObjectId.get());
        item.setEnabled(true);
        item.getOptions().put("url","http://github.com/capone/hygieia");
        item.getOptions().put("branch","master");
        item.setLastUpdated(lastUpdated);
        return item;

    }

    private Commit makeCommit(String message){
        Commit c = new Commit();
        c.setId(ObjectId.get());
        c.setScmCommitLog(message);
        c.setScmAuthorLDAPDN("CN=hygieiaUser,OU=Service Accounts,DC=basic,DC=ds,DC=industry,DC=com");
        c.setScmRevisionNumber("scmRevisionNumber1");
        c.setType(CommitType.New);
        return c;
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
