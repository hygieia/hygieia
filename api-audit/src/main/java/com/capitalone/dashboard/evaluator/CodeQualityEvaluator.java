package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditStatus;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CollItemCfgHist;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollItemCfgHistRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.response.CodeQualityProfileValidationResponse;
import com.capitalone.dashboard.response.GenericAuditResponse;
import com.capitalone.dashboard.response.StaticAnalysisResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.capitalone.dashboard.response.GenericAuditResponse.STATIC_CODE_REVIEW;

@Component
public class CodeQualityEvaluator extends Evaluator {

    private final CustomRepositoryQuery customRepositoryQuery;
    private final CodeQualityRepository codeQualityRepository;
    private final CollItemCfgHistRepository collItemCfgHistRepository;

    @Autowired
    public CodeQualityEvaluator(CustomRepositoryQuery customRepositoryQuery, CodeQualityRepository codeQualityRepository, CollItemCfgHistRepository collItemCfgHistRepository) {
        this.customRepositoryQuery = customRepositoryQuery;
        this.codeQualityRepository = codeQualityRepository;
        this.collItemCfgHistRepository = collItemCfgHistRepository;
    }

    @Override
    public GenericAuditResponse evaluate(Dashboard dashboard, long beginDate, long endDate, Collection<?> dummy) throws HygieiaException {
        return  getCodeQualityAuditResponse(dashboard);

    }

    @Override
    public List<StaticAnalysisResponse> evaluate(CollectorItem collectorItem, long beginDate, long endDate, Collection<?> dummy) {
        return null;
    }


    /**
     * Calculates code quality audit response
     *
     * @param dashboard
     * @return @GenericAuditResponse for the code quality of a given @Dashboard
     * @throws HygieiaException
     */
    private GenericAuditResponse getCodeQualityAuditResponse(Dashboard dashboard) throws HygieiaException {
        List<CollectorItem> codeQualityItems = this.getCollectorItems(dashboard, "codeanalysis", CollectorType.CodeQuality);
        GenericAuditResponse genericAuditResponse = new GenericAuditResponse();
        if (CollectionUtils.isEmpty(codeQualityItems)) {
            genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_CODEQUALITY_NOT_CONFIGURED);
            return genericAuditResponse;
        }
        genericAuditResponse.addAuditStatus(AuditStatus.DASHBOARD_CODEQUALITY_CONFIGURED);
        CollectorItem codeQualityItem = codeQualityItems.get(0);
        List<CodeQuality> codeQualityDetails = codeQualityRepository.findByCollectorItemIdOrderByTimestampDesc(codeQualityItem.getCollectorId());
        StaticAnalysisResponse staticAnalysisResponse = this.getStaticAnalysisResponse(codeQualityDetails);
        genericAuditResponse.addResponse(STATIC_CODE_REVIEW, staticAnalysisResponse);
        //Commenting this out until Sonar Collector is updated to pull config changes
//			if(repoItems != null && !repoItems.isEmpty()){
//				for (CollectorItem repoItem : repoItems) {
//					String aRepoItembranch = (String) repoItem.getOptions().get("branch");
//					String aRepoItemUrl = (String) repoItem.getOptions().get("url");
//					List<Commit> repoCommits = getCommits(aRepoItemUrl, aRepoItembranch, beginDate, endDate);
//
//					CodeQualityProfileValidationResponse codeQualityProfileValidationResponse = this.qualityProfileAudit(repoCommits,codeQualityDetails,beginDate, endDate);
//					genericAuditResponse.setCodeQualityProfileValidationResponse(codeQualityProfileValidationResponse);
//				}
//			}

