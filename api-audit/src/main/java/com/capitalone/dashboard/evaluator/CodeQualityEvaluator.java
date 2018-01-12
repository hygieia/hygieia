package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorItemConfigHistory;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollItemConfigHistoryRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CodeQualityEvaluator extends Evaluator<CodeQualityAuditResponse> {

    private final CustomRepositoryQuery customRepositoryQuery;
    private final CodeQualityRepository codeQualityRepository;
    private final CollItemConfigHistoryRepository collItemConfigHistoryRepository;

    @Autowired
    public CodeQualityEvaluator(CustomRepositoryQuery customRepositoryQuery, CodeQualityRepository codeQualityRepository, CollItemConfigHistoryRepository collItemConfigHistoryRepository) {
        this.customRepositoryQuery = customRepositoryQuery;
        this.codeQualityRepository = codeQualityRepository;
        this.collItemConfigHistoryRepository = collItemConfigHistoryRepository;
    }


    @Override
    public Collection<CodeQualityAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Collection<?> data) throws AuditException {
        List<CodeQualityAuditResponse> responseV2s = new ArrayList<>();
        List<CollectorItem> codeanalysisItems = this.getCollectorItems(dashboard, "codeanalysis", CollectorType.CodeQuality);
        if (CollectionUtils.isEmpty(codeanalysisItems)) {
            throw new AuditException("No code quality job configured", AuditException.NO_COLLECTOR_ITEM_CONFIGURED);
        }

        List<CollectorItem> repoItems = this.getCollectorItems(dashboard, "repo", CollectorType.SCM);

        Set<String> codeAuthors = getCodeAuthors(repoItems, beginDate, endDate);


        for (CollectorItem codeanalysisItem : codeanalysisItems) {
            CodeQualityAuditResponse reviewResponse = evaluate(codeanalysisItem, beginDate, endDate, null);
            List<CollectorItemConfigHistory> configHistories = getProfileChanges(reviewResponse.getCodeQuality(), beginDate, endDate);
            if (CollectionUtils.isEmpty(configHistories)) {
                reviewResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_NO_CHANGE);
            }
            List<String> overlap = configHistories.stream().map(CollectorItemConfigHistory::getUserID).filter(codeAuthors::contains).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(overlap)) {
                reviewResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_FAIL);
            } else {
                reviewResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_OK);
            }
            responseV2s.add(reviewResponse);
        }
        return responseV2s;
    }

    private Set<String> getCodeAuthors(List<CollectorItem> repoItems, long beginDate, long endDate) {
        Set<String> authors = new HashSet<>();
        //making sure we have a goot url?
        repoItems.forEach(repoItem -> {
            String scmUrl = (String) repoItem.getOptions().get("url");
            String scmBranch = (String) repoItem.getOptions().get("branch");
            GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(scmUrl);
            String parsedUrl = gitHubParsed.getUrl(); //making sure we have a goot url?
            List<Commit> commits = customRepositoryQuery.findByScmUrlAndScmBranchAndScmCommitTimestampGreaterThanEqualAndScmCommitTimestampLessThanEqual(parsedUrl, scmBranch, beginDate, endDate);
            authors.addAll(commits.stream().map(SCM::getScmAuthor).collect(Collectors.toCollection(HashSet::new)));
        });
        return authors;
    }

    @Override
    public CodeQualityAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection<?> data) throws AuditException {
        List<CodeQuality> codeQualityDetails = codeQualityRepository.findByCollectorItemIdOrderByTimestampDesc(collectorItem.getCollectorId());
        return getStaticAnalysisResponse(codeQualityDetails);
    }


    /**
     * Reusable method for constructing the CodeQualityAuditResponse object for a
     *
     * @param codeQualities Code Quality List
     * @return CodeQualityAuditResponse
     * @throws AuditException
     */
    private CodeQualityAuditResponse getStaticAnalysisResponse(List<CodeQuality> codeQualities) throws AuditException {
        ObjectMapper mapper = new ObjectMapper();

        if (CollectionUtils.isEmpty(codeQualities))
            throw new AuditException("Empty CodeQuality collection", AuditException.MISSING_DETAILS);
        //get the latest
        codeQualities.sort(Comparator.comparingLong(CodeQuality::getTimestamp));

        CodeQuality returnQuality = codeQualities.get(0);
        CodeQualityAuditResponse codeQualityAuditResponse = new CodeQualityAuditResponse();
        codeQualityAuditResponse.setCodeQuality(returnQuality);
        for (CodeQualityMetric metric : returnQuality.getMetrics()) {
            //TODO: This is sonar specific - need to move this to api settings via properties file
            if (metric.getName().equalsIgnoreCase("quality_gate_details")) {
                TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
                };
                Map<String, String> values;
                try {
                    values = mapper.readValue((String) metric.getValue(), typeRef);
                    if (MapUtils.isNotEmpty(values) && values.containsKey("level")) {
                        String level = values.get("level");
                        codeQualityAuditResponse.addAuditStatus(level.equalsIgnoreCase("ok") ? AuditStatus.CODE_QUALITY_AUDIT_OK : AuditStatus.CODE_QUALITY_AUDIT_FAIL);
                    }
                    break;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        Set<AuditStatus> auditStatuses = codeQualityAuditResponse.getAuditStatuses();
        if (!(auditStatuses.contains(AuditStatus.CODE_QUALITY_AUDIT_OK)
                || auditStatuses.contains(AuditStatus.CODE_QUALITY_AUDIT_FAIL))) {
            codeQualityAuditResponse.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_GATE_MISSING);
        }

        return codeQualityAuditResponse;
    }



    private List<CollectorItemConfigHistory> getProfileChanges(CodeQuality codeQuality, long beginDate, long endDate) {
        return collItemConfigHistoryRepository
                .findByCollectorItemIdAndTimestampBetweenOrderByTimestampDesc(codeQuality.getCollectorItemId(), beginDate - 1, endDate + 1);
    }
}
