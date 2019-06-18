package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStage;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CollectionError;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.response.DeployAuditResponse;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class DeployEvaluatorTest {
    @InjectMocks
    private DeployEvaluator deployEvaluator;
    @Mock
    private BuildRepository buildRepository;
    @Mock
    private ApiSettings apiSettings;

    private DeployAuditResponse response;


    @Test
    public void testEvaluate_CollectorItemError_Artifact_NULL() {
        response = deployEvaluator.evaluate(getCollectorItem("testGenericItem", "/test", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("NO_ACTIVITY"));
    }

    @Test
    public void test_Evaluate_NoActivity() {
        when(buildRepository.findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(null);
        response = deployEvaluator.evaluate(getCollectorItem("testGenericItem", "/test", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("NO_ACTIVITY"));
        verify(buildRepository, times(1)).findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class));

    }

    @Test
    public void test_Evaluate_Deploy_Scripts_Found_Tested() {
        when(buildRepository.findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(getBuild(BuildStatus.Success, "success"));
        when(apiSettings.getBuildStageRegEx()).thenReturn("(?i:.*any)");
        response = deployEvaluator.evaluate(getCollectorItem("testGenericItem", "/test", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("DEPLOY_SCRIPTS_FOUND_TESTED"));
        verify(buildRepository, times(1)).findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class));

    }

    @Test
    public void test_Evaluate_Deploy_Scripts_Found_Non_Tested() {
        when(buildRepository.findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(getBuild(BuildStatus.Failure, "failed"));
        when(apiSettings.getBuildStageRegEx()).thenReturn("(?i:.*any)");
        response = deployEvaluator.evaluate(getCollectorItem("testGenericItem", "/test", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("DEPLOY_SCRIPTS_FOUND_NOT_TESTED"));
        verify(buildRepository, times(1)).findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class));

    }

    @Test
    public void test_Evaluate_Deploy_Scripts_Tests_Not_Found() {
        when(buildRepository.findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(getBuild(BuildStatus.Failure, "failed"));
        when(apiSettings.getBuildStageRegEx()).thenReturn("(?i:.*error)");
        response = deployEvaluator.evaluate(getCollectorItem("testGenericItem", "/test", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("DEPLOYMENT_SCRIPTS_TEST_NOT_FOUND"));
        verify(buildRepository, times(1)).findTop1ByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class));

    }

    private CollectorItem getCollectorItem(String jobName, String jobUrl, boolean isError) {
        CollectorItem ci = new CollectorItem();
        ci.getOptions().put("jobName", jobName);
        ci.getOptions().put("jobUrl", jobUrl);
        ci.getOptions().put("instanceUrl", "http://jenkins.com/");
        if (isError) {
            ci.getErrors().add(new CollectionError("404", "Service Unavailable"));
        }
        return ci;
    }

    private Build getBuild(BuildStatus status, String stageStatus) {
        Build build = new Build();
        build.setBuildStatus(status);
        build.setStages(getBuildStages(stageStatus));
        return build;
    }

    private List<BuildStage> getBuildStages(String status) {
        return Arrays.asList("DEV ANY", "TEST", "PROD").stream().map(env -> getBuildStage(env, status)).collect(Collectors.toList());
    }

    private BuildStage getBuildStage(String name, String status) {
        BuildStage stage = new BuildStage();
        stage.setName(name);
        stage.setStatus(status);
        return stage;
    }

}