        return genericAuditResponse;
    }

    /**
     * Gets StaticAnalysisResponses for artifact
     *
     * @param projectName     Sonar Project Name
     * @param artifactVersion Artifact Version
     * @return List of StaticAnalysisResponse
     * @throws HygieiaException
     */
    public List<StaticAnalysisResponse> getCodeQualityAudit(String projectName,
                                                            String artifactVersion) throws HygieiaException {
        List<CodeQuality> qualities = codeQualityRepository
                .findByNameAndVersionOrderByTimestampDesc(projectName, artifactVersion);
        if (CollectionUtils.isEmpty(qualities))
            throw new HygieiaException("Empty CodeQuality collection", HygieiaException.BAD_DATA);
        StaticAnalysisResponse response = getStaticAnalysisResponse(qualities);
        return Collections.singletonList(response);
    }

    /**
     * Reusable method for constructing the StaticAnalysisResponse object for a
     *
     * @param codeQualities Code Quality List
     * @return StaticAnalysisResponse
     * @throws HygieiaException
     */
    private StaticAnalysisResponse getStaticAnalysisResponse(List<CodeQuality> codeQualities) throws HygieiaException {
        if (codeQualities == null)
            return new StaticAnalysisResponse();

        ObjectMapper mapper = new ObjectMapper();

        if (CollectionUtils.isEmpty(codeQualities))
            throw new HygieiaException("Empty CodeQuality collection", HygieiaException.BAD_DATA);
        CodeQuality returnQuality = codeQualities.get(0);

        StaticAnalysisResponse staticAnalysisResponse = new StaticAnalysisResponse();
        staticAnalysisResponse.setCodeQualityDetails(returnQuality);
        for (CodeQualityMetric metric : returnQuality.getMetrics()) {
            if (metric.getName().equalsIgnoreCase("quality_gate_details")) {
                TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
                };
                Map<String, String> values;
                try {
                    values = mapper.readValue((String) metric.getValue(), typeRef);
                    if (MapUtils.isNotEmpty(values) && values.containsKey("level")) {
                        String level = values.get("level");
                        if (level.equalsIgnoreCase("ok")) {
                            staticAnalysisResponse.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_OK);
                        } else {
                            staticAnalysisResponse.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_FAIL);
                        }
                    }
                    break;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        Set<AuditStatus> auditStatuses = staticAnalysisResponse.getAuditStatuses();
        if (!(auditStatuses.contains(AuditStatus.CODE_QUALITY_AUDIT_OK)
                || auditStatuses.contains(AuditStatus.CODE_QUALITY_AUDIT_FAIL))) {
            staticAnalysisResponse.addAuditStatus(AuditStatus.CODE_QUALITY_AUDIT_GATE_MISSING);
        }

        return staticAnalysisResponse;
    }

    /**
     * Retrieves code quality profile changeset for a given time period and
     * determines if change author matches commit author within time period
     *
     * @param repoUrl         SCM repo url
     * @param repoBranch      SCM repo branch
     * @param projectName     Sonar Project name
     * @param artifactVersion Artifact Version
     * @return CodeQualityProfileValidationResponse
     * @throws HygieiaException
     */

    public CodeQualityProfileValidationResponse getQualityGateValidationDetails(String repoUrl, String repoBranch,
                                                                                String projectName, String artifactVersion, long beginDate, long endDate) {

        List<Commit> commits = customRepositoryQuery.findByScmUrlAndScmBranchAndScmCommitTimestampGreaterThanEqualAndScmCommitTimestampLessThanEqual(repoUrl, repoBranch, beginDate, endDate);

        List<CodeQuality> codeQualities = codeQualityRepository
                .findByNameAndVersionOrderByTimestampDesc(projectName, artifactVersion);

        return this.qualityProfileAudit(commits, codeQualities, beginDate, endDate);

    }

    private CodeQualityProfileValidationResponse qualityProfileAudit(List<Commit> commits, List<CodeQuality> codeQualities, long beginDate, long endDate) {

        Set<String> authors = commits.stream().map(SCM::getScmAuthor).collect(Collectors.toSet());

        CodeQualityProfileValidationResponse codeQualityGateValidationResponse = new CodeQualityProfileValidationResponse();
        CodeQuality codeQuality = codeQualities.get(0);
        String url = codeQuality.getUrl();
        List<CollItemCfgHist> qualityProfileChanges = collItemCfgHistRepository
                .findByJobUrlAndTimestampBetweenOrderByTimestampDesc(url, beginDate - 1, endDate + 1);

        // If no change has been made to quality profile between the time range,
        // then return an audit status of no change
        // Need to differentiate between document not being found and whether
        // there was no change for the quality profile
        if (CollectionUtils.isEmpty(qualityProfileChanges)) {
            codeQualityGateValidationResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_NO_CHANGE);
        } else {

            // Iterate over all the change performers and check if they exist
            // within the authors set
            Set<String> qualityProfileChangePerformers = new HashSet<>();
            // TODO Improve this check as it is inefficient
// If the change performer matches a commit author, then fail
// the audit
            qualityProfileChanges.stream().map(CollItemCfgHist::getUserID).forEach(qualityProfileChangePerformer -> {
                qualityProfileChangePerformers.add(qualityProfileChangePerformer);
                if (authors.contains(qualityProfileChangePerformer)) {
                    codeQualityGateValidationResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_FAIL);
                }
            });
            // If there is no match between change performers and commit
            // authors, then pass the audit
            Set<AuditStatus> auditStatuses = codeQualityGateValidationResponse.getAuditStatuses();
            if (!(auditStatuses.contains(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_FAIL))) {
                codeQualityGateValidationResponse.addAuditStatus(AuditStatus.QUALITY_PROFILE_VALIDATION_AUDIT_OK);
            }
            codeQualityGateValidationResponse.setQualityGateChangePerformers(qualityProfileChangePerformers);
        }
        codeQualityGateValidationResponse.setCommitAuthors(authors);
        return codeQualityGateValidationResponse;
    }



}
