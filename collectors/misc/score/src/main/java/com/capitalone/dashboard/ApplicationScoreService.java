package com.capitalone.dashboard;

import java.util.*;

import com.capitalone.dashboard.exception.PropagateScoreException;
import com.capitalone.dashboard.widget.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.collector.*;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.DashboardRepository;

@Service
public class ApplicationScoreService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationScoreService.class);

  private final ScoreSettings scoreSettings;
  private final DashboardRepository dashboardRepository;

  private final BuildWidgetScore buildWidgetScore;
  private final QualityWidgetScore qualityWidgetScore;
  private final DeployWidgetScore deployWidgetScore;
  private final GithubScmWidgetScore githubScmWidgetScore;

  @Autowired
  public ApplicationScoreService(
    ScoreSettings scoreSettings,
    DashboardRepository dashboardRepository,
    BuildWidgetScore buildWidgetScore,
    QualityWidgetScore qualityWidgetScore,
    DeployWidgetScore deployWidgetScore,
    GithubScmWidgetScore githubScmWidgetScore) {
    this.scoreSettings = scoreSettings;
    this.dashboardRepository = dashboardRepository;
    this.buildWidgetScore = buildWidgetScore;
    this.qualityWidgetScore = qualityWidgetScore;
    this.deployWidgetScore = deployWidgetScore;
    this.githubScmWidgetScore = githubScmWidgetScore;
    initScoreSettings();

  }

  public final void initScoreSettings() {
    ScoreCriteria criteria = this.scoreSettings.getCriteria();
    if (null == criteria) {
      criteria = new ScoreCriteria();
      this.scoreSettings.setCriteria(criteria);
    }
    if (null == criteria.getNoDataFound()) {
      criteria.setNoDataFound(ScoreTypeValue.zeroScore());
    }
    if (null == criteria.getNoWidgetFound()) {
      criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    }

    initBuildScoreSettings();
    initDeployScoreSettings();
    initQualityScoreSettings();
    initGithubScmScoreSettings();

    LOGGER.info("this.scoreSettings {}", this.scoreSettings);
  }

  private void initBuildScoreSettings() {
    BuildScoreSettings buildScoreSettings = this.scoreSettings.getBuildWidget();
    if (null != buildScoreSettings) {
      buildScoreSettings.setCriteria(
        Utils.mergeCriteria(this.scoreSettings.getCriteria(), buildScoreSettings.getCriteria())
        );

      initBuildScoreChildrenSettings(buildScoreSettings);

    }
  }

  private void initBuildScoreChildrenSettings(BuildScoreSettings buildScoreSettings) {
    ScoreParamSettings buildStatusSettings = Utils.getInstanceIfNull(
      buildScoreSettings.getStatus(),
      ScoreParamSettings.class
    );
    buildStatusSettings.setCriteria(
      Utils.mergeCriteria(buildScoreSettings.getCriteria(), buildStatusSettings.getCriteria())
    );
    buildScoreSettings.setStatus(buildStatusSettings);

    BuildScoreSettings.BuildDurationScoreSettings buildDurationSettings = Utils.getInstanceIfNull(
      buildScoreSettings.getDuration(),
      BuildScoreSettings.BuildDurationScoreSettings.class
    );

    buildDurationSettings.setCriteria(
      Utils.mergeCriteria(buildScoreSettings.getCriteria(), buildDurationSettings.getCriteria())
    );
    buildScoreSettings.setDuration(buildDurationSettings);
  }

  private void initDeployScoreSettings() {
    DeployScoreSettings deployScoreSettings = this.scoreSettings.getDeployWidget();
    if (null != deployScoreSettings) {

      deployScoreSettings.setCriteria(
        Utils.mergeCriteria(this.scoreSettings.getCriteria(), deployScoreSettings.getCriteria())
        );
      initDeployScoreChildrenSettings(deployScoreSettings);

    }
  }

  private void initDeployScoreChildrenSettings(DeployScoreSettings deployScoreSettings) {
    ScoreParamSettings deploySuccessSettings = Utils.getInstanceIfNull(deployScoreSettings.getDeploySuccess(), ScoreParamSettings.class);
    deploySuccessSettings.setCriteria(
      Utils.mergeCriteria(deployScoreSettings.getCriteria(), deploySuccessSettings.getCriteria())
    );
    deployScoreSettings.setDeploySuccess(deploySuccessSettings);

    ScoreParamSettings instanceOnlineSettings = Utils.getInstanceIfNull(deployScoreSettings.getIntancesOnline(), ScoreParamSettings.class);
    instanceOnlineSettings.setCriteria(
      Utils.mergeCriteria(deployScoreSettings.getCriteria(), instanceOnlineSettings.getCriteria())
    );
    deployScoreSettings.setIntancesOnline(instanceOnlineSettings);

  }

  private void initQualityScoreSettings() {
    QualityScoreSettings qualityScoreSettings = this.scoreSettings.getQualityWidget();
    if (null != qualityScoreSettings) {
      qualityScoreSettings.setCriteria(
        Utils.mergeCriteria(this.scoreSettings.getCriteria(), qualityScoreSettings.getCriteria())
        );

      initQualityScoreChildrenSettings(qualityScoreSettings);
    }
  }

  private void initQualityScoreChildrenSettings(QualityScoreSettings qualityScoreSettings) {
    ScoreParamSettings qualityCCSettings = Utils.getInstanceIfNull(qualityScoreSettings.getCodeCoverage(), ScoreParamSettings.class);
    qualityCCSettings.setCriteria(
      Utils.mergeCriteria(qualityScoreSettings.getCriteria(), qualityCCSettings.getCriteria())
    );
    qualityScoreSettings.setCodeCoverage(qualityCCSettings);

    ScoreParamSettings qualityUTSettings = Utils.getInstanceIfNull(qualityScoreSettings.getUnitTests(), ScoreParamSettings.class);
    qualityUTSettings.setCriteria(
      Utils.mergeCriteria(qualityScoreSettings.getCriteria(), qualityUTSettings.getCriteria())
    );
    qualityScoreSettings.setUnitTests(qualityUTSettings);

    QualityScoreSettings.ViolationsScoreSettings violationsSettings = Utils.getInstanceIfNull(qualityScoreSettings.getViolations(), QualityScoreSettings.ViolationsScoreSettings.class);
    violationsSettings.setCriteria(
      Utils.mergeCriteria(qualityScoreSettings.getCriteria(), violationsSettings.getCriteria())
    );
    qualityScoreSettings.setViolations(violationsSettings);
  }

  private void initGithubScmScoreSettings() {
    GithubScmScoreSettings githubScmScoreSettings = this.scoreSettings.getGithubScmWidget();
    if (null != githubScmScoreSettings) {
      githubScmScoreSettings.setCriteria(
        Utils.mergeCriteria(this.scoreSettings.getCriteria(), githubScmScoreSettings.getCriteria())
      );

      initGithubScmScoreChildrenSettings(githubScmScoreSettings);
    }
  }

  private void initGithubScmScoreChildrenSettings(GithubScmScoreSettings githubScmScoreSettings) {
    ScoreParamSettings commitsPerDaySettings = Utils.getInstanceIfNull(githubScmScoreSettings.getCommitsPerDay(), ScoreParamSettings.class);
    commitsPerDaySettings.setCriteria(
      Utils.mergeCriteria(githubScmScoreSettings.getCriteria(), commitsPerDaySettings.getCriteria())
    );
    githubScmScoreSettings.setCommitsPerDay(commitsPerDaySettings);
  }

  public ScoreMetric getScoreForApplication(ScoreApplication scoreApplication) {
    Dashboard dashboard = getDashboard(new ObjectId(scoreApplication.getDashboardId()));

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
      processWidgetScores(scoreApplication, dashboard.getWidgets())
      );

    LOGGER.info("dashboardScore: {}" + dashboardScore);
    ScoreMetric scoreMetric = ScoreCalculationUtils.generateScoreMetric(
      dashboardScore,
      this.scoreSettings.getMaxScore(),
      scoreApplication.getId(),
      dashboard.getId()
      );

    LOGGER.info("ScoreMetric scoreMetric " + scoreMetric.toString());

    return scoreMetric;
  }

  private Dashboard getDashboard(ObjectId id) {
    Dashboard dashboard = dashboardRepository.findOne(id);
    return dashboard;
  }

  private ScoreWeight getDashboardScoreFromWidgets(List<ScoreWeight> widgetScores) {
    if (null == widgetScores) {
      return getDashboardScore(new ScoreTypeValue(Constants.ZERO_SCORE), widgetScores);
    }

    try {
      //Calculate score
      return getDashboardScore(
        ScoreCalculationUtils.calculateWidgetScoreTypeValue(widgetScores, PropagateType.dashboard),
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

  private List<ScoreWeight> processWidgetScores(ScoreApplication scoreApplication, List<Widget> widgets) {
    List<ScoreWeight> scoreWeights = new ArrayList<>();

    Map<String, ScoreParamSettings> scoreParamSettingsMap = generateWidgetSettings(scoreApplication);

    Set<String> widgetTypes = scoreParamSettingsMap.keySet();
    if (widgetTypes.isEmpty()) {
      return null;
    }

    for (String widgetType : widgetTypes) {
      ScoreParamSettings scoreSettings = scoreParamSettingsMap.get(widgetType);
      WidgetScore widgetScore = getWidgetScoreByType(widgetType);
      ScoreWeight score = widgetScore.processWidgetScore(
        getWidgetByName(widgets, widgetType),
        scoreSettings
        );
      LOGGER.info("Widget for type: " + widgetType + " score" + score);

      if (null != score) {
        setWidgetAlert(score);
        scoreWeights.add(score);
      }
    }

    return scoreWeights;
  }

  private void setWidgetAlert(ScoreWeight score) {
    WidgetAlert widgetAlert = this.scoreSettings.getWidgetAlert();
    if (null == widgetAlert || null == widgetAlert.getValue()) {
      return;
    }
    score.setAlert(
      ScoreCalculationUtils.isWidgetAlert(widgetAlert, score.getScore().getScoreValue())
    );
  }

  private void addSettingsToMap(Map<String, ScoreParamSettings> scoreParamSettingsMap, String widgetType, ScoreParamSettings scoreParamSettings) {
    LOGGER.info("addSettingsToMap with widgetType:" + widgetType + " scoreParamSettings:" + scoreParamSettings);
    if (null != scoreParamSettings) {
      scoreParamSettingsMap.put(widgetType, scoreParamSettings);
    }
  }

  private Map<String, ScoreParamSettings> generateWidgetSettings(ScoreApplication scoreApplication) {
    Map<String, ScoreParamSettings> scoreParamSettingsMap = new HashMap<>();
    ScoreApplication.SettingsType settingsType = scoreApplication.getSettingsType();

    addSettingsToMap(
      scoreParamSettingsMap,
      Constants.WIDGET_BUILD,
      fetchWidgetSettings(
        settingsType,
        this.scoreSettings.getBuildWidget(),
        scoreApplication.getBuildWidget()
      ));

    addSettingsToMap(
      scoreParamSettingsMap,
      Constants.WIDGET_DEPLOY,
      fetchWidgetSettings(
        settingsType,
        this.scoreSettings.getDeployWidget(),
        scoreApplication.getDeployWidget()
      ));

    addSettingsToMap(
      scoreParamSettingsMap,
      Constants.WIDGET_CODE_ANALYSIS,
      fetchWidgetSettings(
        settingsType,
        this.scoreSettings.getQualityWidget(),
        scoreApplication.getQualityWidget()
      ));

    addSettingsToMap(
      scoreParamSettingsMap,
      Constants.WIDGET_GITHUB_SCM,
      fetchWidgetSettings(
        settingsType,
        this.scoreSettings.getGithubScmWidget(),
        scoreApplication.getGithubScmWidget()
      ));

    return scoreParamSettingsMap;
  }

  private ScoreParamSettings getSettingsIfEnabled(ScoreParamSettings scoreParamSettings) {
    if (null != scoreParamSettings && !scoreParamSettings.isDisabled()) {
      return scoreParamSettings;
    }
    return null;
  }

  private ScoreParamSettings fetchWidgetSettings(ScoreApplication.SettingsType settingsType, ScoreParamSettings widgetDefaultSettings, ScoreParamSettings widgetCustomSettings) {
    if (null != settingsType && null != widgetCustomSettings &&
      settingsType.equals(ScoreApplication.SettingsType.CUSTOM)) {
      ScoreParamSettings customSettings = getSettingsIfEnabled(widgetCustomSettings);
      customSettings.setCriteria(
        Utils.mergeCriteria(widgetDefaultSettings.getCriteria(), customSettings.getCriteria())
      );
      initWidgetChildrenSettings(customSettings);
      return customSettings;
    }

    return getSettingsIfEnabled(widgetDefaultSettings);
  }

  private void initWidgetChildrenSettings(ScoreParamSettings scoreParamSettings) {
    if (scoreParamSettings instanceof BuildScoreSettings) {
      initBuildScoreChildrenSettings((BuildScoreSettings) scoreParamSettings);
    } else if (scoreParamSettings instanceof QualityScoreSettings) {
      initQualityScoreChildrenSettings((QualityScoreSettings) scoreParamSettings);
    } else if (scoreParamSettings instanceof DeployScoreSettings) {
      initDeployScoreChildrenSettings((DeployScoreSettings) scoreParamSettings);
    } else if (scoreParamSettings instanceof GithubScmScoreSettings) {
      initGithubScmScoreChildrenSettings((GithubScmScoreSettings) scoreParamSettings);
    }
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
