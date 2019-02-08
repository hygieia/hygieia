package com.capitalone.dashboard.service;

import com.capitalone.dashboard.common.TestUtils;
import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.mapper.ObjectIdSerializer;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.LibraryPolicyResultsRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.response.AuditReviewResponse;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.LibraryPolicyAuditResponse;
import com.capitalone.dashboard.response.SecurityReviewAuditResponse;
import com.capitalone.dashboard.response.PerformanceTestAuditResponse;
import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import com.capitalone.dashboard.response.TestResultsAuditResponse;
import com.capitalone.dashboard.testutil.GsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fakemongo.junit.FongoRule;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FongoConfig.class})
@DirtiesContext

public class DashboardAuditServiceTest {
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private CollectorRepository collectorRepository;
    @Autowired
    private CollectorItemRepository collectorItemRepository;

    @Autowired
    private GitRequestRepository gitRequestRepository;
    @Autowired
    private CommitRepository commitRepository;

    @Rule
    public FongoRule fongoRule = new FongoRule();

    @Autowired
    private CodeReviewAuditService codeReviewAuditService;

    @Autowired
    private DashboardAuditService dashboardAuditService;

    @Autowired
    private CodeQualityRepository codeQualityRepository;

    @Autowired
    private LibraryPolicyResultsRepository libraryPolicyResultsRepository;

    @Autowired
    private TestResultRepository testResultsRepository;

    @Autowired
    private FeatureRepository featureRepository;


    @Before
    public void loadStuff() throws IOException {
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadComponent(componentRepository);
        TestUtils.loadCollectorItems(collectorItemRepository);
        TestUtils.loadCommits(commitRepository);
        TestUtils.loadCollector(collectorRepository);
        TestUtils.loadPullRequests(gitRequestRepository);
        TestUtils.loadSSCRequests(codeQualityRepository);
        TestUtils.loadTestResults(testResultsRepository);
        TestUtils.loadCodeQuality(codeQualityRepository);
        TestUtils.loadFeature(featureRepository);
    }

    @Test
    public void runStaticSecurityAuditTests() throws AuditException, IOException {
        DashboardReviewResponse actual = getActualReviewResponse(dashboardAuditService.getDashboardReviewResponse(
                "TestSSA",
                DashboardType.Team,
                "TestBusServ",
                "confItem", 1519728000000L, 1523180525854L,
                Sets.newHashSet(AuditType.STATIC_SECURITY_ANALYSIS)), SecurityReviewAuditResponse.class);
        DashboardReviewResponse expected = getExpectedReviewResponse("StaticSecurityAnalysisAudit.json", SecurityReviewAuditResponse.class);
        assertDashboardAudit(actual, expected);
        assertThat(actual.getReview()).isNotEmpty();
        assertThat(actual.getReview().get(AuditType.STATIC_SECURITY_ANALYSIS)).isNotNull();
        Map<AuditType, Collection<LibraryPolicyAuditResponse>> actualReviewMap = actual.getReview();
        Collection<LibraryPolicyAuditResponse> actualReview = actualReviewMap.get(AuditType.STATIC_SECURITY_ANALYSIS);
        Map<AuditType, Collection<LibraryPolicyAuditResponse>> expectedReviewMap = expected.getReview();
        Collection<LibraryPolicyAuditResponse> expectedReview = expectedReviewMap.get(AuditType.STATIC_SECURITY_ANALYSIS);
        assertThat(actualReview.size()).isEqualTo(1);
        assertThat(actualReview.toArray()[0]).isEqualToComparingFieldByField(expectedReview.toArray()[0]);
    }

    @Test
    public void runLibraryPolicyAuditTests() throws AuditException, IOException {
        libraryPolicyResultsRepository.deleteAll();
        TestUtils.loadLibraryPolicy(libraryPolicyResultsRepository, "./librarypolicy/librarypolicy.json");
        DashboardReviewResponse actual = getActualReviewResponse(dashboardAuditService.getDashboardReviewResponse("TestSSA",
                DashboardType.Team,
                "TestBusServ",
                "confItem",
                1522623841000L, 1526505798000L,
                Sets.newHashSet(AuditType.LIBRARY_POLICY)), LibraryPolicyAuditResponse.class);
        DashboardReviewResponse expected = getExpectedReviewResponse("LibraryPolicyAudit.json", LibraryPolicyAuditResponse.class);

        assertDashboardAudit(actual, expected);
        assertThat(actual.getReview()).isNotEmpty();
        assertThat(actual.getReview().get(AuditType.LIBRARY_POLICY)).isNotNull();
        Map<AuditType, Collection<LibraryPolicyAuditResponse>> actualReviewMap = actual.getReview();
        Collection<LibraryPolicyAuditResponse> actualReview = actualReviewMap.get(AuditType.LIBRARY_POLICY);
        Map<AuditType, Collection<LibraryPolicyAuditResponse>> expectedReviewMap = expected.getReview();
        Collection<LibraryPolicyAuditResponse> expectedReview = expectedReviewMap.get(AuditType.LIBRARY_POLICY);
        assertThat(actualReview.size()).isEqualTo(1);
        assertThat(actualReview.toArray()[0]).isEqualToComparingFieldByField(expectedReview.toArray()[0]);
    }


