package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.EnvironmentStage;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.capitalone.dashboard.util.TestUtils.createBuild;
import static com.capitalone.dashboard.util.TestUtils.createCommit;
import static com.capitalone.dashboard.util.TestUtils.createPipelineCommit;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BuildEventListenerTest {

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private CollectorRepository collectorRepository;

    @Mock
    private CollectorItemRepository collectorItemRepository;

    @Mock
    private PipelineRepository pipelineRepository;

    @InjectMocks
    private BuildEventListener eventListener;

    @Mock
    private CommitRepository commitRepository;

    private static final boolean HAS_BUILD_COLLECTOR = true;

    @Test
    public void buildSaved_addedToPipeline() {
        Build build = createBuild();
        Dashboard dashboard = createDashboard(HAS_BUILD_COLLECTOR);
        Pipeline pipeline = new Pipeline();
        setupFindDashboards(build, dashboard);
        setupGetOrCreatePipeline(dashboard, pipeline);
        eventListener.onAfterSave(new AfterSaveEvent<>(build, null, ""));
        Map<String,EnvironmentStage> pipelineMap = pipeline.getEnvironmentStageMap();
        Assert.assertEquals(pipelineMap.get("Build").getCommits().size(), 2);
        verify(pipelineRepository).save(pipeline);
    }

    @Test
    public void buildSaved_addedToPipeline_commitStage() {
        Build build = createBuild();
        Dashboard dashboard = createDashboard(HAS_BUILD_COLLECTOR);
        Pipeline pipeline = new Pipeline();
        pipeline.addCommit(PipelineStage.COMMIT.getName(), createPipelineCommit("scmRev3"));
        setupFindDashboards(build, dashboard);
        setupGetOrCreatePipeline(dashboard, pipeline);
        List<Commit> commits = new ArrayList<>();
       commits.add(createCommit("scmRev3","http://github.com/scmurl"));
        when(commitRepository.findByScmRevisionNumber("scmRev3")).thenReturn(commits);
        eventListener.onAfterSave(new AfterSaveEvent<>(build, null, ""));
        Map<String,EnvironmentStage> pipelineMap = pipeline.getEnvironmentStageMap();
        Assert.assertEquals(pipelineMap.get("Build").getCommits().size(), 3);
        verify(pipelineRepository).save(pipeline);
    }

    private Dashboard createDashboard(boolean hasBuildCollector) {
        Component component = new Component();
        component.setId(ObjectId.get());
        component.addCollectorItem(CollectorType.Product, collectorItem());
        if (hasBuildCollector) {
            component.addCollectorItem(CollectorType.Build, collectorItem());
        }
        ObjectId configItemAppId = new ObjectId();
        ObjectId configItemComponentId = new ObjectId();

        Application application = new Application("app", component); List<String> activeWidgets = new ArrayList<>();
        Dashboard dashboard = new Dashboard("template", "title", application, new Owner("owner", AuthType.STANDARD),  DashboardType.Team , configItemAppId, configItemComponentId,activeWidgets);
        dashboard.setId(ObjectId.get());
        return dashboard;
    }

    private void setupFindDashboards(Build build, Dashboard dashboard) {
        CollectorItem commitCollectorItem = new CollectorItem();
        List<Component> components = Collections.singletonList(dashboard.getApplication().getComponents().get(0));
        commitCollectorItem.setId(build.getCollectorItemId());
        when(collectorItemRepository.findOne(build.getCollectorItemId())).thenReturn(commitCollectorItem);
        when(componentRepository.findByBuildCollectorItemId(commitCollectorItem.getId())).thenReturn(components);
        when(dashboardRepository.findByApplicationComponentsIn(components)).thenReturn(Collections.singletonList(dashboard));
    }

    private void setupGetOrCreatePipeline(Dashboard dashboard, Pipeline pipeline) {
        Collector productCollector = new Collector();
        productCollector.setId(ObjectId.get());
        CollectorItem teamDashboardCI = collectorItem();

        when(collectorRepository.findByCollectorType(CollectorType.Product))
                .thenReturn(Collections.singletonList(productCollector));
        when(collectorItemRepository.findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(productCollector.getId(), dashboard.getId().toString()))
                .thenReturn(teamDashboardCI);
        when(pipelineRepository.findByCollectorItemId(teamDashboardCI.getId())).thenReturn(pipeline);
    }

    private CollectorItem collectorItem() {
        CollectorItem item = new CollectorItem();
        item.setId(ObjectId.get());
        return item;
    }

}