package com.capitalone.dashboard.service;

import com.capitalone.dashboard.evaluator.CodeReviewEvaluatorLegacy;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CodeReviewAuditServiceImpl implements CodeReviewAuditService {


    private final CollectorRepository collectorRepository;

    private final CollectorItemRepository collectorItemRepository;
    private final CodeReviewEvaluatorLegacy codeReviewEvaluatorLegacy;


//    private static final Log LOGGER = LogFactory.getLog(CodeReviewAuditServiceImpl.class);

    @Autowired
    public CodeReviewAuditServiceImpl(CollectorRepository collectorRepository, CollectorItemRepository collectorItemRepository, CodeReviewEvaluatorLegacy codeReviewEvaluatorLegacy) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;

        this.codeReviewEvaluatorLegacy = codeReviewEvaluatorLegacy;
    }


    /**
     * Calculates peer review response
     *
     * @param repoUrl
     * @param repoBranch
     * @param scmName
     * @param beginDt
     * @param endDt
     * @return
     */
    @Override
    public Collection<CodeReviewAuditResponse> getPeerReviewResponses(String repoUrl, String repoBranch, String scmName,
                                                                      long beginDt, long endDt) throws AuditException {
        Collector githubCollector = collectorRepository.findByName(!StringUtils.isEmpty(scmName) ? scmName : "GitHub");
        CollectorItem collectorItem = collectorItemRepository.findRepoByUrlAndBranch(githubCollector.getId(),
                repoUrl, repoBranch, true);

        return codeReviewEvaluatorLegacy.evaluate(collectorItem, beginDt, endDt, null);
    }

}
