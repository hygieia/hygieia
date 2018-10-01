package com.capitalone.dashboard.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.capitalone.dashboard.model.ScoreDisplayType;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.model.score.settings.BuildScoreSettings;
import com.capitalone.dashboard.model.score.settings.PropagateType;
import com.capitalone.dashboard.model.score.settings.ScoreComponentSettings;
import com.capitalone.dashboard.model.score.settings.ScoreCriteriaSettings;
import com.capitalone.dashboard.model.score.settings.ScoreThresholdSettings;
import com.capitalone.dashboard.model.score.settings.ScoreType;
import com.capitalone.dashboard.model.score.settings.ScoreTypeValue;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.repository.ScoreCriteriaSettingsRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.apache.commons.lang.NotImplementedException;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScoreCriteriaSettingsServiceTest {

  @Mock
  private ScoreCriteriaSettingsRepository scoreCriteriaSettingsRepository;

  @InjectMocks
  private ScoreCriteriaSettingsServiceImpl scoreCriteriaSettingsService;

  @Test
  public void getScoreCriteriaSettingsByType() throws Exception {
    ObjectMapper mapper = getObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("score-criteria-settings.json")).read();
    ScoreCriteriaSettings scoreCriteriaSettings = mapper.readValue(content, ScoreCriteriaSettings.class);

    when(this.scoreCriteriaSettingsRepository.findByType(ScoreValueType.DASHBOARD)).thenReturn(scoreCriteriaSettings);

    ScoreCriteriaSettings scoreCriteriaSettingsResult =
      this.scoreCriteriaSettingsService.getScoreCriteriaSettingsByType(
        ScoreValueType.DASHBOARD
      );

    assertNotNull(scoreCriteriaSettingsResult);
    assertThat(scoreCriteriaSettingsResult.getMaxScore(), is(5));
    assertThat(scoreCriteriaSettingsResult.getType(), is(ScoreValueType.DASHBOARD));
    assertThat(scoreCriteriaSettingsResult.getComponentAlert().getValue(), is(0d));
    assertThat(scoreCriteriaSettingsResult.getComponentAlert().getComparator(), is(ScoreThresholdSettings.ComparatorType.less_or_equal));

    BuildScoreSettings buildScoreSettings = scoreCriteriaSettingsResult.getBuild();
    assertNotNull(buildScoreSettings);
    assertThat(buildScoreSettings.getWeight(), is(25));
    assertThat(buildScoreSettings.getNumberOfDays(), is(14));

    ScoreTypeValue noWidgetFound = buildScoreSettings.getCriteria().getNoWidgetFound();
    assertNotNull(noWidgetFound);

    assertThat(
      noWidgetFound.getScoreType(),
      is(ScoreType.zero_score)
    );
    assertThat(
      noWidgetFound.getPropagate(),
      is(PropagateType.no)
    );

    ScoreTypeValue noDataFound = buildScoreSettings.getCriteria().getNoDataFound();
    assertNotNull(noDataFound);

    assertThat(
      noDataFound.getScoreType(),
      is(ScoreType.zero_score)
    );
    assertThat(
      noDataFound.getPropagate(),
      is(PropagateType.no)
    );

    ScoreComponentSettings buildStatusSettings = buildScoreSettings.getStatus();
    assertNotNull(buildStatusSettings);

    assertThat(buildStatusSettings.getWeight(), is(50));

    BuildScoreSettings.BuildDurationScoreSettings buildDurationSettings = buildScoreSettings.getDuration();
    assertNotNull(buildDurationSettings);

    assertThat(buildDurationSettings.getWeight(), is(50));

  }

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class PipelineServiceTest {

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

            Dashboard dashboard = makeTeamDashboard("template", "title", "appName", "", "ASVTEST", "BAPTEST", "comp1", "comp2");
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
            when(dashboardRepository.findOne(new ObjectId((String) dashboardCollectorItem.getOptions().get("dashboardId")))).thenReturn(dashboard);

            PipelineResponse expected = makePipelineResponse(pipeline, dashboard);

            List<PipelineResponse> pipelineResponses = (List<PipelineResponse>) pipelineService.search(request);
            PipelineResponse actual = pipelineResponses.get(0);

            assertEquals(actual.getCollectorItemId(), expected.getCollectorItemId());
            assertThat(actual.getStageCommits(PipelineStage.COMMIT).size(), is(0));
            assertThat(actual.getStageCommits(PipelineStage.BUILD).size(), is(0));
            assertThat(actual.getStageCommits(PipelineStage.valueOf("dev")).size(), is(0));
            assertThat(actual.getStageCommits(PipelineStage.valueOf("qa")).size(), is(0));
            assertThat(actual.getStageCommits(PipelineStage.valueOf("prod")).size(), is(0));
        }

        private Widget makePipelineWidget(String devName, String qaName, String intName, String perfName, String prodName) {
            Widget pipelineWidget = new Widget();
            pipelineWidget.setName("pipeline");
            Map<String, String> environmentMap = new HashMap<>();

            if (devName != null) {
                environmentMap.put("dev", devName);
            }
            if (qaName != null) {
                environmentMap.put("qa", qaName);
            }
            if (intName != null) {
                environmentMap.put("int", intName);
            }
            if (perfName != null) {
                environmentMap.put("perf", perfName);
            }
            if (prodName != null) {
                environmentMap.put("prod", prodName);
                pipelineWidget.getOptions().put("prod", prodName);
            }

            Map<String, String> order = new HashMap<>();
            order.put("0", "dev");
            order.put("1", "qa");
            order.put("2", "prod");
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

        private Dashboard makeTeamDashboard(String template, String title, String appName, String owner, String configItemAppName, String configItemComponentName, String... compNames) {

            Application app = new Application(appName);
            for (String compName : compNames) {
                app.addComponent(new Component(compName));
            }
            List<String> activeWidgets = new ArrayList<>();
            Dashboard dashboard = new Dashboard(template, title, app, new Owner(owner, AuthType.STANDARD), DashboardType.Team, configItemAppName, configItemComponentName, activeWidgets, false, ScoreDisplayType.HEADER);
            return dashboard;
        }


        private CollectorItem makeDashboardCollectorItem(Dashboard dashboard) {
            CollectorItem collectorItem = new CollectorItem();
            collectorItem.setCollectorId(ObjectId.get());
            collectorItem.setDescription(dashboard.getTitle());
            collectorItem.getOptions().put("dashboardId", dashboard.getId().toString());
            return collectorItem;
        }

        private Pipeline makePipeline(CollectorItem collectorItem) {
            Pipeline pipeline = new Pipeline();
            pipeline.setCollectorItemId(collectorItem.getId());
            return pipeline;
        }

        private PipelineCommit makePipelineCommit(String revisionNumber, long timestamp) {
            PipelineCommit commit = new PipelineCommit();
            commit.setTimestamp(timestamp);
            commit.setScmRevisionNumber(revisionNumber);
            return commit;
        }

        //slow, explicit, and easy to read.
        private PipelineResponse makePipelineResponse(Pipeline pipeline, Dashboard dashboard) {
            PipelineResponse pipelineResponse = new PipelineResponse();
            List<PipelineStage> pipelineStageList = Arrays.asList(PipelineStage.COMMIT, PipelineStage.BUILD,
                    PipelineStage.valueOf("Dev ENV"), PipelineStage.valueOf("QA Env"), PipelineStage.valueOf("Int Env"), PipelineStage.valueOf("Perf Env"), PipelineStage.valueOf("Prod"));
            for (PipelineStage stage : pipelineStageList) {
                pipelineResponse.setStageCommits(stage, new ArrayList<PipelineResponseCommit>());
            }
            pipelineResponse.setCollectorItemId(pipeline.getCollectorItemId());
            return pipelineResponse;
        }

        @SuppressWarnings("unused")
        private void applyTimestamps(Pipeline pipeline, PipelineResponseCommit commit) {
            throw new NotImplementedException();
        }
    }
}
