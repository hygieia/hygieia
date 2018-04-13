package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.model.score.settings.PropagateType;
import com.capitalone.dashboard.model.score.settings.ScoreCriteriaSettings;
import com.capitalone.dashboard.model.score.settings.ScoreThresholdSettings;
import com.capitalone.dashboard.model.score.settings.ScoreType;
import com.capitalone.dashboard.service.ScoreCriteriaSettingsService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class ScoreCriteriaSettingsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private ScoreCriteriaSettingsService scoreCriteriaSettingsService;

  @Before
  public void before() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Test
  public void getScoreCriteriaSettingsByType() throws Exception {
    ObjectMapper mapper = getObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("score-criteria-settings.json")).read();
    ScoreCriteriaSettings scoreCriteriaSettings = mapper.readValue(content, ScoreCriteriaSettings.class);

    when(scoreCriteriaSettingsService.getScoreCriteriaSettingsByType(ScoreValueType.DASHBOARD))
      .thenReturn(scoreCriteriaSettings);

    mockMvc.perform(get("/score/settings/type/dashboard"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.maxScore", is(5)))
      .andExpect(jsonPath("$.type", is(ScoreValueType.DASHBOARD.toString())))
      .andExpect(jsonPath("$.componentAlert.value", is(0d)))
      .andExpect(jsonPath("$.componentAlert.comparator",
        is(ScoreThresholdSettings.ComparatorType.less_or_equal.toString())))
      .andExpect(jsonPath("$.build.numberOfDays", is(14)))
      .andExpect(jsonPath("$.build.weight", is(25)))
      .andExpect(jsonPath("$.build.criteria.noWidgetFound.scoreType", is(ScoreType.zero_score.toString())))
      .andExpect(jsonPath("$.build.criteria.noWidgetFound.propagate", is(PropagateType.no.toString())))
      .andExpect(jsonPath("$.build.criteria.noDataFound.scoreType", is(ScoreType.zero_score.toString())))
      .andExpect(jsonPath("$.build.criteria.noDataFound.propagate", is(PropagateType.no.toString())))
      .andExpect(jsonPath("$.build.status.weight", is(50)))
      .andExpect(jsonPath("$.build.duration.weight", is(50)));

  }

  private ObjectMapper getObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper;
  }
}
