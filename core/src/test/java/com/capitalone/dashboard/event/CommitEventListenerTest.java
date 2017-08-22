package com.capitalone.dashboard.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;

@RunWith(MockitoJUnitRunner.class)
public class CommitEventListenerTest {

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
    private CommitEventListener eventListener;

    private static final boolean HAS_BUILD_COLLECTOR = true;
    private static final boolean NO_BUILD_COLLECTOR = false;

    @Test
    public void commitSaved_addedToPipeline() {
        // Arrange
        Commit commit = createCommit("myCommit");
        Dashboard dashboard = createDashboard(HAS_BUILD_COLLECTOR);
        Pipeline pipeline = new Pipeline();

        setupFindDashboards(commit, dashboard);
        setupGetOrCreatePipeline(dashboard, pipeline);

        // Act
        eventListener.onAfterSave(new AfterSaveEvent<>(commit, null, ""));

        // Assert
        boolean commitFound = pipeline.getEnvironmentStageMap()
                .get(PipelineStage.COMMIT.getName())
                .getCommits()
                .stream()
                .anyMatch(pc -> pc.getScmRevisionNumber().equals(commit.getScmRevisionNumber()));
        assertThat(commitFound, is(true));
        verify(pipelineRepository).save(pipeline);
    }

    @Test
    public void mergeCommitSaved_addedToPipeline() {
        // Arrange
        Commit commit = createMergeCommit("myCommit");
        Dashboard dashboard = createDashboard(HAS_BUILD_COLLECTOR);
        Pipeline pipeline = new Pipeline();

        setupFindDashboards(commit, dashboard);
        setupGetOrCreatePipeline(dashboard, pipeline);

        // Act
        eventListener.onAfterSave(new AfterSaveEvent<>(commit, null, ""));

        // Assert
        boolean commitFound = !pipeline.getEnvironmentStageMap().isEmpty() &&  pipeline.getEnvironmentStageMap()
                .get(PipelineStage.COMMIT.getName())
                .getCommits()
                .stream()
                .anyMatch(pc -> pc.getScmRevisionNumber().equals(commit.getScmRevisionNumber()));
        assertThat(commitFound, is(false));
        verify(pipelineRepository, never()).save(pipeline);
    }

    @Test
    public void releaseCommitSaved_addedToPipeline() {
        // Arrange
        Commit commit = createMavenCommit("myCommit");
        Dashboard dashboard = createDashboard(HAS_BUILD_COLLECTOR);
        Pipeline pipeline = new Pipeline();

        setupFindDashboards(commit, dashboard);
        setupGetOrCreatePipeline(dashboard, pipeline);

        // Act
        eventListener.onAfterSave(new AfterSaveEvent<>(commit, null, ""));

        // Assert
        boolean commitFound = !pipeline.getEnvironmentStageMap().isEmpty() &&  pipeline.getEnvironmentStageMap()
                .get(PipelineStage.COMMIT.getName())
                .getCommits()
                .stream()
                .anyMatch(pc -> pc.getScmRevisionNumber().equals(commit.getScmRevisionNumber()));
        assertThat(commitFound, is(false));
        verify(pipelineRepository, never()).save(pipeline);
    }
    @Test
    public void commitSaved_noBuildCollector_notAddedToPipeline() {
        // Arrange
        Commit commit = createCommit("myCommit");
        Dashboard dashboard = createDashboard(NO_BUILD_COLLECTOR);
        Pipeline pipeline = new Pipeline();

        setupFindDashboards(commit, dashboard);
        setupGetOrCreatePipeline(dashboard, pipeline);

        // Act
        eventListener.onAfterSave(new AfterSaveEvent<>(commit, null, ""));

        // Assert
        assertThat(pipeline.getEnvironmentStageMap().get(PipelineStage.COMMIT.getName()), nullValue());
        verify(pipelineRepository, never()).save(pipeline);
    }

    private Commit createCommit(String revisionNumber) {
        Commit commit = new Commit();
        commit.setScmRevisionNumber(revisionNumber);
        commit.setCollectorItemId(ObjectId.get());
        commit.setType(CommitType.New);
        return commit;
    }

    private Commit createMergeCommit(String revisionNumber) {
        Commit commit = new Commit();
        commit.setScmRevisionNumber(revisionNumber);
        commit.setCollectorItemId(ObjectId.get());
        commit.setType(CommitType.Merge);
        return commit;
    }

    private Commit createMavenCommit(String revisionNumber) {
        Commit commit = new Commit();
        commit.setScmRevisionNumber(revisionNumber);
        commit.setCollectorItemId(ObjectId.get());
        commit.setType(CommitType.NotBuilt);
        return commit;
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

    private void setupFindDashboards(Commit commit, Dashboard dashboard) {
        CollectorItem commitCollectorItem = new CollectorItem();
        List<Component> components = Collections.singletonList(dashboard.getApplication().getComponents().get(0));
        commitCollectorItem.setId(commit.getCollectorItemId());
        when(collectorItemRepository.findOne(commit.getCollectorItemId())).thenReturn(commitCollectorItem);
        when(componentRepository.findBySCMCollectorItemId(commitCollectorItem.getId())).thenReturn(components);
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