    @Test
    public void runLibraryPolicyAuditTestsWithDispositionOk() throws AuditException, IOException {
        libraryPolicyResultsRepository.deleteAll();
        TestUtils.loadLibraryPolicy(libraryPolicyResultsRepository, "./librarypolicy/librarypolicy-disp-ok.json");
        DashboardReviewResponse actual = getActualReviewResponse(dashboardAuditService.getDashboardReviewResponse("TestSSA",
                DashboardType.Team,
                "TestBusServ",
                "confItem",
                1522623841000L, 1526505798000L,
                Sets.newHashSet(AuditType.LIBRARY_POLICY)), LibraryPolicyAuditResponse.class);
        DashboardReviewResponse expected = getExpectedReviewResponse("LibraryPolicyAuditWithDisposition-ok.json", LibraryPolicyAuditResponse.class);

        assertDashboardAudit(actual, expected);
        assertThat(actual.getReview()).isNotEmpty();
        assertThat(actual.getReview().get(AuditType.LIBRARY_POLICY)).isNotNull();
        Map<AuditType, Collection<LibraryPolicyAuditResponse>> actualReviewMap = actual.getReview();
        Collection<LibraryPolicyAuditResponse> actualReview = actualReviewMap.get(AuditType.LIBRARY_POLICY);
        Map<AuditType, Collection<LibraryPolicyAuditResponse>> expectedReviewMap = expected.getReview();
        Collection<LibraryPolicyAuditResponse> expectedReview = expectedReviewMap.get(AuditType.LIBRARY_POLICY);
        assertThat(actualReview.size()).isEqualTo(1);
        assertThat(actualReview.toArray()[0]).isEqualToComparingFieldByField(expectedReview.toArray()[0]);
    }

    @Test
    public void runLibraryPolicyAuditTestsWithDispositionFail() throws AuditException, IOException {
        libraryPolicyResultsRepository.deleteAll();
        TestUtils.loadLibraryPolicy(libraryPolicyResultsRepository, "./librarypolicy/librarypolicy-disp-fail.json");
        DashboardReviewResponse actual = getActualReviewResponse(dashboardAuditService.getDashboardReviewResponse("TestSSA",
                DashboardType.Team,
                "TestBusServ",
                "confItem",
                1522623841000L, 1526505798000L,
                Sets.newHashSet(AuditType.LIBRARY_POLICY)), LibraryPolicyAuditResponse.class);
        DashboardReviewResponse expected = getExpectedReviewResponse("LibraryPolicyAuditWithDisposition-fail.json", LibraryPolicyAuditResponse.class);

        assertDashboardAudit(actual, expected);
        assertThat(actual.getReview()).isNotEmpty();
        assertThat(actual.getReview().get(AuditType.LIBRARY_POLICY)).isNotNull();
        Map<AuditType, Collection<LibraryPolicyAuditResponse>> actualReviewMap = actual.getReview();
        Collection<LibraryPolicyAuditResponse> actualReview = actualReviewMap.get(AuditType.LIBRARY_POLICY);
        Map<AuditType, Collection<LibraryPolicyAuditResponse>> expectedReviewMap = expected.getReview();
        Collection<LibraryPolicyAuditResponse> expectedReview = expectedReviewMap.get(AuditType.LIBRARY_POLICY);
        assertThat(actualReview.size()).isEqualTo(1);
        assertThat(actualReview.toArray()[0]).isEqualToComparingFieldByField(expectedReview.toArray()[0]);
    }

