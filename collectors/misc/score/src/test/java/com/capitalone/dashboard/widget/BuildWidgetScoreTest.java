package com.capitalone.dashboard.widget;

import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.score.settings.*;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.mysema.query.types.Predicate;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BuildWidgetScoreTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(BuildWidgetScoreTest.class);


  @Mock
  private BuildRepository buildRepository;
  @Mock
  private ComponentRepository componentRepository;
  @InjectMocks
  private BuildWidgetScore buildWidgetScore;

  @Test
  public void calculateScoreNoThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("build-widget.json")).read();
    Widget buildWidget = mapper.readValue(content, Widget.class);
    BuildScoreSettings buildScoreSettings = getBuildScoreSettingsNoThreshold();

    LOGGER.info("buildWidget {}", buildWidget);

    BuildSearch request = new BuildSearch();
    request.setComponentId(buildWidget.getComponentId());
    request.setNumberOfDays(buildScoreSettings.getNumberOfDays());

    content = Resources.asByteSource(Resources.getResource("build-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(request.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("build-data.json")).read();
    List<Build> buildResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, Build.class));
    updateBuildResultTimestamps(buildResult);

    when(buildRepository.findAll((Predicate) any())).thenReturn(buildResult);

    ScoreWeight scoreWeight = buildWidgetScore.processWidgetScore(buildWidget, buildScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("62.3"));

  }

  @Test
  public void calculateScoreWithFailThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("build-widget.json")).read();
    Widget buildWidget = mapper.readValue(content, Widget.class);
    BuildScoreSettings buildScoreSettings = getBuildScoreSettingsWithThreshold(90d);

    LOGGER.info("buildWidget {}", buildWidget);

    BuildSearch request = new BuildSearch();
    request.setComponentId(buildWidget.getComponentId());
    request.setNumberOfDays(buildScoreSettings.getNumberOfDays());

    content = Resources.asByteSource(Resources.getResource("build-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(request.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("build-data.json")).read();
    List<Build> buildResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, Build.class));
    updateBuildResultTimestamps(buildResult);

    when(buildRepository.findAll((Predicate) any())).thenReturn(buildResult);

    ScoreWeight scoreWeight = buildWidgetScore.processWidgetScore(buildWidget, buildScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(scoreWeight.getScore().getScoreType(), is(ScoreType.zero_score));
  }


  @Test
  public void calculateScoreWithPassThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("build-widget.json")).read();
    Widget buildWidget = mapper.readValue(content, Widget.class);
    BuildScoreSettings buildScoreSettings = getBuildScoreSettingsWithThreshold(10d);

    LOGGER.info("buildWidget {}", buildWidget);

    BuildSearch request = new BuildSearch();
    request.setComponentId(buildWidget.getComponentId());
    request.setNumberOfDays(buildScoreSettings.getNumberOfDays());

    content = Resources.asByteSource(Resources.getResource("build-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(request.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("build-data.json")).read();
    List<Build> buildResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, Build.class));
    updateBuildResultTimestamps(buildResult);

    when(buildRepository.findAll((Predicate) any())).thenReturn(buildResult);

    ScoreWeight scoreWeight = buildWidgetScore.processWidgetScore(buildWidget, buildScoreSettings);


    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("63.6"));

  }



  private BuildScoreSettings getBuildScoreSettingsNoThreshold() {
    BuildScoreSettings buildScoreSettings = new BuildScoreSettings();
    buildScoreSettings.setNumberOfDays(14);
    buildScoreSettings.setWeight(33);

    ScoreComponentSettings statusSettings = new ScoreComponentSettings();
    statusSettings.setWeight(40);
    buildScoreSettings.setStatus(statusSettings);

    BuildScoreSettings.BuildDurationScoreSettings durationSettings = new BuildScoreSettings.BuildDurationScoreSettings();
    durationSettings.setWeight(60);
    buildScoreSettings.setDuration(durationSettings);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());
    buildScoreSettings.setCriteria(criteria);

    return buildScoreSettings;
  }

  private BuildScoreSettings getBuildScoreSettingsWithThreshold(Double thresholdValue) {
    BuildScoreSettings buildScoreSettings = new BuildScoreSettings();
    buildScoreSettings.setNumberOfDays(14);
    buildScoreSettings.setWeight(33);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());

    ScoreThresholdSettings scoreThresholdSettings = new ScoreThresholdSettings();
    scoreThresholdSettings.setComparator(ScoreThresholdSettings.ComparatorType.less);
    scoreThresholdSettings.setValue(thresholdValue);
    scoreThresholdSettings.setType(ScoreThresholdSettings.ValueType.percent);
    scoreThresholdSettings.setScore(ScoreTypeValue.zeroScore());
    scoreThresholdSettings.getScore().setPropagate(PropagateType.dashboard);

    criteria.setDataRangeThresholds(Lists.newArrayList(scoreThresholdSettings));

    buildScoreSettings.setCriteria(criteria);

    return buildScoreSettings;
  }

  private void updateBuildResultTimestamps(List<Build> buildResults) {
    int twoInc = 0;
    int daySet = 0;
    for (Build buildResult : buildResults) {
      buildResult.setTimestamp(new LocalDate().minusDays(daySet).toDate().getTime());
      twoInc++;
      if (twoInc == 2) {
        twoInc = 0;
        daySet++;
      }
    }
  }

}
