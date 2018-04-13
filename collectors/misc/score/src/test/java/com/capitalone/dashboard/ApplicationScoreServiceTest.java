package com.capitalone.dashboard;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.score.ScoreCollectorItem;
import com.capitalone.dashboard.model.score.ScoreComponentMetric;
import com.capitalone.dashboard.model.score.ScoreMetric;
import com.capitalone.dashboard.model.score.settings.*;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.widget.BuildWidgetScore;
import com.capitalone.dashboard.widget.DeployWidgetScore;
import com.capitalone.dashboard.widget.GithubScmWidgetScore;
import com.capitalone.dashboard.widget.QualityWidgetScore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationScoreServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationScoreServiceTest.class);

  @Mock
  private DashboardRepository dashboardRepository;

  @Mock
  private BuildWidgetScore buildWidgetScore;
  @Mock
  private QualityWidgetScore qualityWidgetScore;
  @Mock
  private DeployWidgetScore deployWidgetScore;
  @Mock
  private GithubScmWidgetScore githubScmWidgetScore;

  @InjectMocks
  private ApplicationScoreService applicationScoreService;


  private BuildScoreSettings getBuildScoreSettingsNoThreshold() {
    BuildScoreSettings buildScoreSettings = new BuildScoreSettings();
    buildScoreSettings.setNumberOfDays(14);
    buildScoreSettings.setWeight(25);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());
    buildScoreSettings.setCriteria(criteria);

    return buildScoreSettings;
  }

  private DeployScoreSettings getDeployScoreSettingsNoThreshold() {
    DeployScoreSettings deployScoreSettings = new DeployScoreSettings();
    deployScoreSettings.setWeight(25);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());
    deployScoreSettings.setCriteria(criteria);

    return deployScoreSettings;
  }

  private QualityScoreSettings getQualityScoreSettingsNoThreshold() {
    QualityScoreSettings qualityScoreSettings = new QualityScoreSettings();
    qualityScoreSettings.setWeight(25);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());
    qualityScoreSettings.setCriteria(criteria);

    return qualityScoreSettings;
  }

  private ScmScoreSettings getGithubScmScoreSettingsNoThreshold() {
    ScmScoreSettings scmScoreSettings = new ScmScoreSettings();
    scmScoreSettings.setWeight(25);

    ScoreCriteria criteria = new ScoreCriteria();
    criteria.setNoWidgetFound(ScoreTypeValue.zeroScore());
    criteria.setNoDataFound(ScoreTypeValue.zeroScore());
    scmScoreSettings.setCriteria(criteria);

    return scmScoreSettings;
  }



  @Test
  public void calculateScore() throws IOException {
    CalculateScoreBuilder calculateScoreBuilder = new CalculateScoreBuilder()
      .buildScoreSettings(getBuildScoreSettingsNoThreshold())
      .deployScoreSettings(getDeployScoreSettingsNoThreshold())
      .qualityScoreSettings(getQualityScoreSettingsNoThreshold())
      .githubScmScoreSettings(getGithubScmScoreSettingsNoThreshold())
      .buildWidgetScore(getBuildWidgetScore(new ScoreTypeValue(60.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .deployWidgetScore(getDeployWidgetScore(new ScoreTypeValue(100.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .qualityWidgetScore(getQualityWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .githubScmWidgetScore(getGithubScmWidgetScore(new ScoreTypeValue(70.0d), ScoreWeight.ProcessingState.complete, PropagateType.no));

    ScoreMetric scoreDashboard = calculateScore(calculateScoreBuilder);
    Collection<ScoreComponentMetric> scoreWidgetMetrics = scoreDashboard.getComponentMetrics();
    assertThat(scoreDashboard.getScore(), is("3.5"));
    assertThat(scoreDashboard.isNoScore(), is(false));
    assertThat(scoreWidgetMetrics, hasSize(4));

    ScoreComponentMetric scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_BUILD);
    assertThat(scoreWidgetMetric.getScore(), is("3"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_DEPLOY);
    assertThat(scoreWidgetMetric.getScore(), is("5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_CODE_ANALYSIS);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_GITHUB_SCM);
    assertThat(scoreWidgetMetric.getScore(), is("3.5"));

    LOGGER.info("scoreDashboard {}", scoreDashboard);
  }

  @Test
  public void calculateScoreWithCriteriaFail() throws IOException {

    ScoreWeight buildScoreWeight = getBuildWidgetScore(
      ScoreTypeValue.zeroScore(),
      ScoreWeight.ProcessingState.criteria_failed,
      PropagateType.widget
    );
    buildScoreWeight.setMessage("Failed to meet criteria");

    CalculateScoreBuilder calculateScoreBuilder = new CalculateScoreBuilder()
      .buildScoreSettings(getBuildScoreSettingsNoThreshold())
      .deployScoreSettings(getDeployScoreSettingsNoThreshold())
      .qualityScoreSettings(getQualityScoreSettingsNoThreshold())
      .githubScmScoreSettings(getGithubScmScoreSettingsNoThreshold())
      .buildWidgetScore(buildScoreWeight)
      .deployWidgetScore(getDeployWidgetScore(new ScoreTypeValue(100.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .qualityWidgetScore(getQualityWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .githubScmWidgetScore(getGithubScmWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no));

    ScoreMetric scoreDashboard = calculateScore(calculateScoreBuilder);
    Collection<ScoreComponentMetric> scoreWidgetMetrics = scoreDashboard.getComponentMetrics();
    assertThat(scoreDashboard.getScore(), is("2.5"));
    assertThat(scoreDashboard.isNoScore(), is(false));
    assertThat(scoreWidgetMetrics, hasSize(4));

    ScoreComponentMetric scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_BUILD);
    assertThat(scoreWidgetMetric.getScore(), is("0"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_DEPLOY);
    assertThat(scoreWidgetMetric.getScore(), is("5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_CODE_ANALYSIS);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_GITHUB_SCM);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));

    LOGGER.info("scoreDashboard {}", scoreDashboard);
  }

  @Test
  public void calculateScorePropagateNoScore() throws IOException {
    CalculateScoreBuilder calculateScoreBuilder = new CalculateScoreBuilder()
      .buildScoreSettings(getBuildScoreSettingsNoThreshold())
      .deployScoreSettings(getDeployScoreSettingsNoThreshold())
      .qualityScoreSettings(getQualityScoreSettingsNoThreshold())
      .githubScmScoreSettings(getGithubScmScoreSettingsNoThreshold())
      .buildWidgetScore(getBuildWidgetScore(ScoreTypeValue.noScore(), ScoreWeight.ProcessingState.complete, PropagateType.dashboard))
      .deployWidgetScore(getDeployWidgetScore(new ScoreTypeValue(100.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .qualityWidgetScore(getQualityWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .githubScmWidgetScore(getGithubScmWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no));

    ScoreMetric scoreDashboard = calculateScore(calculateScoreBuilder);
    Collection<ScoreComponentMetric> scoreWidgetMetrics = scoreDashboard.getComponentMetrics();
    assertThat(scoreDashboard.getScore(), is("0"));
    assertThat(scoreDashboard.isNoScore(), is(true));
    assertThat(scoreWidgetMetrics, hasSize(4));

    ScoreComponentMetric scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_BUILD);
    assertThat(scoreWidgetMetric.getScore(), is("0"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_DEPLOY);
    assertThat(scoreWidgetMetric.getScore(), is("5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_CODE_ANALYSIS);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_GITHUB_SCM);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));

    LOGGER.info("scoreDashboard {}", scoreDashboard);
  }

  @Test
  public void calculateScorePropagateScore() throws IOException {
    CalculateScoreBuilder calculateScoreBuilder = new CalculateScoreBuilder()
      .buildScoreSettings(getBuildScoreSettingsNoThreshold())
      .deployScoreSettings(getDeployScoreSettingsNoThreshold())
      .qualityScoreSettings(getQualityScoreSettingsNoThreshold())
      .githubScmScoreSettings(getGithubScmScoreSettingsNoThreshold())
      .buildWidgetScore(getBuildWidgetScore(new ScoreTypeValue(20.0d), ScoreWeight.ProcessingState.complete, PropagateType.dashboard))
      .deployWidgetScore(getDeployWidgetScore(new ScoreTypeValue(100.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .qualityWidgetScore(getQualityWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .githubScmWidgetScore(getGithubScmWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no));

    ScoreMetric scoreDashboard = calculateScore(calculateScoreBuilder);
    Collection<ScoreComponentMetric> scoreWidgetMetrics = scoreDashboard.getComponentMetrics();
    assertThat(scoreDashboard.getScore(), is("1"));
    assertThat(scoreDashboard.isNoScore(), is(false));
    assertThat(scoreWidgetMetrics, hasSize(4));

    ScoreComponentMetric scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_BUILD);
    assertThat(scoreWidgetMetric.getScore(), is("1"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_DEPLOY);
    assertThat(scoreWidgetMetric.getScore(), is("5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_CODE_ANALYSIS);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_GITHUB_SCM);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));


    LOGGER.info("scoreDashboard {}", scoreDashboard);
  }

  @Test
  public void calculateScorePropagateZeroScore() throws IOException {
    CalculateScoreBuilder calculateScoreBuilder = new CalculateScoreBuilder()
      .buildScoreSettings(getBuildScoreSettingsNoThreshold())
      .deployScoreSettings(getDeployScoreSettingsNoThreshold())
      .qualityScoreSettings(getQualityScoreSettingsNoThreshold())
      .githubScmScoreSettings(getGithubScmScoreSettingsNoThreshold())
      .buildWidgetScore(getBuildWidgetScore(ScoreTypeValue.zeroScore(), ScoreWeight.ProcessingState.complete, PropagateType.dashboard))
      .deployWidgetScore(getDeployWidgetScore(new ScoreTypeValue(100.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .qualityWidgetScore(getQualityWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no))
      .githubScmWidgetScore(getGithubScmWidgetScore(new ScoreTypeValue(50.0d), ScoreWeight.ProcessingState.complete, PropagateType.no));

    ScoreMetric scoreDashboard = calculateScore(calculateScoreBuilder);
    Collection<ScoreComponentMetric> scoreWidgetMetrics = scoreDashboard.getComponentMetrics();
    assertThat(scoreDashboard.getScore(), is("0"));
    assertThat(scoreDashboard.isNoScore(), is(false));
    assertThat(scoreWidgetMetrics, hasSize(4));

    ScoreComponentMetric scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_BUILD);
    assertThat(scoreWidgetMetric.getScore(), is("0"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_DEPLOY);
    assertThat(scoreWidgetMetric.getScore(), is("5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_CODE_ANALYSIS);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));
    scoreWidgetMetric = getScoreWidgetMetricById(scoreWidgetMetrics, Constants.WIDGET_GITHUB_SCM);
    assertThat(scoreWidgetMetric.getScore(), is("2.5"));

    LOGGER.info("scoreDashboard {}", scoreDashboard);
  }


  private ScoreComponentMetric getScoreWidgetMetricById(Collection<ScoreComponentMetric> scoreWidgetMetrics, String id) {
    for (ScoreComponentMetric scoreWidgetMetric : scoreWidgetMetrics) {
      if (scoreWidgetMetric.getDisplayId().equals(id)) {
        return scoreWidgetMetric;
      }
    }
    return null;
  }




  public ScoreMetric calculateScore(CalculateScoreBuilder calculateScoreBuilder) throws IOException {
    ScoreCollectorItem scoreApplication = new ScoreCollectorItem();
    scoreApplication.setDashboardId(new ObjectId("596c43483d88f70bf778caee"));
    scoreApplication.setId(new ObjectId("596c43483d88f70bf778caef"));

    ObjectMapper mapper = new ObjectMapper();
    byte[] content = Resources.asByteSource(Resources.getResource("dashboard-data.json")).read();
    Dashboard dashboard = mapper.readValue(content, Dashboard.class);

    when(dashboardRepository.findOne(scoreApplication.getDashboardId())).thenReturn(dashboard);

    when(buildWidgetScore.processWidgetScore(any(), any())).thenReturn(calculateScoreBuilder.getBuildWidgetScore());
    when(qualityWidgetScore.processWidgetScore(any(), any())).thenReturn(calculateScoreBuilder.getQualityWidgetScore());
    when(deployWidgetScore.processWidgetScore(any(), any())).thenReturn(calculateScoreBuilder.getDeployWidgetScore());
    when(githubScmWidgetScore.processWidgetScore(any(), any())).thenReturn(calculateScoreBuilder.getGithubScmWidgetScore());


    ScoreMetric scoreDashboard = applicationScoreService.getScoreForApplication(scoreApplication, getScoreCriteriaSettings(calculateScoreBuilder));
    return scoreDashboard;
  }


  private ScoreCriteriaSettings getScoreCriteriaSettings(CalculateScoreBuilder calculateScoreBuilder) {
    ScoreCriteriaSettings scoreCriteriaSettings = new ScoreCriteriaSettings();
    scoreCriteriaSettings.setMaxScore(5);
    scoreCriteriaSettings.setBuild(
      calculateScoreBuilder.getBuildScoreSettings()
    );
    scoreCriteriaSettings.setDeploy(
      calculateScoreBuilder.getDeployScoreSettings()
    );
    scoreCriteriaSettings.setQuality(
      calculateScoreBuilder.getQualityScoreSettings()
    );
    scoreCriteriaSettings.setScm(
      calculateScoreBuilder.getScmScoreSettings()
    );

    return scoreCriteriaSettings;
  }


  private ScoreWeight getBuildWidgetScore(
    ScoreTypeValue scoreTypeValue,
    ScoreWeight.ProcessingState processingState,
    PropagateType propagateType
  ) {
    ScoreWeight scoreWeight = new ScoreWeight(
      Constants.WIDGET_BUILD,
      Constants.WIDGET_BUILD_NAME
    );

    scoreWeight.setScore(scoreTypeValue);
    scoreWeight.setState(processingState);
    scoreWeight.setWeight(25);
    scoreWeight.getScore().setPropagate(propagateType);
    ScoreWeight scoreStatusWeight = new ScoreWeight(
      BuildWidgetScore.WIDGET_BUILD_STATUS_ID_NAME.getId(),
      BuildWidgetScore.WIDGET_BUILD_STATUS_ID_NAME.getName()
    );

    scoreStatusWeight.setScore(new ScoreTypeValue(50.0d));
    scoreStatusWeight.setState(ScoreWeight.ProcessingState.complete);
    scoreStatusWeight.setWeight(50);

    ScoreWeight scoreDurationWeight = new ScoreWeight(
      BuildWidgetScore.WIDGET_BUILD_DURATION_ID_NAME.getId(),
      BuildWidgetScore.WIDGET_BUILD_DURATION_ID_NAME.getName()
    );

    scoreDurationWeight.setScore(new ScoreTypeValue(60.0d));
    scoreDurationWeight.setState(ScoreWeight.ProcessingState.complete);
    scoreDurationWeight.setWeight(50);
    scoreWeight.setChildren(Lists.newArrayList(scoreStatusWeight, scoreDurationWeight));
    return scoreWeight;
  }

  private ScoreWeight getQualityWidgetScore(
    ScoreTypeValue scoreTypeValue,
    ScoreWeight.ProcessingState processingState,
    PropagateType propagateType

  ) {
    ScoreWeight scoreWeight = new ScoreWeight(
      Constants.WIDGET_CODE_ANALYSIS,
      Constants.WIDGET_CODE_ANALYSIS_NAME
    );

    scoreWeight.setScore(scoreTypeValue);
    scoreWeight.setState(processingState);
    scoreWeight.setWeight(25);
    scoreWeight.getScore().setPropagate(propagateType);
    return scoreWeight;
  }

  private ScoreWeight getDeployWidgetScore(
    ScoreTypeValue scoreTypeValue,
    ScoreWeight.ProcessingState processingState,
    PropagateType propagateType
  ) {
    ScoreWeight scoreWeight = new ScoreWeight(
      Constants.WIDGET_DEPLOY,
      Constants.WIDGET_DEPLOY_NAME
    );

    scoreWeight.setScore(scoreTypeValue);
    scoreWeight.setState(processingState);
    scoreWeight.setWeight(25);
    scoreWeight.getScore().setPropagate(propagateType);
    return scoreWeight;
  }

  private ScoreWeight getGithubScmWidgetScore(
    ScoreTypeValue scoreTypeValue,
    ScoreWeight.ProcessingState processingState,
    PropagateType propagateType

  ) {
    ScoreWeight scoreWeight = new ScoreWeight(
      Constants.WIDGET_GITHUB_SCM,
      Constants.WIDGET_GITHUB_SCM_NAME
    );

    scoreWeight.setScore(scoreTypeValue);
    scoreWeight.setState(processingState);
    scoreWeight.setWeight(25);
    scoreWeight.getScore().setPropagate(propagateType);
    return scoreWeight;
  }


  static class CalculateScoreBuilder {

    private BuildScoreSettings buildScoreSettings;

    private DeployScoreSettings deployScoreSettings;

    private QualityScoreSettings qualityScoreSettings;

    private ScmScoreSettings scmScoreSettings;

    private ScoreWeight buildWidgetScore;

    private ScoreWeight deployWidgetScore;

    private ScoreWeight qualityWidgetScore;

    private ScoreWeight githubScmWidgetScore;


    public CalculateScoreBuilder buildScoreSettings(BuildScoreSettings buildScoreSettings) {
      this.buildScoreSettings = buildScoreSettings;
      return this;
    }

    public CalculateScoreBuilder deployScoreSettings(DeployScoreSettings deployScoreSettings) {
      this.deployScoreSettings = deployScoreSettings;
      return this;
    }

    public CalculateScoreBuilder qualityScoreSettings(QualityScoreSettings qualityScoreSettings) {
      this.qualityScoreSettings = qualityScoreSettings;
      return this;
    }

    public CalculateScoreBuilder githubScmScoreSettings(ScmScoreSettings scmScoreSettings) {
      this.scmScoreSettings = scmScoreSettings;
      return this;
    }

    public CalculateScoreBuilder buildWidgetScore(ScoreWeight buildWidgetScore) {
      this.buildWidgetScore = buildWidgetScore;
      return this;
    }

    public CalculateScoreBuilder deployWidgetScore(ScoreWeight deployWidgetScore) {
      this.deployWidgetScore = deployWidgetScore;
      return this;
    }

    public CalculateScoreBuilder qualityWidgetScore(ScoreWeight qualityWidgetScore) {
      this.qualityWidgetScore = qualityWidgetScore;
      return this;
    }

    public CalculateScoreBuilder githubScmWidgetScore(ScoreWeight githubScmWidgetScore) {
      this.githubScmWidgetScore = githubScmWidgetScore;
      return this;
    }

    public BuildScoreSettings getBuildScoreSettings() {
      return buildScoreSettings;
    }

    public DeployScoreSettings getDeployScoreSettings() {
      return deployScoreSettings;
    }

    public QualityScoreSettings getQualityScoreSettings() {
      return qualityScoreSettings;
    }

    public ScoreWeight getBuildWidgetScore() {
      return buildWidgetScore;
    }

    public ScoreWeight getDeployWidgetScore() {
      return deployWidgetScore;
    }

    public ScoreWeight getQualityWidgetScore() {
      return qualityWidgetScore;
    }

    public ScmScoreSettings getScmScoreSettings() {
      return scmScoreSettings;
    }

    public ScoreWeight getGithubScmWidgetScore() {
      return githubScmWidgetScore;
    }
  }
}
