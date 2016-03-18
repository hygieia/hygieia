package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import org.apache.commons.lang.NotImplementedException;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PipelineServiceTest {

    @Mock
    private PipelineRepository pipelineRepository;
    @Mock
    private DashboardRepository dashboardRepository;
    @Mock
    private CollectorItemRepository collectorItemRepository;
    @InjectMocks
    private PipelineServiceImpl pipelineService;

    @Test
    public void search() throws Exception {
        ObjectId dashboardCollectorItemId = ObjectId.get();

        //build request
        PipelineSearchRequest request = new PipelineSearchRequest();
        List<ObjectId> dashboardCollectorItemIds = new ArrayList<>();
        dashboardCollectorItemIds.add(dashboardCollectorItemId);
        request.setCollectorItemId(dashboardCollectorItemIds);

        Dashboard dashboard = makeTeamDashboard("template", "title", "appName", "comp1", "comp2");
        dashboard.getWidgets().add(makePipelineWidget("DEV", "QA", null, null, "PROD"));
        ObjectId dashboardId = ObjectId.get();
        dashboard.setId(dashboardId);

        CollectorItem dashboardCollectorItem = makeDashboardCollectorItem(dashboard);
        dashboardCollectorItem.setId(dashboardCollectorItemId);

        Pipeline pipeline = makePipeline(dashboardCollectorItem);
        pipeline.addCommit(PipelineStageType.Commit.name(), makePipelineCommit("sha0", 1454953452000L));
        pipeline.addCommit(PipelineStageType.Build.name(), makePipelineCommit("sha0", 1454953452001L));
        pipeline.addCommit("DEV", makePipelineCommit("sha0", 1454953452002L));
        pipeline.addCommit("QA", makePipelineCommit("sha0", 1454953452003L));
        pipeline.addCommit("PROD", makePipelineCommit("sha0", 1454953452004L));

        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(pipeline);

        when(pipelineRepository.findByCollectorItemIdIn(request.getCollectorItemId())).thenReturn(pipelines);
        when(collectorItemRepository.findOne(pipeline.getCollectorItemId())).thenReturn(dashboardCollectorItem);
        when(dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")))).thenReturn(dashboard);

        PipelineResponse expected = makePipelineResponse(pipeline, dashboard);

        List<PipelineResponse> pipelineResponses = (List<PipelineResponse>)pipelineService.search(request);
        PipelineResponse actual = pipelineResponses.get(0);

        assertEquals(actual.getCollectorItemId(), expected.getCollectorItemId());
        assertEquals(actual.getStages().get(PipelineStageType.Prod), actual.getStages().get(PipelineStageType.Prod));
        assertThat(actual.getStages().get(PipelineStageType.Commit).size(), is(0));
    }

    private Widget makePipelineWidget(String devName, String qaName, String intName, String perfName, String prodName){
        Widget pipelineWidget = new Widget();
        pipelineWidget.setName("pipeline");
        Map<String, String> environmentMap = new HashMap<>();

        if(devName != null){
            environmentMap.put(PipelineStageType.Dev.name(), devName);
        }
        if(qaName != null) {
            environmentMap.put(PipelineStageType.QA.name(), qaName);
        }
        if(intName != null) {
            environmentMap.put(PipelineStageType.Int.name(), intName);
        }
        if(perfName != null) {
            environmentMap.put(PipelineStageType.Perf.name(), perfName);
        }
        if(prodName != null) {
            environmentMap.put(PipelineStageType.Prod.name(), prodName);
        }

        pipelineWidget.getOptions().put("mappings", environmentMap);
        return pipelineWidget;
    }


    @Ignore
    @Test
    public void search_commit_moves_from_commit_to_dev() throws Exception {

    }

    @Ignore
    @Test
    public void search_45_day_production_timespan() throws Exception {

    }

    @Ignore
    @Test
    public void search_broken_build_moves_to_dev() throws Exception {

    }

    private Dashboard makeTeamDashboard(String template, String title, String appName, String owner, String... compNames) {
        Application app = new Application(appName);
        for (String compName : compNames) {
            app.addComponent(new Component(compName));
        }

        Dashboard dashboard = new Dashboard(template, title, app, owner, DashboardType.Team);
        return dashboard;
    }


    private CollectorItem makeDashboardCollectorItem(Dashboard dashboard){
        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setCollectorId(ObjectId.get());
        collectorItem.setDescription(dashboard.getTitle());
        collectorItem.getOptions().put("dashboardId", dashboard.getId().toString());
        return collectorItem;
    }

    private Pipeline makePipeline(CollectorItem collectorItem){
        Pipeline pipeline = new Pipeline();
        pipeline.setCollectorItemId(collectorItem.getId());
        return pipeline;
    }

    private PipelineCommit makePipelineCommit(String revisionNumber, long timestamp){
        PipelineCommit commit = new PipelineCommit();
        commit.setTimestamp(timestamp);
        commit.setScmRevisionNumber(revisionNumber);
        return commit;
    }

    //slow, explicit, and easy to read.
    private PipelineResponse makePipelineResponse(Pipeline pipeline, Dashboard dashboard){
        PipelineResponse pipelineResponse = new PipelineResponse();
        for(PipelineStageType stage : PipelineStageType.values())
        {
            pipelineResponse.getStages().put(stage, new ArrayList<PipelineResponseCommit>());
            if(stage.equals(PipelineStageType.Prod)) {
                String mappedName = dashboard.findEnvironmentMappings().get(stage);
                List<PipelineCommit> prodCommits = new ArrayList<>(pipeline.getStages().get(mappedName).getCommits());
                for (PipelineCommit commit : prodCommits) {
                    pipelineResponse.addToStage(PipelineStageType.Prod, new PipelineResponseCommit(commit));
                }
            }
        }

        pipelineResponse.setCollectorItemId(pipeline.getCollectorItemId());

        return pipelineResponse;
    }

    @SuppressWarnings("unused")
	private void applyTimestamps(Pipeline pipeline, PipelineResponseCommit commit){
        throw new NotImplementedException();
    }
}