    @Test
    public void runPerformanceAuditTests() throws AuditException, IOException {
        DashboardReviewResponse actual = getActualReviewResponse(dashboardAuditService.getDashboardReviewResponse("TestSSA",
                DashboardType.Team,
                "TestBusServ",
                "confItem",
                1522623841000L, 1526505798000L,
                Sets.newHashSet(AuditType.PERF_TEST)), PerformanceTestAuditResponse.class);
        DashboardReviewResponse expected = getExpectedReviewResponse("Performance.json", PerformanceTestAuditResponse.class);
        assertDashboardAudit(actual, expected);
        assertThat(actual.getReview()).isNotEmpty();
        assertThat(actual.getReview().get(AuditType.PERF_TEST)).isNotNull();
        Map<AuditType, Collection<PerformanceTestAuditResponse>> actualReviewMap = actual.getReview();
        Collection<PerformanceTestAuditResponse> actualReview = actualReviewMap.get(AuditType.PERF_TEST);
        Map<AuditType, Collection<PerformanceTestAuditResponse>> expectedReviewMap = expected.getReview();
        Collection<PerformanceTestAuditResponse> expectedReview = expectedReviewMap.get(AuditType.PERF_TEST);
        assertThat(actualReview.size()).isEqualTo(1);
        assertThat(actualReview.toArray()[0]).isEqualToComparingFieldByField(expectedReview.toArray()[0]);
    }

    @Test
    public void runCodeQualityAuditTests() throws AuditException, IOException {
        DashboardReviewResponse actual = getActualReviewResponse(dashboardAuditService.getDashboardReviewResponse("TestSSA",
                DashboardType.Team,
                "TestBusServ",
                "confItem",
                1473860406000L, 1478983206000L,
                Sets.newHashSet(AuditType.CODE_QUALITY)), CodeQualityAuditResponse.class);
        DashboardReviewResponse expected = getExpectedReviewResponse("CodeQuality.json", CodeQualityAuditResponse.class);

        assertDashboardAudit(actual, expected);
        assertThat(actual.getReview()).isNotEmpty();
        assertThat(actual.getReview().get(AuditType.CODE_QUALITY)).isNotNull();
        Map<AuditType, Collection<CodeQualityAuditResponse>> actualReviewMap = actual.getReview();
        Collection<CodeQualityAuditResponse> actualReview = actualReviewMap.get(AuditType.CODE_QUALITY);
        Map<AuditType, Collection<CodeQualityAuditResponse>> expectedReviewMap = expected.getReview();
        Collection<CodeQualityAuditResponse> expectedReview = expectedReviewMap.get(AuditType.CODE_QUALITY);
        assertThat(actualReview.size()).isEqualTo(1);
        assertThat(actualReview.toArray()[0]).isEqualToComparingFieldByField(expectedReview.toArray()[0]);
    }

    @Test
    public void runTestResultsAuditTests() throws AuditException, IOException {
        DashboardReviewResponse actual = getActualReviewResponse(dashboardAuditService.getDashboardReviewResponse("TestSSA",
                DashboardType.Team,
                "TestBusServ",
                "confItem",
                1473885606000L, 1478983206000L,
                Sets.newHashSet(AuditType.TEST_RESULT)), TestResultsAuditResponse.class);
        DashboardReviewResponse expected = getExpectedReviewResponse("TestResults.json", TestResultsAuditResponse.class);
        assertDashboardAudit(actual, expected);
        assertThat(actual.getReview()).isNotEmpty();
        assertThat(actual.getReview().get(AuditType.TEST_RESULT)).isNotNull();
        Map<AuditType, Collection<TestResultsAuditResponse>> actualReviewMap = actual.getReview();
        Collection<TestResultsAuditResponse> actualReview = actualReviewMap.get(AuditType.TEST_RESULT);
        Map<AuditType, Collection<TestResultsAuditResponse>> expectedReviewMap = expected.getReview();
        Collection<TestResultsAuditResponse> expectedReview = expectedReviewMap.get(AuditType.TEST_RESULT);
        assertThat(actualReview.size()).isEqualTo(1);
        //assertThat((actualReview.toArray()[0])).isEqualToComparingFieldByField(expectedReview.toArray()[0]);
    }




