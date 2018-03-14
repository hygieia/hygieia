package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.score.ScoreMetric;
import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.service.ScoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class ScoreControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;
  @Autowired private ScoreService scoreService;

  @Before
  public void before() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Test
  public void scoreMetric() throws Exception {
    ObjectId dashboardId = ObjectId.get();
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("score-metric.json")).read();
    ScoreMetric scoreMetric = mapper.readValue(content, ScoreMetric.class);

    DataResponse<ScoreMetric> response = new DataResponse<>(scoreMetric, 1);

    when(scoreService.getScoreMetric(dashboardId)).thenReturn(response);

    mockMvc.perform(get("/score/metric/" + dashboardId.toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.result.type", is(ScoreValueType.DASHBOARD.toString())))
      .andExpect(jsonPath("$.result.score", is("2.4")))
      .andExpect(jsonPath("$.result.total", is("5")))
      .andExpect(jsonPath("$.result.noScore", is(false)))
      .andExpect(jsonPath("$.result.componentMetrics", hasSize(4)))
      .andExpect(jsonPath("$.result.componentMetrics[0].score", is("0")))
      .andExpect(jsonPath("$.result.componentMetrics[0].total", is("5")))
      .andExpect(jsonPath("$.result.componentMetrics[0].weight", is("25")))
      .andExpect(jsonPath("$.result.componentMetrics[0].displayId", is("build")))
      .andExpect(jsonPath("$.result.componentMetrics[0].displayName", is("Build")))
      .andExpect(jsonPath("$.result.componentMetrics[0].state", is("criteria_failed")))

      .andExpect(jsonPath("$.result.componentMetrics[1].score", is("4.3")))
      .andExpect(jsonPath("$.result.componentMetrics[1].total", is("5")))
      .andExpect(jsonPath("$.result.componentMetrics[1].weight", is("25")))
      .andExpect(jsonPath("$.result.componentMetrics[1].displayId", is("codeanalysis")))
      .andExpect(jsonPath("$.result.componentMetrics[1].displayName", is("Quality")))
      .andExpect(jsonPath("$.result.componentMetrics[1].state", is("complete")))

      .andExpect(jsonPath("$.result.componentMetrics[2].score", is("1.2")))
      .andExpect(jsonPath("$.result.componentMetrics[2].total", is("5")))
      .andExpect(jsonPath("$.result.componentMetrics[2].weight", is("25")))
      .andExpect(jsonPath("$.result.componentMetrics[2].displayId", is("repo")))
      .andExpect(jsonPath("$.result.componentMetrics[2].displayName", is("GitHub SCM")))
      .andExpect(jsonPath("$.result.componentMetrics[2].state", is("complete")))

      .andExpect(jsonPath("$.result.componentMetrics[3].score", is("4")))
      .andExpect(jsonPath("$.result.componentMetrics[3].total", is("5")))
      .andExpect(jsonPath("$.result.componentMetrics[3].weight", is("25")))
      .andExpect(jsonPath("$.result.componentMetrics[3].displayId", is("deploy")))
      .andExpect(jsonPath("$.result.componentMetrics[3].displayName", is("Deploy")))
      .andExpect(jsonPath("$.result.componentMetrics[3].state", is("complete")));
  }
}
