package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStage;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.response.DeployAuditResponse;
import com.capitalone.dashboard.status.DeployAuditStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class DeployEvaluator extends Evaluator<DeployAuditResponse> {

    private final BuildRepository buildRepository;
    private final ApiSettings apiSettings;
    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";

    @Autowired
    public DeployEvaluator(BuildRepository buildRepository, ApiSettings apiSettings) {
        this.buildRepository = buildRepository;
        this.apiSettings = apiSettings;
    }


    @Override
    public Collection<DeployAuditResponse> evaluate(Dashboard dashboard, long beginDate, long endDate, Map<?, ?> data) throws AuditException {
        List<CollectorItem> buildItems = getCollectorItems(dashboard, "build", CollectorType.Build);
        if (CollectionUtils.isEmpty(buildItems)) {
            return Arrays.asList(getErrorResponse(DeployAuditStatus.COLLECTOR_ITEM_ERROR));
        }
        return buildItems.stream().map(item -> evaluate(item, beginDate, endDate, null)).collect(Collectors.toList());
    }

    @Override
    public DeployAuditResponse evaluate(CollectorItem collectorItem, long beginDate, long endDate, Map<?, ?> data) {
        return getDeployAuditResponse(collectorItem, beginDate, endDate, apiSettings);
    }

    /**
     * Calculates Audit Response for a given dashboard
     *
     * @param beginDate
     * @param endDate
     * @return DeployAuditResponse for the build job for a given dashboard, begin and end date
     */
    private DeployAuditResponse getDeployAuditResponse(CollectorItem buildItem, long beginDate, long endDate, ApiSettings apiSettings) {
        DeployAuditResponse deployAuditResponse = new DeployAuditResponse();
        Build build = buildRepository.findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(buildItem.getId(), beginDate - 1, endDate + 1);
        if (Objects.isNull(build)) return getErrorResponse(DeployAuditStatus.NO_ACTIVITY);
        if (Objects.nonNull(build)) {
            List<BuildStage> stages = build.getStages();
            if (matchStage(stages, SUCCESS, apiSettings)) {
                deployAuditResponse.addAuditStatus(DeployAuditStatus.DEPLOY_SCRIPTS_FOUND_TESTED);
            } else if (matchStage(stages, FAILED, apiSettings)) {
                deployAuditResponse.addAuditStatus(DeployAuditStatus.DEPLOY_SCRIPTS_FOUND_NOT_TESTED);
            } else {
                deployAuditResponse.addAuditStatus(DeployAuditStatus.DEPLOYMENT_SCRIPTS_TEST_NOT_FOUND);
            }
            deployAuditResponse.setBuild(build);
            deployAuditResponse.setLastBuildTime(build.getEndTime());
            deployAuditResponse.setLastUpdated(build.getTimestamp());
        }
        return deployAuditResponse;
    }

    private DeployAuditResponse getErrorResponse(DeployAuditStatus auditStatus) {
        DeployAuditResponse deployAuditResponse = new DeployAuditResponse();
        deployAuditResponse.addAuditStatus(auditStatus);
        deployAuditResponse.setErrorMessage("Unable to register in Hygieia- Deployment scripts not configured");
        return deployAuditResponse;
    }

    public boolean matchStage(List<BuildStage> stages, String status, ApiSettings settings) {
        if (StringUtils.isEmpty(settings.getBuildStageRegEx())) return false;
        return stages.stream().filter(s -> Pattern.compile(settings.getBuildStageRegEx()).matcher(s.getName()).find() && s.getStatus().equalsIgnoreCase(status)).findAny().isPresent();
    }

}
