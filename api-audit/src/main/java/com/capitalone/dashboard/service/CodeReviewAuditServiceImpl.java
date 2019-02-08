package com.capitalone.dashboard.service;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.evaluator.CodeReviewEvaluatorLegacy;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CodeReviewAuditServiceImpl implements CodeReviewAuditService {

    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final CodeReviewEvaluatorLegacy codeReviewEvaluatorLegacy;
    private final ApiSettings settings;
    private static final String BRANCH = "branch";
    private static final String REPO_URL = "url";

    @Autowired
    public CodeReviewAuditServiceImpl(CollectorRepository collectorRepository,
                                      CollectorItemRepository collectorItemRepository,
                                      CodeReviewEvaluatorLegacy codeReviewEvaluatorLegacy,
                                      ApiSettings settings) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.codeReviewEvaluatorLegacy = codeReviewEvaluatorLegacy;
        this.settings = settings;
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
                                                                      long beginDt, long endDt) {
        Collector githubCollector = collectorRepository.findByName(!StringUtils.isEmpty(scmName) ? scmName : "GitHub");
        CollectorItem collectorItem = collectorItemRepository.findRepoByUrlAndBranch(githubCollector.getId(), repoUrl, repoBranch, true);

        // This is the list of repos in the database with the same repoUrl, but a different branch.
        List<CollectorItem> filteredCollectorItemList = new ArrayList<>();
        if ((collectorItem != null) && (collectorItem.isPushed())) {
            List<ObjectId> collectorIdList = new ArrayList<>();
            collectorIdList.add(githubCollector.getId());

            Iterable<CollectorItem> collectorItemIterable
                    = collectorItemRepository.findAllByOptionNameValueAndCollectorIdsIn(REPO_URL, repoUrl, collectorIdList);

            for (CollectorItem ci : collectorItemIterable) {
                if (repoBranch.equalsIgnoreCase((String) ci.getOptions().get(BRANCH))) { continue; }

                filteredCollectorItemList.add(ci);
            }

            return codeReviewEvaluatorLegacy.evaluate(collectorItem, filteredCollectorItemList, beginDt, endDt, null);
        }

        return codeReviewEvaluatorLegacy.evaluate(collectorItem, beginDt, endDt, null);
    }
}
