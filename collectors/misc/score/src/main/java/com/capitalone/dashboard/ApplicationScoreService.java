package com.capitalone.dashboard;

import java.util.*;

import com.capitalone.dashboard.exception.PropagateScoreException;
import com.capitalone.dashboard.model.score.ScoreCollectorItem;
import com.capitalone.dashboard.model.score.ScoreMetric;
import com.capitalone.dashboard.model.score.settings.*;
import com.capitalone.dashboard.widget.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.DashboardRepository;

/**
 * Service to calculate Dashboard score for a {@link ScoreCollectorItem}
 * <p>
 *   On construction the service will initialize settings for widgets.
 *   These settings will be used to calculate score
 * <p>
 *
 */
@Service
public class ApplicationScoreService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationScoreService.class);

  private final DashboardRepository dashboardRepository;

  private final BuildWidgetScore buildWidgetScore;
  private final QualityWidgetScore qualityWidgetScore;
  private final DeployWidgetScore deployWidgetScore;
  private final GithubScmWidgetScore githubScmWidgetScore;

  @Autowired
  public ApplicationScoreService(
    DashboardRepository dashboardRepository,
    BuildWidgetScore buildWidgetScore,
    QualityWidgetScore qualityWidgetScore,
    DeployWidgetScore deployWidgetScore,
    GithubScmWidgetScore githubScmWidgetScore) {
    this.dashboardRepository = dashboardRepository;
    this.buildWidgetScore = buildWidgetScore;
    this.qualityWidgetScore = qualityWidgetScore;
    this.deployWidgetScore = deployWidgetScore;
    this.githubScmWidgetScore = githubScmWidgetScore;
  }


  /**
   * Calculate score for a {@link ScoreCollectorItem}
   *
   * @param scoreApplication Score Application collector item for a dashboard
   * @param scoreCriteriaSettings Score Criteria Settings
   * @return Score for dashboard
   */
  public ScoreMetric getScoreForApplication(ScoreCollectorItem scoreApplication, ScoreCriteriaSettings scoreCriteriaSettings) {
    Dashboard dashboard = getDashboard(scoreApplication.getDashboardId());

    if (null == dashboard) {
      LOGGER.info("Dashboard with id " + scoreApplication.getDashboardId() + " is null!");
      return null;
    }

    LOGGER.info("Dashboard title:" + dashboard.getTitle() + ", type:" + dashboard.getType() + ", owner:" + dashboard.getOwner());

    if (null == dashboard.getType() || !dashboard.getType().equals(DashboardType.Team)) {
      return null;
    }

    LOGGER.info("dashboard.getTitle():" + dashboard.getTitle() + " " + dashboard.getOwner());

    ScoreWeight dashboardScore = getDashboardScoreFromWidgets(
      processWidgetScores(dashboard.getWidgets(), scoreCriteriaSettings)
      );

    LOGGER.debug("dashboardScore: {}" + dashboardScore);
    ScoreMetric scoreMetric = ScoreCalculationUtils.generateScoreMetric(
      dashboardScore,
      scoreCriteriaSettings.getMaxScore(),
      scoreApplication.getId(),
      dashboard.getId()
      );

    LOGGER.debug("ScoreMetric scoreMetric {}", scoreMetric);

    return scoreMetric;
  }

  private Dashboard getDashboard(ObjectId id) {
    Dashboard dashboard = dashboardRepository.findOne(id);
    return dashboard;
  }

  /**
   * Calculate dashboard score from widget score settings
   *
   * @param widgetScores widget score settings
   * @return dashboard score
   */
  private ScoreWeight getDashboardScoreFromWidgets(List<ScoreWeight> widgetScores) {
    if (null == widgetScores) {
      return getDashboardScore(new ScoreTypeValue(Constants.ZERO_SCORE), widgetScores);
    }

    try {
      //Calculate score
      return getDashboardScore(
        ScoreCalculationUtils.calculateComponentScoreTypeValue(widgetScores, PropagateType.dashboard),
        widgetScores
      );
    } catch (PropagateScoreException ex) {
      ScoreWeight scoreDashboard = new ScoreWeight();
      scoreDashboard.setScore(ex.getScore());
      scoreDashboard.setMessage(ex.getMessage());
      scoreDashboard.setState(ex.getState());
      scoreDashboard.setChildren(widgetScores);
      return scoreDashboard;
    }
  }

  private ScoreWeight getDashboardScore(ScoreTypeValue score, List<ScoreWeight> scoreWidgets) {
    ScoreWeight scoreDashboard = new ScoreWeight();
    scoreDashboard.setScore(score);
    scoreDashboard.setChildren(scoreWidgets);
    scoreDashboard.setState(ScoreWeight.ProcessingState.complete);

    return scoreDashboard;
  }

  /**
   * Process scores for each widget based on widget settings
   *
   * @param widgets List of widgets
   * @param scoreCriteriaSettings Score Criteria Settings
   * @return List of widget scores
   */
  private List<ScoreWeight> processWidgetScores(List<Widget> widgets, ScoreCriteriaSettings scoreCriteriaSettings) {
    List<ScoreWeight> scoreWeights = new ArrayList<>();

    Map<String, ScoreComponentSettings> scoreParamSettingsMap = generateWidgetSettings(scoreCriteriaSettings);

    Set<String> widgetTypes = scoreParamSettingsMap.keySet();
    if (widgetTypes.isEmpty()) {
      return null;
    }

    //For each widget calculate score
    for (String widgetType : widgetTypes) {
      ScoreComponentSettings scoreSettings = scoreParamSettingsMap.get(widgetType);
      WidgetScore widgetScore = getWidgetScoreByType(widgetType);
      ScoreWeight score = widgetScore.processWidgetScore(
        getWidgetByName(widgets, widgetType),
        scoreSettings
        );
      LOGGER.debug("Widget for type: {} score {}", widgetType, score);

      if (null != score) {
        setWidgetAlert(score, scoreCriteriaSettings.getComponentAlert());
        scoreWeights.add(score);
      }
    }

    return scoreWeights;
  }

  private void setWidgetAlert(ScoreWeight score, ComponentAlert componentAlert) {
    if (null == componentAlert || null == componentAlert.getValue()) {
      return;
    }
    score.setAlert(
      ScoreCalculationUtils.isComponentAlert(componentAlert, score.getScore().getScoreValue())
    );
  }

  /**
   * Add settings for widget in map if it exists
   * This map is used to calculate score for team
   *
   * @param scoreParamSettingsMap Map to update the settings for a widget
   * @param widgetType Type of widget
   * @param scoreComponentSettings score settings for the widget
   */
  private void addSettingsToMap(Map<String, ScoreComponentSettings> scoreParamSettingsMap, String widgetType, ScoreComponentSettings scoreComponentSettings) {
    LOGGER.debug("addSettingsToMap with widgetType: {} scoreParamSettings: {}", widgetType, scoreComponentSettings);
    if (null != scoreComponentSettings) {
      scoreParamSettingsMap.put(widgetType, scoreComponentSettings);
    }
  }

  /**
   * Generate criteria settings for each widget type
   *
   * @param scoreCriteriaSettings Score Criteria Settings
   * @return Map of settings by each widget name
   */
  private Map<String, ScoreComponentSettings> generateWidgetSettings(ScoreCriteriaSettings scoreCriteriaSettings) {
    Map<String, ScoreComponentSettings> scoreParamSettingsMap = new HashMap<>();

    addSettingsToMap(
      scoreParamSettingsMap,
      Constants.WIDGET_BUILD,
      getSettingsIfEnabled(
        scoreCriteriaSettings.getBuild()
      ));

    addSettingsToMap(
      scoreParamSettingsMap,
      Constants.WIDGET_DEPLOY,
      getSettingsIfEnabled(
        scoreCriteriaSettings.getDeploy()
      ));

    addSettingsToMap(
      scoreParamSettingsMap,
      Constants.WIDGET_CODE_ANALYSIS,
      getSettingsIfEnabled(
        scoreCriteriaSettings.getQuality()
      ));

    addSettingsToMap(
      scoreParamSettingsMap,
      Constants.WIDGET_GITHUB_SCM,
      getSettingsIfEnabled(
        scoreCriteriaSettings.getScm()
      ));

    return scoreParamSettingsMap;
  }

  private ScoreComponentSettings getSettingsIfEnabled(ScoreComponentSettings scoreComponentSettings) {
    if (null != scoreComponentSettings && !scoreComponentSettings.isDisabled()) {
      return scoreComponentSettings;
    }
    return null;
  }

  private Widget getWidgetByName(List<Widget> widgets, String name) {
    for (Widget widget : widgets) {
      if (name.equals(widget.getName())) {
        return widget;
      }
    }
    return null;
  }

  private WidgetScore getWidgetScoreByType(String widgetType) {
    if (widgetType.equals(Constants.WIDGET_BUILD)) {
      return this.buildWidgetScore;
    } else if (widgetType.equals(Constants.WIDGET_DEPLOY)) {
      return this.deployWidgetScore;
    } else if (widgetType.equals(Constants.WIDGET_CODE_ANALYSIS)) {
      return this.qualityWidgetScore;
    } else if (widgetType.equals(Constants.WIDGET_GITHUB_SCM)) {
      return this.githubScmWidgetScore;
    }
    return null;
  }



}
