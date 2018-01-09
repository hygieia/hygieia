package com.capitalone.dashboard.service;

import com.capitalone.dashboard.evaluator.PeerReviewEvaluator;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.response.PeerReviewResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PeerReviewAuditServiceImpl implements PeerReviewAuditService {


    private final CollectorRepository collectorRepository;

    private final CollectorItemRepository collectorItemRepository;
    private final PeerReviewEvaluator peerReviewEvaluator;


//    private static final Log LOGGER = LogFactory.getLog(PeerReviewAuditServiceImpl.class);

    @Autowired
    public PeerReviewAuditServiceImpl(CollectorRepository collectorRepository, CollectorItemRepository collectorItemRepository, PeerReviewEvaluator peerReviewEvaluator) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;

        this.peerReviewEvaluator = peerReviewEvaluator;
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
    public Collection<PeerReviewResponse> getPeerReviewResponses(String repoUrl, String repoBranch, String scmName,
                                                                 long beginDt, long endDt) {
        Collector githubCollector = collectorRepository.findByName(!StringUtils.isEmpty(scmName) ? scmName : "GitHub");
        CollectorItem collectorItem = collectorItemRepository.findRepoByUrlAndBranch(githubCollector.getId(),
                repoUrl, repoBranch, true);

        return peerReviewEvaluator.evaluate(collectorItem, beginDt, endDt, null);
    }

}
