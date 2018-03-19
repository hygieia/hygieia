package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.model.score.settings.*;
import com.capitalone.dashboard.repository.ScoreCriteriaSettingsRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

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
}
