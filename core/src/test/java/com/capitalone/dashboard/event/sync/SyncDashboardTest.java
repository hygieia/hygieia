package com.capitalone.dashboard.event.sync;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.relation.RelatedCollectorItem;
import com.capitalone.dashboard.repository.RelatedCollectorItemRepository;
import com.capitalone.dashboard.util.LoadTestData;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.LibraryPolicyResultsRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.testutil.FongoConfig;
import com.github.fakemongo.junit.FongoRule;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FongoConfig.class})
@DirtiesContext

public class SyncDashboardTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();

    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private CollectorRepository collectorRepository;
    @Autowired
    private CollectorItemRepository collectorItemRepository;

    @Autowired
    private CodeQualityRepository codeQualityRepository;

    @Autowired
    private BuildRepository buildRepository;

    @Autowired
    private LibraryPolicyResultsRepository libraryPolicyResultsRepository;

    @Autowired
    private TestResultRepository testResultsRepository;

    @Autowired
    private RelatedCollectorItemRepository relatedCollectorItemRepository;


    @Bean
    private SyncDashboard syncDashboard() {
        return new SyncDashboard(dashboardRepository, componentRepository, collectorRepository, collectorItemRepository, buildRepository, relatedCollectorItemRepository);
    }


    @Before
    public void loadStuff() throws IOException {
        LoadTestData.loadComponent(componentRepository);
        LoadTestData.loadDashBoard(dashboardRepository);
        LoadTestData.loadCollectorItems(collectorItemRepository);
        LoadTestData.loadCollector(collectorRepository);

        LoadTestData.loadBuilds(buildRepository);
        LoadTestData.loadSSCRequests(codeQualityRepository);
        LoadTestData.loadLibraryPolicy(libraryPolicyResultsRepository);
        LoadTestData.loadTestResults(testResultsRepository);
        LoadTestData.loadCodeQuality(codeQualityRepository);
    }


    @Test
    public void getWidget() {
        Widget w = syncDashboard().getWidget("repo", dashboardRepository.findAll().iterator().next());
        assertNotEquals(w.getOptions(), null);
        assertEquals(w.getOptions().get("url"), "https://mygithub.com/myOrg/myRepo");

        w =syncDashboard().getWidget("codeanalysis", dashboardRepository.findAll().iterator().next());
        assertNotEquals(w.getOptions(), null);

        w =syncDashboard().getWidget("dummy", dashboardRepository.findAll().iterator().next());
        assertEquals(w, null);
    }



    @Test
    public void getDashboardsByCollectorItems() {
        CollectorItem item = collectorItemRepository.findOne(new ObjectId("5ba136220be2d32568777fa5"));
        List<Dashboard> dashboardList = syncDashboard().getDashboardsByCollectorItems(Sets.newHashSet(item), CollectorType.Build);
        assertTrue(!CollectionUtils.isEmpty(dashboardList));
        assertTrue(dashboardList.size() == 3);
        List<String> titles = dashboardList.stream().map(Dashboard::getTitle).collect(Collectors.toList());
        assertTrue(titles.contains("TestPlugin"));
        assertTrue(titles.contains("Test212"));
        assertTrue(titles.contains("WidgetTest"));
    }

    @Test
    public void getDashboardsByCollectorItemsNullTest() {
        List<Dashboard> dashboardList = syncDashboard().getDashboardsByCollectorItems(null, CollectorType.Build);
        assertTrue(CollectionUtils.isEmpty(dashboardList));
    }


    @Test
    public void syncBuildAndRepo() {
        relatedCollectorItemRepository.deleteAll();
        Build build = buildRepository.findOne(new ObjectId("5ba520c40be2d3f98f795054"));

        syncDashboard().sync(build);
        List<RelatedCollectorItem> relatedCollectorItems = Lists.newArrayList(relatedCollectorItemRepository.findAll());
        assertTrue(relatedCollectorItems.size() == 1);
        assertTrue(relatedCollectorItems.get(0).getLeft().equals(build.getCollectorItemId()));
        assertTrue(relatedCollectorItems.get(0).getRight().equals(new ObjectId("5ba16a200be2d349ddf7fc80")));
    }

    @Test
    public void syncBuildAndRepoEmptyRepo() {
        relatedCollectorItemRepository.deleteAll();
        Build build = buildRepository.findOne(new ObjectId("5ba520c40be2d3f98f795055"));

        syncDashboard().sync(build);
        List<RelatedCollectorItem> relatedCollectorItems = Lists.newArrayList(relatedCollectorItemRepository.findAll());
        assertTrue(relatedCollectorItems.size() == 0);
    }


    @Test
    public void syncBuildAndRepoNoSCMCollector() {
        List<Collector> scmCollectors = collectorRepository.findAllByCollectorType(CollectorType.SCM);
        collectorRepository.delete(scmCollectors);
        Build build = buildRepository.findOne(new ObjectId("5ba520c40be2d3f98f795055"));

        syncDashboard().sync(build);
        List<RelatedCollectorItem> relatedCollectorItems = Lists.newArrayList(relatedCollectorItemRepository.findAll());
        assertTrue(relatedCollectorItems.size() == 0);
    }


    @Test
    public void syncBuildAndCodeQualityWithBuild() {
        relatedCollectorItemRepository.deleteAll();
        CodeQuality codeQuality = codeQualityRepository.findOne(new ObjectId("5ba98d055de4b1195307bf5a"));

        Build build = buildRepository.findOne(new ObjectId("5ba520c40be2d3f98f795054"));
        syncDashboard().sync(build);
        // now sync code quality
        syncDashboard().sync(codeQuality);

        List<RelatedCollectorItem> relatedCollectorItems = Lists.newArrayList(relatedCollectorItemRepository.findAll());
        assertTrue(relatedCollectorItems.size() == 2);

        List<ObjectId> lefts = relatedCollectorItems.stream().map(RelatedCollectorItem::getLeft).collect(Collectors.toList());
        List<ObjectId> rights = relatedCollectorItems.stream().map(RelatedCollectorItem::getRight).collect(Collectors.toList());

        assertTrue(lefts.get(0).equals(build.getCollectorItemId()));
        assertTrue(lefts.get(1).equals(build.getCollectorItemId()));
        assertTrue(rights.contains(new ObjectId("5ba16a200be2d349ddf7fc80")));
        assertTrue(rights.contains(new ObjectId("5ba136290be2d32568777fa9")));

    }

    @Test
    public void syncWithRelatedCollectorItems() {
        relatedCollectorItemRepository.deleteAll();
        CodeQuality codeQuality = codeQualityRepository.findOne(new ObjectId("5ba98d055de4b1195307bf5a"));
        Dashboard testSubject = dashboardRepository.findOne(new ObjectId("5baa458b0be2d337e3885815"));
        Widget widget = syncDashboard().getWidget("codeanalysis", testSubject);
        assertTrue(widget == null);

        Build build = buildRepository.findOne(new ObjectId("5ba520c40be2d3f98f795054"));
        // sync build
        syncDashboard().sync(build);
        // now sync code quality
        syncDashboard().sync(codeQuality);

        relatedCollectorItemRepository.findAll().forEach( r -> {
            try {
                syncDashboard().sync(r);
            } catch (SyncException e) {
            }
        });

        testSubject = dashboardRepository.findOne(new ObjectId("5baa458b0be2d337e3885815"));
        widget = syncDashboard().getWidget("codeanalysis", testSubject);
        assertTrue(widget != null);

        Component component = componentRepository.findOne(widget.getComponentId());
        assertTrue(component != null);
        CollectorItem si = component.getCollectorItems(CollectorType.CodeQuality).get(0);

        assertTrue(si != null);
    }
}