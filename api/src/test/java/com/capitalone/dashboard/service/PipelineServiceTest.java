package com.capitalone.dashboard.service;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import com.capitalone.dashboard.util.PipelineUtils;

import org.apache.commons.lang.NotImplementedException;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
	@Mock
	private ApiSettings apiSettings;
    @InjectMocks
    private PipelineServiceImpl pipelineService;

	@Before
	public void setUp() {
		Mockito.when(apiSettings.getSystemStages()).thenReturn(Arrays.asList(PipelineStage.COMMIT, PipelineStage.BUILD, 
				PipelineStage.valueOf("Dev ENV"), PipelineStage.valueOf("QA Env"), PipelineStage.valueOf("Int Env"), PipelineStage.valueOf("Perf Env"), PipelineStage.valueOf("Prod")));
	}
    
    @Test
    public void search() throws Exception {
        ObjectId dashboardCollectorItemId = ObjectId.get();

        //build request
        PipelineSearchRequest request = new PipelineSearchRequest();
        List<ObjectId> dashboardCollectorItemIds = new ArrayList<>();
        dashboardCollectorItemIds.add(dashboardCollectorItemId);
        request.setCollectorItemId(dashboardCollectorItemIds);

        Dashboard dashboard = makeTeamDashboard("template", "title", "appName", "comp1", "comp2");
        dashboard.getWidgets().add(makePipelineWidget("Dev ENV", "QA Env", null, null, "Prod"));
        ObjectId dashboardId = ObjectId.get();
        dashboard.setId(dashboardId);

        CollectorItem dashboardCollectorItem = makeDashboardCollectorItem(dashboard);
        dashboardCollectorItem.setId(dashboardCollectorItemId);

        Pipeline pipeline = makePipeline(dashboardCollectorItem);
        pipeline.addCommit(PipelineStage.COMMIT.getName(), makePipelineCommit("sha0", 1454953452000L));
        pipeline.addCommit(PipelineStage.BUILD.getName(), makePipelineCommit("sha0", 1454953452001L));
        pipeline.addCommit("Dev ENV", makePipelineCommit("sha0", 1454953452002L));
        pipeline.addCommit("QA Env", makePipelineCommit("sha0", 1454953452003L));
        pipeline.addCommit("Prod", makePipelineCommit("sha0", 1454953452004L));

        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(pipeline);

        when(pipelineRepository.findByCollectorItemIdIn(request.getCollectorItemId())).thenReturn(pipelines);
        when(collectorItemRepository.findOne(pipeline.getCollectorItemId())).thenReturn(dashboardCollectorItem);
        when(dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")))).thenReturn(dashboard);

        PipelineResponse expected = makePipelineResponse(pipeline, dashboard);

        List<PipelineResponse> pipelineResponses = (List<PipelineResponse>)pipelineService.search(request);
        PipelineResponse actual = pipelineResponses.get(0);

        assertEquals(actual.getCollectorItemId(), expected.getCollectorItemId());
        assertEquals(actual.getStageCommits(PipelineStage.valueOf("prod")), actual.getStageCommits(PipelineStage.valueOf("prod")));
        assertThat(actual.getStageCommits(PipelineStage.COMMIT).size(), is(0));
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
        for(PipelineStage stage : apiSettings.getSystemStages()) {
        	pipelineResponse.setStageCommits(stage, new ArrayList<PipelineResponseCommit>());
            if(PipelineStageType.DEPLOY.equals(stage.getType()) && stage.getName().matches("(?i)Prod(uction)?")) {
                String mappedName = PipelineUtils.getStageToEnvironmentNameMap(dashboard).get(stage);
                List<PipelineCommit> prodCommits = new ArrayList<>(pipeline.getEnvironmentStageMap().get(mappedName).getCommits());
                for (PipelineCommit commit : prodCommits) {
                    pipelineResponse.addToStage(PipelineStage.valueOf("prod"), new PipelineResponseCommit(commit));
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
