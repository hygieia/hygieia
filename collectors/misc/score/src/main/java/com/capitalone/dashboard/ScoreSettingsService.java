package com.capitalone.dashboard;

import com.capitalone.dashboard.collector.ScoreSettings;
import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.model.score.settings.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to fetch initialized settings used for score calculation
 */
@Service
public class ScoreSettingsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScoreSettingsService.class);

  private final ScoreSettings scoreSettings;

  private ScoreCriteriaSettings dashboardScoreCriteriaSettings;

  @Autowired
  public ScoreSettingsService(
    ScoreSettings scoreSettings
  ) {
    this.scoreSettings = scoreSettings;
    initScoreSettings();
    generateDashboardScoreSettings();

  }

  /**
   * Generate Score Criteria Settings from Score Settings
   */
  public final void generateDashboardScoreSettings() {
    ScoreCriteriaSettings dashboardScoreCriteriaSettings = new ScoreCriteriaSettings();
    dashboardScoreCriteriaSettings.setMaxScore(
      this.scoreSettings.getMaxScore()
    );
    dashboardScoreCriteriaSettings.setType(ScoreValueType.DASHBOARD);
    dashboardScoreCriteriaSettings.setComponentAlert(
      ComponentAlert.cloneComponentAlert(this.scoreSettings.getComponentAlert())
    );
    dashboardScoreCriteriaSettings.setBuild(
      BuildScoreSettings.cloneBuildScoreSettings(this.scoreSettings.getBuildWidget())
    );
    dashboardScoreCriteriaSettings.setDeploy(
      DeployScoreSettings.cloneDeployScoreSettings(this.scoreSettings.getDeployWidget())
    );
    dashboardScoreCriteriaSettings.setQuality(
      QualityScoreSettings.cloneQualityScoreSettings(this.scoreSettings.getQualityWidget())
    );
    dashboardScoreCriteriaSettings.setScm(
      ScmScoreSettings.cloneScmScoreSettings(this.scoreSettings.getScmWidget())
    );

    LOGGER.debug("Generate Score Dashboard Settings dashboardScoreCriteriaSettings {}", dashboardScoreCriteriaSettings);

    this.dashboardScoreCriteriaSettings = dashboardScoreCriteriaSettings;
  }

  public ScoreCriteriaSettings getDashboardScoreCriteriaSettings() {
    return this.dashboardScoreCriteriaSettings;
  }

  /**
   * Initialize score settings for widgets
   * <p><ul>
   * <li>Build
   * <li>Deploy
   * <li>Quality
   * <li>Github SCM
   * </ul><p>
   */
  public final void initScoreSettings() {
    ScoreCriteria criteria = this.scoreSettings.getCriteria();
    if (null == criteria) {
      criteria = new ScoreCriteria();
      this.scoreSettings.setCriteria(criteria);
    }

    //Initialize criteria values for no data found and no widget found conditions
    //Default value is 0 score for no data found and no widget found
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

    LOGGER.debug("Initialized scoreSettings {}", this.scoreSettings);
  }

  /**
   * Initialize build score settings
   * <p>
   *  If build widget settings are present merge it with default score settings
   *  Initialize settings for children scores in build widget
   */
  private void initBuildScoreSettings() {
    BuildScoreSettings buildScoreSettings = this.scoreSettings.getBuildWidget();
    if (null != buildScoreSettings) {
      buildScoreSettings.setCriteria(
        Utils.mergeCriteria(this.scoreSettings.getCriteria(), buildScoreSettings.getCriteria())
      );

      initBuildScoreChildrenSettings(buildScoreSettings);

    }
  }

  /**
   * Initialize Settings for child components in build widget
   * 1. Build Status criteria settings
   * 2. Build Duration criteria settings
   *
   * @param buildScoreSettings
   */
  private void initBuildScoreChildrenSettings(BuildScoreSettings buildScoreSettings) {
    ScoreComponentSettings buildStatusSettings = Utils.getInstanceIfNull(
      buildScoreSettings.getStatus(),
      ScoreComponentSettings.class
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

  /**
   * Initialize deploy score settings
   * <p>
   *  If build widget settings are present merge it with default score settings
   *  deploy settings for children scores in deploy widget
   */
  private void initDeployScoreSettings() {
    DeployScoreSettings deployScoreSettings = this.scoreSettings.getDeployWidget();
    if (null != deployScoreSettings) {

      deployScoreSettings.setCriteria(
        Utils.mergeCriteria(this.scoreSettings.getCriteria(), deployScoreSettings.getCriteria())
      );
      initDeployScoreChildrenSettings(deployScoreSettings);

    }
  }

  /**
   * Initialize Settings for child components in deploy widget
   * 1. Deploys Success criteria settings
   * 2. Instances Online criteria settings
   * @param deployScoreSettings
   */
  private void initDeployScoreChildrenSettings(DeployScoreSettings deployScoreSettings) {
    ScoreComponentSettings deploySuccessSettings = Utils.getInstanceIfNull(deployScoreSettings.getDeploySuccess(), ScoreComponentSettings.class);
    deploySuccessSettings.setCriteria(
      Utils.mergeCriteria(deployScoreSettings.getCriteria(), deploySuccessSettings.getCriteria())
    );
    deployScoreSettings.setDeploySuccess(deploySuccessSettings);

    ScoreComponentSettings instanceOnlineSettings = Utils.getInstanceIfNull(deployScoreSettings.getIntancesOnline(), ScoreComponentSettings.class);
    instanceOnlineSettings.setCriteria(
      Utils.mergeCriteria(deployScoreSettings.getCriteria(), instanceOnlineSettings.getCriteria())
    );
    deployScoreSettings.setIntancesOnline(instanceOnlineSettings);

  }

  /**
   * Initialize quality score settings
   * <p>
   *  If quality widget settings are present merge it with default score settings
   *  Initialize settings for children scores in quality widget
   */
  private void initQualityScoreSettings() {
    QualityScoreSettings qualityScoreSettings = this.scoreSettings.getQualityWidget();
    if (null != qualityScoreSettings) {
      qualityScoreSettings.setCriteria(
        Utils.mergeCriteria(this.scoreSettings.getCriteria(), qualityScoreSettings.getCriteria())
      );

      initQualityScoreChildrenSettings(qualityScoreSettings);
    }
  }

  /**
   * Initialize Settings for child components in quality widget
   * 1. Code Coverage criteria settings
   * 2. Unit Tests criteria settings
   * 3. Violations criteria settings
   *
   * @param qualityScoreSettings
   */
  private void initQualityScoreChildrenSettings(QualityScoreSettings qualityScoreSettings) {
    ScoreComponentSettings qualityCCSettings = Utils.getInstanceIfNull(qualityScoreSettings.getCodeCoverage(), ScoreComponentSettings.class);
    qualityCCSettings.setCriteria(
      Utils.mergeCriteria(qualityScoreSettings.getCriteria(), qualityCCSettings.getCriteria())
    );
    qualityScoreSettings.setCodeCoverage(qualityCCSettings);

    ScoreComponentSettings qualityUTSettings = Utils.getInstanceIfNull(qualityScoreSettings.getUnitTests(), ScoreComponentSettings.class);
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

  /**
   * Initialize github scm score settings
   * <p>
   *  If github scm widget settings are present merge it with default score settings
   *  Initialize settings for children scores in github scm widget
   */
  private void initGithubScmScoreSettings() {
    ScmScoreSettings scmScoreSettings = this.scoreSettings.getScmWidget();
    if (null != scmScoreSettings) {
      scmScoreSettings.setCriteria(
        Utils.mergeCriteria(this.scoreSettings.getCriteria(), scmScoreSettings.getCriteria())
      );

      initGithubScmScoreChildrenSettings(scmScoreSettings);
    }
  }

  /**
   * Initialize Settings for child components in scm widget
   * 1. Commits Per Day criteria settings
   * @param scmScoreSettings
   */
  private void initGithubScmScoreChildrenSettings(ScmScoreSettings scmScoreSettings) {
    ScoreComponentSettings daysWithCommitsSettings = Utils.getInstanceIfNull(scmScoreSettings.getDaysWithCommits(), ScoreComponentSettings.class);
    daysWithCommitsSettings.setCriteria(
      Utils.mergeCriteria(scmScoreSettings.getCriteria(), daysWithCommitsSettings.getCriteria())
    );
    scmScoreSettings.setDaysWithCommits(daysWithCommitsSettings);
  }
}
