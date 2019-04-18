package com.capitalone.dashboard.evaluator;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CollectionError;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.response.ArtifactAuditResponse;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ArtifactEvaluatorTest {
    @InjectMocks
    private ArtifactEvaluator artifactEvaluator;
    @Mock
    private BinaryArtifactRepository binaryArtifactRepository;
    @Mock
    private ApiSettings apiSettings;

    private ArtifactAuditResponse response;

    @Test
    public void testEvaluate_ArtifactNotConfigured() {
        CollectorItem c = null;
        response = artifactEvaluator.evaluate(c, 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("ARTIFACT_NOT_CONFIGURED"));
    }

    @Test
    public void testEvaluate_CollectorItemError_Artifact_NULL() {
        response = artifactEvaluator.evaluate(getCollectorItem(null, "/test", "repo", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("COLLECTOR_ITEM_ERROR"));
    }

    @Test
    public void testEvaluate_CollectorItemError_Path_NULL() {
        response = artifactEvaluator.evaluate(getCollectorItem("artifact", null, "repo", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("COLLECTOR_ITEM_ERROR"));
    }

    @Test
    public void testEvaluate_CollectorItemError_RepoName_NULL() {
        response = artifactEvaluator.evaluate(getCollectorItem("artifact", "/test", null, false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("COLLECTOR_ITEM_ERROR"));
    }

    @Test
    public void testEvaluate_Unavailable() {
        response = artifactEvaluator.evaluate(getCollectorItem("artifact", "/test", "repo", true), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("UNAVAILABLE"));

    }

    @Test
    public void test_Evaluate_NoActivity() {
        when(binaryArtifactRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(null);
        response = artifactEvaluator.evaluate(getCollectorItem("artifact", "/test", "repo", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("NO_ACTIVITY"));
        Mockito.verify(binaryArtifactRepository, times(1)).findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class));

    }

    @Test
    public void test_Evaluate_ART_SYS_ACCT_BUILD_AUTO() {
        when(binaryArtifactRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(Stream.of(getBinaryArtifact(true)).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountRegEx()).thenReturn("/./g");
        response = artifactEvaluator.evaluate(getCollectorItem("artifact", "/test", "repo", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("ART_SYS_ACCT_BUILD_AUTO"));
        Mockito.verify(binaryArtifactRepository, times(1)).findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class));

    }

    @Test
    public void test_Evaluate_ART_SYS_ACCT_BUILD_USER() {
        when(binaryArtifactRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(Stream.of(getBinaryArtifact(false)).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountRegEx()).thenReturn("/./g");
        response = artifactEvaluator.evaluate(getCollectorItem("artifact", "/test", "repo", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("ART_SYS_ACCT_BUILD_USER"));
        Mockito.verify(binaryArtifactRepository, times(1)).findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class));

    }

    @Test
    public void test_Evaluate_ART_DOCK_IMG_FOUND() {
        when(binaryArtifactRepository.findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class))).thenReturn(Stream.of(getBinaryArtifact(false)).collect(Collectors.toList()));
        when(apiSettings.getServiceAccountRegEx()).thenReturn("/./g");
        response = artifactEvaluator.evaluate(getCollectorItem("artifact", "/test", "repo", false), 125634536, 6235263, null);
        Assert.assertEquals(true, response.getAuditStatuses().toString().contains("ART_DOCK_IMG_FOUND"));
        Mockito.verify(binaryArtifactRepository, times(1)).findByCollectorItemIdAndTimestampIsBetweenOrderByTimestampDesc(any(ObjectId.class), any(Long.class), any(Long.class));

    }


    private CollectorItem getCollectorItem(String artifactName, String path, String repoName, boolean isError) {
        CollectorItem ci = new CollectorItem();
        ci.getOptions().put("artifactName", artifactName);
        ci.getOptions().put("path", path);
        ci.getOptions().put("repoName", repoName);
        if (isError) {
            ci.getErrors().add(new CollectionError("404", "Service Unavailable"));
        }
        return ci;
    }

    private BinaryArtifact getBinaryArtifact(boolean isBuild) {
        BinaryArtifact ba = new BinaryArtifact();
        ba.setTimestamp(123456789);
        ba.setCanonicalName("canonicalName");
        ba.setArtifactGroupId("groupId");
        ba.setArtifactVersion("1.0.0");
        ba.setArtifactName("artifactName");
        ba.setType("file");
        ba.setCreatedTimeStamp(1234567);
        ba.setCreatedBy("testuser");
        ba.setVirtualRepos(Arrays.asList("docker", "public"));
        if (isBuild) {
            Build buildInfo = new Build();
            buildInfo.setBuildStatus(BuildStatus.Success);
            ba.setBuildInfo(buildInfo);
        }
        return ba;
    }
}