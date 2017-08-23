package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStage;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
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

import static com.capitalone.dashboard.util.TestUtils.createCommit;
import static com.capitalone.dashboard.util.TestUtils.createPipelineCommit;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.capitalone.dashboard.util.TestUtils.createBuild;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentComponentEventListenerTest {

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private CollectorRepository collectorRepository;

    @Mock
    private CollectorItemRepository collectorItemRepository;

    @Mock
    private BinaryArtifactRepository binaryArtifactRepository;

    @Mock
    private PipelineRepository pipelineRepository;

    @InjectMocks
    private EnvironmentComponentEventListener eventListener;

    @Mock
    private CommitRepository commitRepository;

    private static final boolean HAS_BUILD_COLLECTOR = true;

    @Test
    public void commit_in_environment_stage_addedToPipeline() {
        EnvironmentComponent environmentComponent = createEnvironmentComponent();
        Dashboard dashboard = createDashboard(HAS_BUILD_COLLECTOR);
        setupFindDashboards(environmentComponent, dashboard);
        Pipeline pipeline = getPipeline();
        setupGetOrCreatePipeline(dashboard, pipeline);
        List<Commit> commits = new ArrayList<>();
        commits.add(createCommit("scmRev3","http://github.com/scmurl"));
        List<BinaryArtifact> binaryArtifactList = new ArrayList<>();
        binaryArtifactList.add(getBinaryArtifact());
        when(binaryArtifactRepository.findByArtifactNameAndArtifactExtensionAndTimestampGreaterThan("hygieia-2.0.5","jar",new Long(374268428))).thenReturn(binaryArtifactList);
        when(binaryArtifactRepository.findByArtifactNameAndArtifactExtensionAndTimestampGreaterThan("hygieia-2.0.5.jar",null,new Long(374268428))).thenReturn(binaryArtifactList);
        when(commitRepository.findByScmRevisionNumber("scmRev3")).thenReturn(commits);
        eventListener.onAfterSave(new AfterSaveEvent<>(environmentComponent, null, ""));
        Map<String,EnvironmentStage> pipelineMap = pipeline.getEnvironmentStageMap();
        Assert.assertEquals(pipelineMap.get("DEV").getCommits().size(), 3);
        verify(pipelineRepository,times(3)).save(pipeline);
    }

    private Pipeline getPipeline() {
        Pipeline pipeline = new Pipeline();
        pipeline.addCommit(PipelineStage.COMMIT.getName(), createPipelineCommit("scmRev3"));
        EnvironmentStage environmentStage = new EnvironmentStage();
        environmentStage.setLastArtifact(getBinaryArtifact());
        pipeline.getEnvironmentStageMap().put("DEV",environmentStage);
        return pipeline;
    }

    private EnvironmentComponent createEnvironmentComponent() {
        EnvironmentComponent environmentComponent = new EnvironmentComponent();
        environmentComponent.setDeployed(true);
        environmentComponent.setEnvironmentName("DEV");
        environmentComponent.setComponentName("hygieia-2.0.5.jar");
        return environmentComponent;
    }

    private BinaryArtifact getBinaryArtifact() {
        BinaryArtifact binaryArtifact = new BinaryArtifact();
        binaryArtifact.setTimestamp(374268428);
        binaryArtifact.setBuildInfo(createBuild());
        return binaryArtifact;
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

        Application application = new Application("app", component);
        List<String> activeWidgets = new ArrayList<>();
        Dashboard dashboard = new Dashboard("template", "title", application, new Owner("owner", AuthType.STANDARD),  DashboardType.Team , configItemAppId, configItemComponentId,activeWidgets);
        dashboard.setId(ObjectId.get());
        return dashboard;
    }

    private void setupFindDashboards(EnvironmentComponent environmentComponent, Dashboard dashboard) {
        CollectorItem commitCollectorItem = new CollectorItem();
        List<Component> components = Collections.singletonList(dashboard.getApplication().getComponents().get(0));
        commitCollectorItem.setId(environmentComponent.getCollectorItemId());
        when(collectorItemRepository.findOne(environmentComponent.getCollectorItemId())).thenReturn(commitCollectorItem);
        when(componentRepository.findByDeployCollectorItemId(commitCollectorItem.getId())).thenReturn(components);
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