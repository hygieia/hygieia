package com.capitalone.dashboard.service;

import com.capitalone.dashboard.common.TestUtils;
import com.capitalone.dashboard.config.CustomObjectMapper;
import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.fakemongo.junit.FongoRule;
import com.google.common.io.Resources;
import com.google.gson.Gson;
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
import java.util.Collection;
import java.util.List;
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


    @Before
    public void loadStuff() throws IOException {
        TestUtils.loadDashBoard(dashboardRepository);
        TestUtils.loadComponent(componentRepository);
        TestUtils.loadCollectorItems(collectorItemRepository);
        TestUtils.loadCommits(commitRepository);
        TestUtils.loadCollector(collectorRepository);
        TestUtils.loadPullRequests(gitRequestRepository);

    }

    @Test
    public void runTests() throws AuditException, IOException {
        for (CollectorItem item : collectorItemRepository.findAll()) {
            Collector collector = collectorRepository.findOne(item.getCollectorId());
            if ((collector != null) && (collector.getCollectorType() == CollectorType.SCM)) {
                String url = (String) item.getOptions().get("url");
                String branch = (String) item.getOptions().get("branch");
                List<CodeReviewAuditResponse> actual = (List<CodeReviewAuditResponse>) codeReviewAuditService.getPeerReviewResponses(url, branch, "GitHub", 0L, System.currentTimeMillis());
                List<CodeReviewAuditResponse> expected = (List<CodeReviewAuditResponse>) getExpected(url);
                assertThat(actual.size()).isEqualByComparingTo(expected.size());
                //TODO: Manually add more assertions.
                IntStream.range(0, actual.size()).forEach(i -> {
                    CodeReviewAuditResponse lhs = actual.get(i);
                    CodeReviewAuditResponse rhs = expected.get(i);
                    List<Commit> lhsCommit = lhs.getCommits();
                    List<Commit> rhsCommit = rhs.getCommits();
                    GitRequest lhsPR = lhs.getPullRequest();
                    GitRequest rhsPR = rhs.getPullRequest();
                    assertThat(lhs).isEqualToComparingOnlyGivenFields(rhs, "scmUrl", "scmBranch", "auditStatuses");
                    boolean bothNull = (lhsPR == null) && (rhsPR == null);
                    if (bothNull) {
                        return;
                    }
                    assertThat(lhsPR).isEqualToComparingOnlyGivenFields(rhsPR, "number");
                });
            }
        }
    }

    private Collection<CodeReviewAuditResponse> getExpected(String url) throws IOException {
        String filename = "./expected/" + url.substring(url.lastIndexOf("/") + 1) + ".json";
        CustomObjectMapper objectMapper = new CustomObjectMapper();
        String json = IOUtils.toString(Resources.getResource(filename));
        return objectMapper.readValue(json, new TypeReference<List<CodeReviewAuditResponse>>() {
        });
    }

}
