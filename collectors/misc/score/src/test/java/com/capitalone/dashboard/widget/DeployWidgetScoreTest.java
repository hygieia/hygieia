package com.capitalone.dashboard.widget;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.model.score.settings.*;
import com.capitalone.dashboard.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;


@RunWith(MockitoJUnitRunner.class)
public class DeployWidgetScoreTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeployWidgetScoreTest.class);

  @Mock
  private EnvironmentComponentRepository environmentComponentRepository;
  @Mock
  private EnvironmentStatusRepository environmentStatusRepository;
  @Mock
  private ComponentRepository componentRepository;
  @InjectMocks
  private DeployWidgetScore deployWidgetScore;

  @Test
  public void calculateScoreNoThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("deploy-widget.json")).read();
    Widget deployWidget = mapper.readValue(content, Widget.class);
    DeployScoreSettings deployScoreSettings = getDeployScoreSettingsNoThreshold();

    LOGGER.info("deployWidget {}", deployWidget);


    content = Resources.asByteSource(Resources.getResource("deploy-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(deployWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("deploy-status-data.json")).read();
    List<EnvironmentStatus> environmentStatusResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, EnvironmentStatus.class));

    when(environmentStatusRepository.findByCollectorItemId(any())).thenReturn(environmentStatusResult);

    content = Resources.asByteSource(Resources.getResource("deploy-component-data.json")).read();
    List<EnvironmentComponent> environmentCompResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, EnvironmentComponent.class));

    when(environmentComponentRepository.findByCollectorItemId(any())).thenReturn(environmentCompResult);

    ScoreWeight scoreWeight = deployWidgetScore.processWidgetScore(deployWidget, deployScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("75"));

  }

  @Test
  public void calculateScoreWithPassThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("deploy-widget.json")).read();
    Widget deployWidget = mapper.readValue(content, Widget.class);
    DeployScoreSettings deployScoreSettings = getDeployScoreSettingsWithThreshold(10d);

    LOGGER.info("deployWidget {}", deployWidget);


    content = Resources.asByteSource(Resources.getResource("deploy-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);

    when(componentRepository.findOne(deployWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("deploy-status-data.json")).read();
    List<EnvironmentStatus> environmentStatusResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, EnvironmentStatus.class));

    when(environmentStatusRepository.findByCollectorItemId(any())).thenReturn(environmentStatusResult);

    content = Resources.asByteSource(Resources.getResource("deploy-component-data.json")).read();
    List<EnvironmentComponent> environmentCompResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, EnvironmentComponent.class));

    when(environmentComponentRepository.findByCollectorItemId(any())).thenReturn(environmentCompResult);

    ScoreWeight scoreWeight = deployWidgetScore.processWidgetScore(deployWidget, deployScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("75"));

  }

  @Test
  public void calculateScoreWithFailThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("deploy-widget.json")).read();
    Widget deployWidget = mapper.readValue(content, Widget.class);
    DeployScoreSettings deployScoreSettings = getDeployScoreSettingsWithThreshold(50d);

    LOGGER.info("deployWidget {}", deployWidget);


    content = Resources.asByteSource(Resources.getResource("deploy-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);

    when(componentRepository.findOne(deployWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("deploy-status-data.json")).read();
    List<EnvironmentStatus> environmentStatusResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, EnvironmentStatus.class));

    when(environmentStatusRepository.findByCollectorItemId(any())).thenReturn(environmentStatusResult);

    content = Resources.asByteSource(Resources.getResource("deploy-component-data.json")).read();
    List<EnvironmentComponent> environmentCompResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, EnvironmentComponent.class));

    when(environmentComponentRepository.findByCollectorItemId(any())).thenReturn(environmentCompResult);

    ScoreWeight scoreWeight = deployWidgetScore.processWidgetScore(deployWidget, deployScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("75"));

  }

  private DeployScoreSettings getDeployScoreSettingsNoThreshold() {
    DeployScoreSettings deployScoreSettings = new DeployScoreSettings();
    deployScoreSettings.setWeight(34);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());
    deployScoreSettings.setCriteria(criteria);

    return deployScoreSettings;
  }

  private DeployScoreSettings getDeployScoreSettingsWithThreshold(Double thresholdValue) {
    DeployScoreSettings deployScoreSettings = new DeployScoreSettings();
    deployScoreSettings.setWeight(34);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());

    ScoreComponentSettings deploySuccess = new ScoreComponentSettings();
    deploySuccess.setWeight(33);

    ScoreComponentSettings intancesOnline = new ScoreComponentSettings();
    intancesOnline.setWeight(33);
    ScoreThresholdSettings scoreThresholdSettings = new ScoreThresholdSettings();
    scoreThresholdSettings.setComparator(ScoreThresholdSettings.ComparatorType.less);
    scoreThresholdSettings.setValue(thresholdValue);
    scoreThresholdSettings.setType(ScoreThresholdSettings.ValueType.percent);
    scoreThresholdSettings.setScore(ScoreTypeValue.zeroScore());
    scoreThresholdSettings.getScore().setPropagate(PropagateType.widget);

    criteria.setDataRangeThresholds(Lists.newArrayList(scoreThresholdSettings));
    deployScoreSettings.setDeploySuccess(deploySuccess);
    deployScoreSettings.setIntancesOnline(intancesOnline);

    deployScoreSettings.setCriteria(criteria);
    LOGGER.info("deployScoreSettings {}", deployScoreSettings);
    return deployScoreSettings;
  }


}