    @Test
    public void runLegacyCodeReviewTests() throws AuditException, IOException {
        for (CollectorItem item : collectorItemRepository.findAll()) {
            Collector collector = collectorRepository.findOne(item.getCollectorId());
            if ((collector != null) && (collector.getCollectorType() == CollectorType.SCM)) {
                String url = (String) item.getOptions().get("url");
                String branch = (String) item.getOptions().get("branch");
                //This is for a different test. Skip it for this.
                if ("https://mygithub.com/TechOriginations/openupf".equalsIgnoreCase(url)) {
                    continue;
                }
                List<CodeReviewAuditResponse> actual = (List<CodeReviewAuditResponse>) codeReviewAuditService.getPeerReviewResponses(url, branch, "GitHub", 0L, System.currentTimeMillis());
                List<CodeReviewAuditResponse> expected = (List<CodeReviewAuditResponse>) getExpectedCodeReviewResponse(url);
                assertThat(actual.size()).isEqualByComparingTo(expected.size());

                IntStream.range(0, actual.size()).forEach(i -> {
                    CodeReviewAuditResponse lhs = actual.get(i);
                    CodeReviewAuditResponse rhs = expected.get(i);
                    List<Commit> lhsCommits = lhs.getCommits();
                    List<Commit> rhsCommits = rhs.getCommits();
                    GitRequest lhsPR = lhs.getPullRequest();
                    GitRequest rhsPR = rhs.getPullRequest();
                    assertThat(lhs).isEqualToComparingOnlyGivenFields(rhs, "scmUrl", "scmBranch", "auditStatuses");
                    boolean bothNull = (lhsPR == null) && (rhsPR == null);
                    if (!bothNull) {
                        assertThat(lhsPR).isEqualToComparingOnlyGivenFields(rhsPR, "scmUrl", "scmBranch", "number", "orgName", "repoName", "scmMergeEventRevisionNumber",
                                "scmCommitLog", "scmCommitTimestamp", "scmAuthor", "numberOfChanges", "sourceRepo", "sourceBranch", "targetRepo", "targetBranch", "updatedAt", "createdAt",
                                "closedAt", "state", "mergedAt", "headSha", "baseSha");

                        List<Commit> lhsPRCommits = Objects.requireNonNull(lhsPR).getCommits();
                        List<Commit> rhsPRCommits = rhsPR.getCommits();
                        compareCommits(lhsPRCommits, rhsPRCommits);

                        List<Review> lhsPRReviews = lhsPR.getReviews();
                        List<Review> rhsPRReviews = rhsPR.getReviews();
                        compareReviews(lhsPRReviews, rhsPRReviews);

                        List<Comment> lhsPRComments = lhsPR.getComments();
                        List<Comment> rhsPRComments = rhsPR.getComments();
                        compareComments(lhsPRComments, rhsPRComments);
                    }
                    compareCommits(lhsCommits, rhsCommits);

                });
            }
        }
    }


    @Test
    public void runLegacyCodeReviewTestSpecialDateRange() throws AuditException, IOException {
                String url = "https://mygithub.com/Devopscode/NewPrOldCommit";
                String branch = "master";
                List<CodeReviewAuditResponse> actual = (List<CodeReviewAuditResponse>) codeReviewAuditService.getPeerReviewResponses(url, branch, "GitHub", 1535502925000L, 1535675725000L);
                List<CodeReviewAuditResponse> expected = (List<CodeReviewAuditResponse>) getExpectedCodeReviewResponse("https://mygithub.com/Devopscode/NewPrOldCommitSpecialDateRange");
                assertThat(actual.size()).isEqualByComparingTo(expected.size());
                IntStream.range(0, actual.size()).forEach(i -> {
                    CodeReviewAuditResponse lhs = actual.get(i);
                    CodeReviewAuditResponse rhs = expected.get(i);
                    List<Commit> lhsCommits = lhs.getCommits();
                    List<Commit> rhsCommits = rhs.getCommits();
                    GitRequest lhsPR = lhs.getPullRequest();
                    GitRequest rhsPR = rhs.getPullRequest();
                    assertThat(lhs).isEqualToComparingOnlyGivenFields(rhs, "scmUrl", "scmBranch", "auditStatuses");
                    boolean bothNull = (lhsPR == null) && (rhsPR == null);
                    if (!bothNull) {
                        assertThat(lhsPR).isEqualToComparingOnlyGivenFields(rhsPR, "scmUrl", "scmBranch", "number", "orgName", "repoName", "scmMergeEventRevisionNumber",
                                "scmCommitLog", "scmCommitTimestamp", "scmAuthor", "numberOfChanges", "sourceRepo", "sourceBranch", "targetRepo", "targetBranch", "updatedAt", "createdAt",
                                "closedAt", "state", "mergedAt", "headSha", "baseSha");

                        List<Commit> lhsPRCommits = Objects.requireNonNull(lhsPR).getCommits();
                        List<Commit> rhsPRCommits = rhsPR.getCommits();
                        compareCommits(lhsPRCommits, rhsPRCommits);

                        List<Review> lhsPRReviews = lhsPR.getReviews();
                        List<Review> rhsPRReviews = rhsPR.getReviews();
                        compareReviews(lhsPRReviews, rhsPRReviews);

                        List<Comment> lhsPRComments = lhsPR.getComments();
                        List<Comment> rhsPRComments = rhsPR.getComments();
                        compareComments(lhsPRComments, rhsPRComments);
                    }
                    compareCommits(lhsCommits, rhsCommits);

                });
    }

