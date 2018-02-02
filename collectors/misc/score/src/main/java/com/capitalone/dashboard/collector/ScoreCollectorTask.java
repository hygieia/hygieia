package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import com.capitalone.dashboard.ApplicationScoreService;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;

/**
 * Collects {@link EnvironmentComponent} and {@link EnvironmentStatus} data from
 * {@link ScoreApplication}s.
 */
@org.springframework.stereotype.Component
public class ScoreCollectorTask extends CollectorTask<ScoreCollector> {
  @SuppressWarnings({"unused", "PMD.UnusedPrivateField"})
  private static final Logger LOGGER = LoggerFactory.getLogger(ScoreCollectorTask.class);

  private final ScoreCollectorRepository scoreCollectorRepository;
  private final ScoreApplicationRepository scoreApplicationRepository;
  private final ScoreSettings scoreSettings;
  private final ScoreRepository scoreRepository;

  private final ComponentRepository dbComponentRepository;
  private final ApplicationScoreService applicationScoreService;

  @Autowired
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public ScoreCollectorTask(TaskScheduler taskScheduler,
    ScoreCollectorRepository scoreCollectorRepository,
    ScoreApplicationRepository scoreApplicationRepository,
    ScoreRepository scoreRepository,
    ScoreSettings scoreSettings,
    ComponentRepository dbComponentRepository,
    ApplicationScoreService applicationScoreService) {
    super(taskScheduler, "Score");
    this.scoreCollectorRepository = scoreCollectorRepository;
    this.scoreApplicationRepository = scoreApplicationRepository;
    this.scoreSettings = scoreSettings;
    this.scoreRepository = scoreRepository;
    this.dbComponentRepository = dbComponentRepository;
    this.applicationScoreService = applicationScoreService;

  }

  @Override
  public ScoreCollector getCollector() {
    return ScoreCollector.prototype();
  }

  @Override
  public BaseCollectorRepository<ScoreCollector> getCollectorRepository() {
    return scoreCollectorRepository;
  }

  @Override
  public String getCron() {
    return scoreSettings.getCron();
  }

  @Override
  @SuppressWarnings("PMD.ExcessiveMethodLength")
  public void collect(ScoreCollector collector) {

    logBanner("Score");

    long start = System.currentTimeMillis();

    //Step 1
    //Find enabled applications from collection repository
    //For each enabled application, get the dashboard id
    //For the dashboard id get the dashboard data
    //For each dashboard go through the widgets one by one


    //Get all dashboards with score widget
    List<ScoreApplication> scoreApplications = getScoreApplications(collector);
    log("No of dashboards with score widget=" + scoreApplications.size());
    for (ScoreApplication scoreApplication : scoreApplications) {
      collectScoreForApplication(scoreApplication);
    }
    log("Finished", start);
  }


  private void collectScoreForApplication(ScoreApplication scoreApplication) {

    ScoreMetric scoreMetric = this.applicationScoreService.getScoreForApplication(scoreApplication);

    if (null == scoreMetric) {
      return;
    }

    ScoreMetric existingScore = this.scoreRepository
      .findByCollectorItemId(scoreApplication.getId());
    if (null != existingScore) {
      this.scoreRepository.delete(existingScore);
    }
    this.scoreRepository.save(scoreMetric);
  }


  private List<ScoreApplication> getScoreApplications(ScoreCollector collector) {
    log("Score collector id = " + collector.getId());
    return scoreApplicationRepository.findEnabledScores(
      collector.getId());
  }

  /**
   * Clean up unused deployment collector items
   *
   * @param collector the {@link ScoreCollector}
   */
  //TODO: Siddharth - can this method be removed since its unused or was the intention to use it somewhere?
  @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
  private void clean(ScoreCollector collector) {
    //deleteUnwantedJobs(collector);
    Set<ObjectId> uniqueIDs = new HashSet<>();

    for (Component comp : dbComponentRepository
      .findAll()) {
      if (comp.getCollectorItems() == null || comp.getCollectorItems().isEmpty())
        continue;
      List<CollectorItem> itemList = comp.getCollectorItems().get(
        CollectorType.Score);
      if (itemList == null)
        continue;
      for (CollectorItem ci : itemList) {
        if (ci == null)
          continue;
        uniqueIDs.add(ci.getId());
      }
    }
    List<ScoreApplication> appList = new ArrayList<>();
    Set<ObjectId> udId = new HashSet<>();
    udId.add(collector.getId());
    for (ScoreApplication app : scoreApplicationRepository.findByCollectorIdIn(udId)) {
      if (app != null) {
        app.setEnabled(uniqueIDs.contains(app.getId()));
        appList.add(app);
      }
    }
    scoreApplicationRepository.save(appList);
  }



}
