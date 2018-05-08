package com.capitalone.dashboard.service;

import com.capitalone.dashboard.common.TestUtils;
import com.capitalone.dashboard.config.CustomObjectMapper;
import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.response.AuditReviewResponse;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.SecurityReviewAuditResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.fakemongo.junit.FongoRule;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FongoConfig.class})
@DirtiesContext

public class DashboardAuditServiceTest {
    private static final Log LOGGER = LogFactory.getLog(DashboardAuditServiceTest.class);

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

    @Before
    public void loadStuff() throws IOException {
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadComponent(componentRepository);
        TestUtils.loadCollectorItems(collectorItemRepository);
        TestUtils.loadCommits(commitRepository);
        TestUtils.loadCollector(collectorRepository);
        TestUtils.loadPullRequests(gitRequestRepository);
        TestUtils.loadSSCRequests(codeQualityRepository);
    }

    @Test
    public void runTests() throws AuditException, IOException {
        for (CollectorItem item : collectorItemRepository.findAll()) {
            Collector collector = collectorRepository.findOne(item.getCollectorId());
            if ((collector != null) && (collector.getCollectorType() == CollectorType.SCM)) {
                String url = (String) item.getOptions().get("url");
                String branch = (String) item.getOptions().get("branch");
                LOGGER.info("Running Test: " + item.getOptions().get("url") + "/" + branch);

                List<CodeReviewAuditResponse> actual = (List<CodeReviewAuditResponse>) codeReviewAuditService.getPeerReviewResponses(url, branch, "GitHub", 0L, System.currentTimeMillis());
                List<CodeReviewAuditResponse> expected = (List<CodeReviewAuditResponse>) getExpected(url);
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
            } else if ((collector != null) && (collector.getCollectorType() == CollectorType.StaticSecurityScan)) {
                String title = "TestSSA";
                String businessService = "ASVCARDDIGITALSERVICING";
                String businessApplication = "CI375032";
                String url = (String) item.getOptions().get("reportUrl");

                DashboardReviewResponse actual = dashboardAuditService.getDashboardReviewResponse(title, DashboardType.Team, businessService, businessApplication, 1519728000000L, 1523180525854L, Sets.newHashSet(AuditType.STATIC_SECURITY_ANALYSIS));
                SecurityReviewAuditResponse expected = getExpectedSecurityReview(url);

                Map<AuditType, Collection<AuditReviewResponse>> auditReviewResponse = actual.getReview();
                Collection<AuditReviewResponse> auditResponse = auditReviewResponse.get(AuditType.STATIC_SECURITY_ANALYSIS);
                Set<CodeQualityMetric> lhs = Sets.newHashSet(((SecurityReviewAuditResponse) ((ArrayList) auditResponse).get(0)).getCodeQuality().getMetrics());
                Set<CodeQualityMetric> rhs = Sets.newHashSet(expected.getCodeQuality().getMetrics());
                compareMetrics(lhs, rhs);
            }
        }
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

    private void compareMetrics(Set<CodeQualityMetric> lhs, Set<CodeQualityMetric> rhs) {
        boolean bothNull = CollectionUtils.isEmpty(lhs) && CollectionUtils.isEmpty(rhs);
        if (!bothNull) {
            assertThat(lhs.size()).isEqualByComparingTo(rhs.size());
            Integer lhsScore = null;
            Integer lhsHigh = null;
            Integer lhsCritical = null;
            Integer rhsScore = null;
            Integer rhsHigh = null;
            Integer rhsCritical = null;

            for (CodeQualityMetric metric : lhs) {
                if (metric.getName().equalsIgnoreCase("Score"))
                    lhsScore = Integer.parseInt(metric.getValue().toString());
                else if (metric.getName().equalsIgnoreCase("High"))
                    lhsHigh = Integer.parseInt(metric.getValue().toString());
                else if (metric.getName().equalsIgnoreCase("Critical"))
                    lhsCritical = Integer.parseInt(metric.getValue().toString());
            }
            for (CodeQualityMetric metric : rhs) {
                if (metric.getName().equalsIgnoreCase("Score"))
                    rhsScore = Integer.parseInt(metric.getValue().toString());
                else if (metric.getName().equalsIgnoreCase("High"))
                    rhsHigh = Integer.parseInt(metric.getValue().toString());
                else if (metric.getName().equalsIgnoreCase("Critical"))
                    rhsCritical = Integer.parseInt(metric.getValue().toString());
            }
            if (lhsScore != null && rhsScore != null)
                assertThat(lhsScore).isEqualTo(rhsScore);
            if (lhsHigh != null && rhsHigh != null)
                assertThat(lhsHigh).isEqualTo(rhsHigh);
            if (lhsCritical != null && rhsCritical != null)
                assertThat(lhsCritical).isEqualTo(rhsCritical);
        }
    }

    private Collection<CodeReviewAuditResponse> getExpected(String url) throws IOException {
        String filename = "./expected/" + url.substring(url.lastIndexOf("/") + 1) + ".json";
        CustomObjectMapper objectMapper = new CustomObjectMapper();
        URL fileUrl = Resources.getResource(filename);
        LOGGER.info("Expected results json: " + fileUrl);
        String json = IOUtils.toString(fileUrl);
        return objectMapper.readValue(json, new TypeReference<List<CodeReviewAuditResponse>>() {
        });
    }

    private SecurityReviewAuditResponse getExpectedSecurityReview(String url) throws IOException {
        String filename = "./expected/" + url.substring(url.lastIndexOf("/") + 1) + ".json";
        CustomObjectMapper objectMapper = new CustomObjectMapper();
        URL fileUrl = Resources.getResource(filename);
        LOGGER.info("Expected results json: " + fileUrl);
        String json = IOUtils.toString(fileUrl);
        return objectMapper.readValue(json, new TypeReference<SecurityReviewAuditResponse>() {
        });
    }
}
