package com.capitalone.dashboard.common;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.evaluator.CodeReviewEvaluator;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponseV2;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CodeReviewEvaluatorTest {

    @InjectMocks
    private CodeReviewEvaluator codeReviewEvaluator;
    @Mock
    private CommitRepository commitRepository;
    @Mock
    private GitRequestRepository gitRequestRepository;

    @Mock
    private ApiSettings apiSettings;

    @Test
    public void evaluate_REPO_NOT_CONFIGURED() {
        CollectorItem c = null;
        CodeReviewAuditResponseV2 responseV2 = codeReviewEvaluator.evaluate(c, 125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("REPO_NOT_CONFIGURED"));
    }

    @Test
    public void evaluate_PENDING_DATA_COLLECTION() {
        CodeReviewAuditResponseV2 responseV2 = codeReviewEvaluator.evaluate(makeCollectorItem(0), 125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("PENDING_DATA_COLLECTION"));
    }


    @Test
    public void evaluate_NO_PULL_REQ_FOR_DATE_RANGE() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(new ArrayList<Commit>());
        CodeReviewAuditResponseV2 responseV2 = codeReviewEvaluator.evaluate(makeCollectorItem(1), 125634536, 6235263, null);
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("NO_PULL_REQ_FOR_DATE_RANGE"));
        Assert.assertEquals(true, responseV2.getAuditStatuses().toString().contains("NO_COMMIT_FOR_DATE_RANGE"));
    }

    @Test
    public void evaluate_COMMITAUTHOR_EQ_SERVICEACCOUNT() {
        when(gitRequestRepository.findByCollectorItemIdAndMergedAtIsBetween(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(new ArrayList<GitRequest>());
        when(commitRepository.findByCollectorItemIdAndScmCommitTimestampIsBetween(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(new ArrayList<Commit>());
        CodeReviewAuditResponseV2 responseV2 = codeReviewEvaluator.evaluate(makeCollectorItem(1), 125634536, 6235263, null);
        Assert.assertEquals(false, responseV2.getAuditStatuses().toString().contains("COMMITAUTHOR_EQ_SERVICEACCOUNT"));
    }

    private CollectorItem makeCollectorItem(int lastUpdated) {
        CollectorItem item = new CollectorItem();
        item.setCollectorId(ObjectId.get());
        item.setEnabled(true);
        item.getOptions().put("url", "http://github.com/capone/hygieia");
        item.getOptions().put("branch", "master");
        item.setLastUpdated(lastUpdated);
        return item;

    }
}