    private void compareComments(List<Comment> lhsPRComments, List<Comment> rhsPRComments) {
        assertThat(CollectionUtils.isEmpty(lhsPRComments) ? 0 : lhsPRComments.size()).isEqualTo(CollectionUtils.isEmpty(rhsPRComments) ? 0 : rhsPRComments.size());
        lhsPRComments.sort(Comparator.comparing(Comment::getCreatedAt));
        lhsPRComments.sort(Comparator.comparing(Comment::getCreatedAt));
        IntStream.range(0, lhsPRComments.size()).forEach(i -> assertThat(lhsPRComments.get(i)).isEqualToComparingFieldByField(rhsPRComments.get(i)));
    }

    private void compareReviews(List<Review> lhsPRReviews, List<Review> rhsPRReviews) {
        assertThat(CollectionUtils.isEmpty(lhsPRReviews) ? 0 : lhsPRReviews.size()).isEqualTo(CollectionUtils.isEmpty(rhsPRReviews) ? 0 : rhsPRReviews.size());
        lhsPRReviews.sort(Comparator.comparing(Review::getCreatedAt));
        rhsPRReviews.sort(Comparator.comparing(Review::getCreatedAt));

        IntStream.range(0, lhsPRReviews.size()).forEach(i -> assertThat(lhsPRReviews.get(i)).isEqualToComparingFieldByField(rhsPRReviews.get(i)));
    }

    private void compareCommits(List<Commit> lhsCommits, List<Commit> rhsCommits) {
        int lhsSize = CollectionUtils.isEmpty(lhsCommits) ? 0 : lhsCommits.size();
        int rhsSize = CollectionUtils.isEmpty(rhsCommits) ? 0 : rhsCommits.size();
        assertThat(lhsSize).isEqualTo(rhsSize);
        boolean bothNull = CollectionUtils.isEmpty(lhsCommits) && CollectionUtils.isEmpty(rhsCommits);
        if (!bothNull) {
            assertThat(lhsCommits.size()).isEqualByComparingTo(rhsCommits.size());
            lhsCommits.sort(Comparator.comparing(Commit::getScmRevisionNumber));
            rhsCommits.sort(Comparator.comparing(Commit::getScmRevisionNumber));
            IntStream.range(0, lhsCommits.size()).forEach(j -> assertThat(lhsCommits.get(j)).isEqualToIgnoringGivenFields(rhsCommits.get(j), "id", "timestamp"));
        }
    }

    public Collection<CodeReviewAuditResponse> getExpectedCodeReviewResponse(String url) throws IOException {
        String filename = "./expected/" + url.substring(url.lastIndexOf("/") + 1) + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module2 = new SimpleModule("ObjectIdModule");
        module2.addSerializer(ObjectId.class, new ObjectIdSerializer());
        objectMapper = objectMapper.registerModule(module2);
        URL fileUrl = Resources.getResource(filename);
        String json = IOUtils.toString(fileUrl);
        return objectMapper.readValue(json, new TypeReference<List<CodeReviewAuditResponse>>() {
        });
    }

    private String getExpectedJSON(String fileName) throws IOException {
        String path = "./expected/" + fileName;
        URL fileUrl = Resources.getResource(path);
        return IOUtils.toString(fileUrl);
    }

    private <T extends AuditReviewResponse> DashboardReviewResponse getExpectedReviewResponse (String fileName, Class<T> anyType) throws IOException {
        Gson gson = GsonUtil.getGson();
        return gson.fromJson(getExpectedJSON(fileName), new TypeToken<DashboardReviewResponse<T>>(){}.getType());
    }

    private <T extends AuditReviewResponse> DashboardReviewResponse getActualReviewResponse (DashboardReviewResponse response, Class<T> anyType) {
        Gson gson = GsonUtil.getGson();
        return gson.fromJson(gson.toJson(response), new TypeToken<DashboardReviewResponse<T>>(){}.getType());
    }

    private void assertDashboardAudit(DashboardReviewResponse lhs, DashboardReviewResponse rhs) {
        assertThat(lhs.getBusinessApplication()).isEqualTo(rhs.getBusinessApplication());
        assertThat(lhs.getBusinessService()).isEqualTo(rhs.getBusinessService());
        assertThat(lhs.getDashboardTitle()).isEqualTo(rhs.getDashboardTitle());
        assertThat(lhs.getErrorMessage()).isEqualTo(rhs.getErrorMessage());
        assertThat(lhs.getAuditStatuses().toArray()).isEqualTo(rhs.getAuditStatuses().toArray());
    }
}