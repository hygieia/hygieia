package com.capitalone.dashboard.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineResponse;
import com.capitalone.dashboard.model.PipelineResponseCommit;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;

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
        ObjectId configItemAppId = ObjectId.get();
        ObjectId configItemComponentId = ObjectId.get();
        //build request
        PipelineSearchRequest request = new PipelineSearchRequest();
        List<ObjectId> dashboardCollectorItemIds = new ArrayList<>();
        dashboardCollectorItemIds.add(dashboardCollectorItemId);
        request.setCollectorItemId(dashboardCollectorItemIds);

        Dashboard dashboard = makeTeamDashboard("template", "title", "appName", "",configItemAppId,configItemComponentId,"comp1", "comp2");
        dashboard.getWidgets().add(makePipelineWidget("Dev ENV", "QA Env", null, null, "Prod"));
        Widget buildWidget = new Widget();
        buildWidget.setName("build");
        dashboard.getWidgets().add(buildWidget);

        Widget commitWidget = new Widget();
        commitWidget.setName("repo");
        dashboard.getWidgets().add(commitWidget);
        ObjectId dashboardId = ObjectId.get();
        dashboard.setId(dashboardId);

        CollectorItem dashboardCollectorItem = makeDashboardCollectorItem(dashboard);
        dashboardCollectorItem.setId(dashboardCollectorItemId);

        Pipeline pipeline = makePipeline(dashboardCollectorItem);
        pipeline.addCommit(PipelineStage.COMMIT.getName(), makePipelineCommit("sha0", 1454953452000L));
        pipeline.addCommit(PipelineStage.BUILD.getName(), makePipelineCommit("sha0", 1454953452000L));
        pipeline.addCommit("dev", makePipelineCommit("sha0", 1454953452000L));
        pipeline.addCommit("qa", makePipelineCommit("sha0", 1454953452000L));
        pipeline.addCommit("prod", makePipelineCommit("sha0", 1454953452000L));

        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(pipeline);

        when(pipelineRepository.findByCollectorItemId(dashboardCollectorItemId)).thenReturn(pipeline);
        when(collectorItemRepository.findOne(pipeline.getCollectorItemId())).thenReturn(dashboardCollectorItem);
        when(dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")))).thenReturn(dashboard);

        PipelineResponse expected = makePipelineResponse(pipeline, dashboard);

        List<PipelineResponse> pipelineResponses = (List<PipelineResponse>)pipelineService.search(request);
        PipelineResponse actual = pipelineResponses.get(0);

        assertEquals(actual.getCollectorItemId(), expected.getCollectorItemId());
        assertThat(actual.getStageCommits(PipelineStage.COMMIT).size(), is(0));
        assertThat(actual.getStageCommits(PipelineStage.BUILD).size(), is(0));
        assertThat(actual.getStageCommits(PipelineStage.valueOf("dev")).size(),is(0));
        assertThat(actual.getStageCommits(PipelineStage.valueOf("qa")).size(),is(0));
        assertThat(actual.getStageCommits(PipelineStage.valueOf("prod")).size(),is(1));
    }

    private Widget makePipelineWidget(String devName, String qaName, String intName, String perfName, String prodName){
        Widget pipelineWidget = new Widget();
        pipelineWidget.setName("pipeline");
        Map<String, String> environmentMap = new HashMap<>();

        if(devName != null){
            environmentMap.put("dev", devName);
        }
        if(qaName != null) {
            environmentMap.put("qa", qaName);
        }
        if(intName != null) {
            environmentMap.put("int", intName);
        }
        if(perfName != null) {
            environmentMap.put("perf", perfName);
        }
        if(prodName != null) {
            environmentMap.put("prod", prodName);
            pipelineWidget.getOptions().put("prod",prodName);
        }

        Map<String,String> order = new HashMap<>();
        order.put("0","dev");
        order.put("1","qa");
        order.put("2","prod");
        pipelineWidget.getOptions().put("order", order);
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

    private Dashboard makeTeamDashboard(String template, String title, String appName, String owner, ObjectId configItemAppId,ObjectId configItemComponentId, String... compNames) {

        Application app = new Application(appName);
        for (String compName : compNames) {
            app.addComponent(new Component(compName));
        }
        List<String> activeWidgets = new ArrayList<>();
        Dashboard dashboard = new Dashboard(template, title, app, new Owner(owner, AuthType.STANDARD), DashboardType.Team, configItemAppId, configItemComponentId,activeWidgets);
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
        List<PipelineStage> pipelineStageList =   Arrays.asList(PipelineStage.COMMIT, PipelineStage.BUILD,
                PipelineStage.valueOf("Dev ENV"), PipelineStage.valueOf("QA Env"), PipelineStage.valueOf("Int Env"), PipelineStage.valueOf("Perf Env"), PipelineStage.valueOf("Prod"));
        for(PipelineStage stage : pipelineStageList) {
            pipelineResponse.setStageCommits(stage, new ArrayList<PipelineResponseCommit>());
        }
        pipelineResponse.setCollectorItemId(pipeline.getCollectorItemId());
        return pipelineResponse;
    }

    @SuppressWarnings("unused")
    private void applyTimestamps(Pipeline pipeline, PipelineResponseCommit commit){
        throw new NotImplementedException();
    }
}
