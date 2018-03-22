package com.capitalone.dashboard.widget;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.model.score.settings.*;
import com.capitalone.dashboard.repository.CommitRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.mysema.query.types.Predicate;


@RunWith(MockitoJUnitRunner.class)
public class GithubScmWidgetScoreTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(GithubScmWidgetScoreTest.class);


  @Mock
  private CommitRepository commitRepository;
  @Mock
  private ComponentRepository componentRepository;
  @InjectMocks
  private GithubScmWidgetScore githubScmWidgetScore;

  @Test
  public void calculateScoreNoThresholds() throws IOException {
    ObjectMapper mapper = getObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("github-widget.json")).read();
    Widget githubWidget = mapper.readValue(content, Widget.class);
    ScmScoreSettings scmScoreSettings = getGithubScmScoreSettingsNoThreshold();

    LOGGER.info("githubWidget {}", githubWidget);
    content = Resources.asByteSource(Resources.getResource("github-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(githubWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("github-commit-data.json")).read();
    List<Commit> commitResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, Commit.class));
    updateCommitResultTimestamps(commitResult);
    when(commitRepository.findAll((Predicate) any())).thenReturn(commitResult);

    ScoreWeight scoreWeight = githubScmWidgetScore.processWidgetScore(githubWidget, scmScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("71.4"));

  }

  @Test
  public void calculateScoreWithFailThresholds() throws IOException {
    ObjectMapper mapper = getObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("github-widget.json")).read();
    Widget githubWidget = mapper.readValue(content, Widget.class);
    ScmScoreSettings scmScoreSettings = getGithubScmScoreSettingsWithThreshold(90d);

    LOGGER.info("githubWidget {}", githubWidget);
    content = Resources.asByteSource(Resources.getResource("github-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(githubWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("github-commit-data.json")).read();
    List<Commit> commitResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, Commit.class));
    updateCommitResultTimestamps(commitResult);
    when(commitRepository.findAll((Predicate) any())).thenReturn(commitResult);

    ScoreWeight scoreWeight = githubScmWidgetScore.processWidgetScore(githubWidget, scmScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(scoreWeight.getScore().getScoreType(), is(ScoreType.zero_score));
  }


  @Test
  public void calculateScoreWithPassThresholds() throws IOException {
    ObjectMapper mapper = getObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("github-widget.json")).read();
    Widget githubWidget = mapper.readValue(content, Widget.class);
    ScmScoreSettings scmScoreSettings = getGithubScmScoreSettingsWithThreshold(10d);

    LOGGER.info("githubWidget {}", githubWidget);
    content = Resources.asByteSource(Resources.getResource("github-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(githubWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("github-commit-data.json")).read();
    List<Commit> commitResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, Commit.class));
    updateCommitResultTimestamps(commitResult);
    when(commitRepository.findAll((Predicate) any())).thenReturn(commitResult);

    ScoreWeight scoreWeight = githubScmWidgetScore.processWidgetScore(githubWidget, scmScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("71.4"));
  }




  private ScmScoreSettings getGithubScmScoreSettingsNoThreshold() {
    ScmScoreSettings scmScoreSettings = new ScmScoreSettings();
    scmScoreSettings.setNumberOfDays(14);
    scmScoreSettings.setWeight(33);

    ScoreComponentSettings commitsPerDay = new ScoreComponentSettings();
    commitsPerDay.setWeight(40);
    scmScoreSettings.setCommitsPerDay(commitsPerDay);

    return scmScoreSettings;
  }

  private ScmScoreSettings getGithubScmScoreSettingsWithThreshold(Double thresholdValue) {
    ScmScoreSettings scmScoreSettings = new ScmScoreSettings();
    scmScoreSettings.setNumberOfDays(14);
    scmScoreSettings.setWeight(33);

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

    ScoreComponentSettings commitsPerDay = new ScoreComponentSettings();
    commitsPerDay.setWeight(40);
    scmScoreSettings.setCommitsPerDay(commitsPerDay);

    scmScoreSettings.setCriteria(criteria);

    return scmScoreSettings;
  }

  private ObjectMapper getObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper;
  }
  
  private void updateCommitResultTimestamps(List<Commit> commits) {
    int twoInc = 0;
    int daySet = 0;
    for (Commit commit : commits) {
      commit.setScmCommitTimestamp(new LocalDate().minusDays(daySet).toDate().getTime());
      twoInc++;
      if (twoInc == 2) {
        twoInc = 0;
        daySet++;
        if (daySet == 10) {
          daySet = 0;
        }
      }
    }
  }

}
