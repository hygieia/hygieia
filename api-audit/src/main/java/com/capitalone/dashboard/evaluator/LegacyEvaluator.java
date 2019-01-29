package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;

import java.util.Collection;
import java.util.List;

public abstract class LegacyEvaluator {
    public abstract List<CodeReviewAuditResponse> evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection<?> data) throws AuditException;
    public abstract List<CodeReviewAuditResponse> evaluate(CollectorItem collectorItem, List<CollectorItem> collectorItemList, long beginDate, long endDate, Collection<?> data) throws AuditException;
}
