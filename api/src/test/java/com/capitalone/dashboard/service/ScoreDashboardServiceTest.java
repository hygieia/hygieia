package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.score.ScoreCollectorItem;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.ScoreCollectorItemRepository;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.capitalone.dashboard.fixture.DashboardFixture.makeDashboard;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScoreDashboardServiceTest {
  @Mock
  private CollectorService collectorService;
  @Mock
  private ScoreCollectorItemRepository scoreCollectorItemRepository;
  @Mock
  private CollectorItemRepository collectorItemRepository;
  @InjectMocks
  private ScoreDashboardServiceImpl scoreDashboardService;

  private String configItemBusServName = "ASVTEST";
  private String configItemBusAppName = "BAPTEST";

  @Test
  public void addScoreForDashboard() throws Exception {
    Dashboard dashboard = makeDashboard("t1", "title", "app", "comp", "someuser", DashboardType.Team, configItemBusServName, configItemBusAppName);
    dashboard.setScoreEnabled(true);
    dashboard.setScoreDisplay(ScoreDisplayType.HEADER);
    dashboard.setId(ObjectId.get());

    Collector collector = new Collector();
    collector.setCollectorType(CollectorType.Score);
    collector.setId(ObjectId.get());

    ScoreCollectorItem scoreCollectorItem = generateScoreCollectorItem(
      dashboard.getId(),
      collector.getId()
    );

    when(collectorService.collectorsByType(CollectorType.Score)).thenReturn(Lists.newArrayList(collector));
    when(collectorService.createCollectorItem(scoreCollectorItem)).thenReturn(scoreCollectorItem);

    CollectorItem scoreCollectorItemResult = scoreDashboardService.addScoreForDashboard(dashboard);
    assertNotNull(scoreCollectorItemResult);
    assertThat(scoreCollectorItemResult.getCollectorId(), is(collector.getId()));
    assertThat(scoreCollectorItemResult.getOptions().get("dashboardId"), is(dashboard.getId()));
    assertThat(scoreCollectorItemResult.isEnabled(), is(true));
  }

  @Test
  public void addScoreForDashboardScoreEnabled() throws Exception {
    Dashboard dashboard = makeDashboard("t1", "title", "app", "comp", "someuser", DashboardType.Team, configItemBusServName, configItemBusAppName);
    dashboard.setScoreEnabled(true);
    dashboard.setScoreDisplay(ScoreDisplayType.HEADER);
    dashboard.setId(ObjectId.get());

    Collector collector = new Collector();
    collector.setCollectorType(CollectorType.Score);
    collector.setId(ObjectId.get());

    ScoreCollectorItem scoreCollectorItem = generateScoreCollectorItem(
      dashboard.getId(),
      collector.getId()
    );

    when(collectorService.collectorsByType(CollectorType.Score)).thenReturn(Lists.newArrayList(collector));
    when(collectorService.createCollectorItem(scoreCollectorItem)).thenReturn(scoreCollectorItem);

    CollectorItem scoreCollectorItemResult = scoreDashboardService.addScoreForDashboardIfScoreEnabled(dashboard);
    assertNotNull(scoreCollectorItemResult);
    assertThat(scoreCollectorItemResult.getCollectorId(), is(collector.getId()));
    assertThat(scoreCollectorItemResult.getOptions().get("dashboardId"), is(dashboard.getId()));
    assertThat(scoreCollectorItemResult.isEnabled(), is(true));
  }

  @Test
  public void addScoreForDashboardScoreDisabled() throws Exception {
    Dashboard dashboard = makeDashboard("t1", "title", "app", "comp", "someuser", DashboardType.Team, configItemBusServName, configItemBusAppName);
    dashboard.setScoreEnabled(false);
    dashboard.setScoreDisplay(ScoreDisplayType.HEADER);
    dashboard.setId(ObjectId.get());

    CollectorItem scoreCollectorItemResult = scoreDashboardService.addScoreForDashboardIfScoreEnabled(dashboard);
    assertNull(scoreCollectorItemResult);
  }

  @Test
  public void disableScoreForDashboard() throws Exception {
    Dashboard dashboard = makeDashboard("t1", "title", "app", "comp", "someuser", DashboardType.Team, configItemBusServName, configItemBusAppName);
    dashboard.setScoreEnabled(false);
    dashboard.setScoreDisplay(ScoreDisplayType.HEADER);
    dashboard.setId(ObjectId.get());

    Collector collector = new Collector();
    collector.setCollectorType(CollectorType.Score);
    collector.setId(ObjectId.get());

    ScoreCollectorItem scoreCollectorItem = generateScoreCollectorItem(
      dashboard.getId(),
      collector.getId()
    );

    when(collectorService.collectorsByType(CollectorType.Score)).thenReturn(Lists.newArrayList(collector));
    when(scoreCollectorItemRepository.findCollectorItemByCollectorIdAndDashboardId(
      collector.getId(),
      dashboard.getId()
    )).thenReturn(scoreCollectorItem);

    scoreCollectorItem.setEnabled(false);

    when(collectorItemRepository.save(scoreCollectorItem)).thenReturn(scoreCollectorItem);
    CollectorItem scoreCollectorItemResult = scoreDashboardService.disableScoreForDashboard(dashboard);
    assertNotNull(scoreCollectorItemResult);
    assertThat(scoreCollectorItemResult.getCollectorId(), is(collector.getId()));
    assertThat(scoreCollectorItemResult.getOptions().get("dashboardId"), is(dashboard.getId()));
    assertThat(scoreCollectorItemResult.isEnabled(), is(false));
  }

  @Test
  public void editScoreForDashboardDisable() throws Exception {
    Dashboard dashboard = makeDashboard("t1", "title", "app", "comp", "someuser", DashboardType.Team, configItemBusServName, configItemBusAppName);
    dashboard.setScoreEnabled(false);
    dashboard.setScoreDisplay(ScoreDisplayType.HEADER);
    dashboard.setId(ObjectId.get());

    Collector collector = new Collector();
    collector.setCollectorType(CollectorType.Score);
    collector.setId(ObjectId.get());

    ScoreCollectorItem scoreCollectorItem = generateScoreCollectorItem(
      dashboard.getId(),
      collector.getId()
    );

    when(collectorService.collectorsByType(CollectorType.Score)).thenReturn(Lists.newArrayList(collector));
    when(scoreCollectorItemRepository.findCollectorItemByCollectorIdAndDashboardId(
      collector.getId(),
      dashboard.getId()
    )).thenReturn(scoreCollectorItem);

    scoreCollectorItem.setEnabled(false);

    when(collectorItemRepository.save(scoreCollectorItem)).thenReturn(scoreCollectorItem);
    CollectorItem scoreCollectorItemResult = scoreDashboardService.editScoreForDashboard(dashboard);
    assertNotNull(scoreCollectorItemResult);
    assertThat(scoreCollectorItemResult.getCollectorId(), is(collector.getId()));
    assertThat(scoreCollectorItemResult.getOptions().get("dashboardId"), is(dashboard.getId()));
    assertThat(scoreCollectorItemResult.isEnabled(), is(false));
  }

  @Test
  public void editScoreForDashboardEnable() throws Exception {
    Dashboard dashboard = makeDashboard("t1", "title", "app", "comp", "someuser", DashboardType.Team, configItemBusServName, configItemBusAppName);
    dashboard.setScoreEnabled(true);
    dashboard.setScoreDisplay(ScoreDisplayType.HEADER);
    dashboard.setId(ObjectId.get());

    Collector collector = new Collector();
    collector.setCollectorType(CollectorType.Score);
    collector.setId(ObjectId.get());

    ScoreCollectorItem scoreCollectorItem = generateScoreCollectorItem(
      dashboard.getId(),
      collector.getId()
    );

    when(collectorService.collectorsByType(CollectorType.Score)).thenReturn(Lists.newArrayList(collector));
    when(collectorService.createCollectorItem(scoreCollectorItem)).thenReturn(scoreCollectorItem);

    CollectorItem scoreCollectorItemResult = scoreDashboardService.editScoreForDashboard(dashboard);
    assertNotNull(scoreCollectorItemResult);
    assertThat(scoreCollectorItemResult.getCollectorId(), is(collector.getId()));
    assertThat(scoreCollectorItemResult.getOptions().get("dashboardId"), is(dashboard.getId()));
    assertThat(scoreCollectorItemResult.isEnabled(), is(true));
  }


  private ScoreCollectorItem generateScoreCollectorItem(ObjectId dashboardId, ObjectId collectorId) {
    ScoreCollectorItem item = new ScoreCollectorItem();
    item.setCollectorId(collectorId);
    item.setDashboardId(dashboardId);
    item.setDescription(dashboardId.toString());
    item.setLastUpdated(System.currentTimeMillis());
    item.setEnabled(true);
    return item;
  }

}
