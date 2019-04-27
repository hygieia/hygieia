package com.capitalone.dashboard.common;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.LibraryPolicyResultsRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.testutil.GsonUtil;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

public class TestUtils {

    public static void loadDashBoard(DashboardRepository dashboardRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./dashboard/dashboard.json"));
        Dashboard dashboard = gson.fromJson(json, Dashboard.class);
        dashboardRepository.save(dashboard);
    }

    public static void loadCollector (CollectorRepository collectorRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./collectors/coll.json"));
        // List<Collector> collector = gson.fromJson(json, Collector.class);
        List<Collector> collector = gson.fromJson(json, new TypeToken<List<Collector>>(){}.getType());
        collectorRepository.save(collector);
    }

    public static void loadComponent(ComponentRepository componentRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./component/component.json"));
        Component component = gson.fromJson(json, Component.class);
        componentRepository.save(component);
    }

    public static void loadCollectorItems(CollectorItemRepository collectorItemRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./collector_items/items.json"));
        List<CollectorItem> collectorItem = gson.fromJson(json, new TypeToken<List<CollectorItem>>(){}.getType());
        collectorItemRepository.save(collectorItem);
    }

    public static void loadCommits(CommitRepository commitRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./commits/commits.json"));
        List<Commit> commits = gson.fromJson(json, new TypeToken<List<Commit>>(){}.getType());
        commitRepository.save(commits);
    }

    public static void loadPullRequests(GitRequestRepository gitRequestRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./gitrequests/prs.json"));
        List<GitRequest> prs = gson.fromJson(json, new TypeToken<List<GitRequest>>(){}.getType());
        gitRequestRepository.save(prs);
    }

    public static void loadSSCRequests(CodeQualityRepository codeQualityRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./securityscan/securityscan.json"));
        List<CodeQuality> ssa = gson.fromJson(json, new TypeToken<List<CodeQuality>>(){}.getType());
        codeQualityRepository.save(ssa);
    }

    public static void loadLibraryPolicy(LibraryPolicyResultsRepository libraryPolicyResultsRepository, String fileName) throws IOException {
        Gson gson = GsonUtil.getGson();
//        String json = IOUtils.toString(Resources.getResource("./librarypolicy/librarypolicy.json"));
        String json = IOUtils.toString(Resources.getResource(fileName));
        List<LibraryPolicyResult> ssa = gson.fromJson(json, new TypeToken<List<LibraryPolicyResult>>() {}.getType());
        libraryPolicyResultsRepository.save(ssa);
    }
    public static void loadTestResults(TestResultRepository testResultRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./test_results/test_results.json"));
        List<TestResult> testResults = gson.fromJson(json, new TypeToken<List<TestResult>>(){}.getType());
        testResultRepository.save(testResults);
    }

    public static void loadCodeQuality(CodeQualityRepository codeQualityRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./codequality/codequality.json"));
        List<CodeQuality> codeQuality = gson.fromJson(json, new TypeToken<List<CodeQuality>>(){}.getType());
        codeQualityRepository.save(codeQuality);
    }


    public static void loadFeature(FeatureRepository featureRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./feature/feature.json"));
        List<Feature> feature = gson.fromJson(json, new TypeToken<List<Feature>>(){}.getType());
        featureRepository.save(feature);
    }

    public static void loadArtifacts(BinaryArtifactRepository binaryArtifactRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./binaryartifact/binaryartifact.json"));
        List<BinaryArtifact> binaryArtifact = gson.fromJson(json, new TypeToken<List<BinaryArtifact>>(){}.getType());
        binaryArtifactRepository.save(binaryArtifact.get(0));
    }


}
