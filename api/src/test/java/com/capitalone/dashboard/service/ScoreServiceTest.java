package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ScoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScoreServiceTest {
  @Mock
  private ComponentRepository componentRepository;
  @Mock
  private ScoreRepository scoreRepository;
  @Mock
  private CollectorRepository collectorRepository;
  @InjectMocks
  private ScoreServiceImpl scoreService;

  @Test
  public void getScoreMetric() throws Exception {
    ObjectId compId = ObjectId.get();
    Component component = new Component();
    CollectorItem item = new CollectorItem();
    item.setId(ObjectId.get());
    item.setCollectorId(ObjectId.get());
    component.getCollectorItems().put(CollectorType.Score, Arrays.asList(item));
    when(componentRepository.findOne(compId)).thenReturn(component);
    when(collectorRepository.findOne(item.getCollectorId())).thenReturn(new Collector());

    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("score-metric.json")).read();
    ScoreMetric scoreMetric = mapper.readValue(content, ScoreMetric.class);
    when(scoreRepository.findByCollectorItemId(item.getId())).thenReturn(scoreMetric);

    DataResponse<ScoreMetric> scoreMetricDataResponse = scoreService.getScoreMetric(compId);

    ScoreMetric scoreMetricResult = scoreMetricDataResponse.getResult();
    assertNotNull(scoreMetricResult);

    assertThat(scoreMetricResult.getType(), is(ScoreValueType.DASHBOARD));
    assertThat(scoreMetricResult.getScore(), is("2.4"));
    assertThat(scoreMetricResult.getTotal(), is("5"));
    assertThat(scoreMetricResult.isNoScore(), is(false));

    Collection<ScoreComponentMetric> scoreWidgetMetrics = scoreMetricResult.getComponentMetrics();
    assertThat(scoreWidgetMetrics, hasSize(4));

    Iterator<ScoreComponentMetric> iterator = scoreWidgetMetrics.iterator();

    ScoreComponentMetric scoreWidgetMetric = iterator.next();
    assertThat(scoreWidgetMetric.getScore(), is("0"));
    assertThat(scoreWidgetMetric.getTotal(), is("5"));
    assertThat(scoreWidgetMetric.getWeight(), is("25"));
    assertThat(scoreWidgetMetric.getDisplayId(), is("build"));
    assertThat(scoreWidgetMetric.getDisplayName(), is("Build"));
    assertThat(scoreWidgetMetric.getState(), is("criteria_failed"));

    scoreWidgetMetric = iterator.next();
    assertThat(scoreWidgetMetric.getScore(), is("4.3"));
    assertThat(scoreWidgetMetric.getTotal(), is("5"));
    assertThat(scoreWidgetMetric.getWeight(), is("25"));
    assertThat(scoreWidgetMetric.getDisplayId(), is("codeanalysis"));
    assertThat(scoreWidgetMetric.getDisplayName(), is("Quality"));
    assertThat(scoreWidgetMetric.getState(), is("complete"));

    scoreWidgetMetric = iterator.next();
    assertThat(scoreWidgetMetric.getScore(), is("1.2"));
    assertThat(scoreWidgetMetric.getTotal(), is("5"));
    assertThat(scoreWidgetMetric.getWeight(), is("25"));
    assertThat(scoreWidgetMetric.getDisplayId(), is("repo"));
    assertThat(scoreWidgetMetric.getDisplayName(), is("GitHub SCM"));
    assertThat(scoreWidgetMetric.getState(), is("complete"));

    scoreWidgetMetric = iterator.next();
    assertThat(scoreWidgetMetric.getScore(), is("4"));
    assertThat(scoreWidgetMetric.getTotal(), is("5"));
    assertThat(scoreWidgetMetric.getWeight(), is("25"));
    assertThat(scoreWidgetMetric.getDisplayId(), is("deploy"));
    assertThat(scoreWidgetMetric.getDisplayName(), is("Deploy"));
    assertThat(scoreWidgetMetric.getState(), is("complete"));





  }
}
