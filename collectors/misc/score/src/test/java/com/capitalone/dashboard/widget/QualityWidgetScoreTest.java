package com.capitalone.dashboard.widget;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.model.score.settings.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;
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
import org.springframework.data.domain.*;


@RunWith(MockitoJUnitRunner.class)
public class QualityWidgetScoreTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(QualityWidgetScoreTest.class);

  @Mock
  private CodeQualityRepository codeQualityRepository;
  @Mock
  private ComponentRepository componentRepository;
  @InjectMocks
  private QualityWidgetScore qualityWidgetScore;

  @Test
  public void calculateScoreNoThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("quality-widget.json")).read();
    Widget qualityWidget = mapper.readValue(content, Widget.class);
    QualityScoreSettings qualityScoreSettings = getQualityScoreSettingsNoThreshold();

    LOGGER.info("qualityWidget {}", qualityWidget);


    content = Resources.asByteSource(Resources.getResource("quality-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(qualityWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("quality-data.json")).read();
    List<CodeQuality> qualityResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, CodeQuality.class));

    PageRequest pageRequest =
      new PageRequest(0, 1, Sort.Direction.DESC, "timestamp");

    PageImpl<CodeQuality> pageResult = new PageImpl(qualityResult, pageRequest, 1);


    when(codeQualityRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(pageResult);

    ScoreWeight scoreWeight = qualityWidgetScore.processWidgetScore(qualityWidget, qualityScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("59.7"));

  }


  @Test
  public void calculateScoreWithPassThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("quality-widget.json")).read();
    Widget qualityWidget = mapper.readValue(content, Widget.class);
    QualityScoreSettings qualityScoreSettings = getQualityScoreSettingsWithThreshold(10d);

    LOGGER.info("qualityWidget {}", qualityWidget);

    content = Resources.asByteSource(Resources.getResource("quality-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(qualityWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("quality-data.json")).read();
    List<CodeQuality> qualityResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, CodeQuality.class));

    PageRequest pageRequest =
      new PageRequest(0, 1, Sort.Direction.DESC, "timestamp");

    PageImpl<CodeQuality> pageResult = new PageImpl(qualityResult, pageRequest, 1);
    
    when(codeQualityRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(pageResult);

    ScoreWeight scoreWeight = qualityWidgetScore.processWidgetScore(qualityWidget, qualityScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(Utils.roundAlloc(scoreWeight.getScore().getScoreValue()), is("59.7"));

  }


  @Test
  public void calculateScoreWithFailThresholds() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("quality-widget.json")).read();
    Widget qualityWidget = mapper.readValue(content, Widget.class);
    QualityScoreSettings qualityScoreSettings = getQualityScoreSettingsWithThreshold(100d);

    LOGGER.info("qualityWidget {}", qualityWidget);

    content = Resources.asByteSource(Resources.getResource("quality-widget-component.json")).read();
    Component component = mapper.readValue(content, Component.class);


    when(componentRepository.findOne(qualityWidget.getComponentId())).thenReturn(component);

    content = Resources.asByteSource(Resources.getResource("quality-data.json")).read();
    List<CodeQuality> qualityResult = mapper.readValue(content,
      TypeFactory.defaultInstance().constructCollectionType(List.class, CodeQuality.class));

    for (CodeQualityMetric codeQualityMetric : qualityResult.get(0).getMetrics()) {
      if (codeQualityMetric.getName().equals("test_success_density")) {
        codeQualityMetric.setValue("99.0");
        break;
      }
    }

    PageRequest pageRequest =
      new PageRequest(0, 1, Sort.Direction.DESC, "timestamp");

    PageImpl<CodeQuality> pageResult = new PageImpl(qualityResult, pageRequest, 1);

    when(codeQualityRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(pageResult);

    ScoreWeight scoreWeight = qualityWidgetScore.processWidgetScore(qualityWidget, qualityScoreSettings);

    LOGGER.info("scoreWeight {}", scoreWeight);
    assertThat(scoreWeight.getScore().getScoreValue(), is(0.0d));

  }



  private QualityScoreSettings getQualityScoreSettingsNoThreshold() {
    QualityScoreSettings qualityScoreSettings = new QualityScoreSettings();
    qualityScoreSettings.setWeight(33);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());
    qualityScoreSettings.setCriteria(criteria);

    return qualityScoreSettings;
  }

  private QualityScoreSettings getQualityScoreSettingsWithThreshold(Double thresholdValue) {
    QualityScoreSettings qualityScoreSettings = new QualityScoreSettings();
    qualityScoreSettings.setWeight(33);

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

    ScoreComponentSettings unitTests = new ScoreComponentSettings();
    unitTests.setCriteria(criteria);
    qualityScoreSettings.setUnitTests(unitTests);

    return qualityScoreSettings;
  }

}
