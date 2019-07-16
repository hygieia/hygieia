package com.capitalone.dashboard.widget;

import java.util.*;

import com.capitalone.dashboard.exception.ThresholdException;
import com.capitalone.dashboard.model.score.settings.DeployScoreSettings;
import com.capitalone.dashboard.model.score.settings.ScoreComponentSettings;
import com.capitalone.dashboard.model.score.settings.ScoreTypeValue;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.Constants;
import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.exception.DataNotFoundException;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import com.google.common.collect.Lists;


/**
 * Service to calculate deploy widget score
 * Deploy scores are based on
 * 1. Percentage of successfully deployed components
 * 2. Percentage of instances online
 */
@Service
public class DeployWidgetScore extends WidgetScoreAbstract {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeployWidgetScore.class);

  protected final static String WIDGET_DEPLOY_SUCCESS = "deploySuccess";
  protected final static String WIDGET_DEPLOY_SUCCESS_NAME = "Deploy Success";
  protected final static String WIDGET_DEPLOY_INSTANCES_ONLINE = "deployInstancesOnline";
  protected final static String WIDGET_DEPLOY_INSTANCES_ONLINE_NAME = "Deploy Instances Online";

  public final static IdName WIDGET_ID_NAME = new IdName(
    Constants.WIDGET_DEPLOY,
    Constants.WIDGET_DEPLOY_NAME
    );

  public final static IdName WIDGET_DEPLOY_SUCCESS_ID_NAME = new IdName(
    WIDGET_DEPLOY_SUCCESS,
    WIDGET_DEPLOY_SUCCESS_NAME
    );

  public final static IdName WIDGET_DEPLOY_INSTANCES_ONLINE_ID_NAME = new IdName(
    WIDGET_DEPLOY_INSTANCES_ONLINE,
    WIDGET_DEPLOY_INSTANCES_ONLINE_NAME
    );

  private final EnvironmentComponentRepository environmentComponentRepository;
  private final EnvironmentStatusRepository environmentStatusRepository;
  private final ComponentRepository componentRepository;

  //Categories are various factors which contribute to the overall score of the widget
  public final List<IdName> categories;

  @Autowired
  public DeployWidgetScore(EnvironmentComponentRepository environmentComponentRepository,
    EnvironmentStatusRepository environmentStatusRepository,
    ComponentRepository componentRepository) {
    this.environmentComponentRepository = environmentComponentRepository;
    this.environmentStatusRepository = environmentStatusRepository;
    this.componentRepository = componentRepository;
    this.categories = Lists.newArrayList(
      WIDGET_DEPLOY_SUCCESS_ID_NAME,
      WIDGET_DEPLOY_INSTANCES_ONLINE_ID_NAME
      );
  }

  @Override
  protected IdName getWidgetIdName() {
    return WIDGET_ID_NAME;
  }

  @Override
  protected List<IdName> getCategories() {
    return this.categories;
  }

  @Override
  protected void calculateCategoryScores(Widget deployWidget, ScoreComponentSettings paramSettings, List<ScoreWeight> categoryScores)
    throws DataNotFoundException, ThresholdException {
    if (CollectionUtils.isEmpty(categoryScores)) {
      return;
    }

    DeployScoreSettings deployScoreSettings = (DeployScoreSettings) paramSettings;
    ScoreComponentSettings deploySuccessSettings = Utils.getInstanceIfNull(deployScoreSettings.getDeploySuccess(), ScoreComponentSettings.class);
    ScoreComponentSettings instanceOnlineSettings = Utils.getInstanceIfNull(deployScoreSettings.getIntancesOnline(), ScoreComponentSettings.class);

    setCategoryScoreWeight(categoryScores, WIDGET_DEPLOY_SUCCESS_ID_NAME, deploySuccessSettings.getWeight());
    setCategoryScoreWeight(categoryScores, WIDGET_DEPLOY_INSTANCES_ONLINE_ID_NAME, instanceOnlineSettings.getWeight());

    boolean isDeploySuccessScoreEnabled = Utils.isScoreEnabled(deploySuccessSettings);
    boolean isInstancesOnlineScoreEnabled = Utils.isScoreEnabled(instanceOnlineSettings);

    Component component = null;

    if (isDeploySuccessScoreEnabled || isInstancesOnlineScoreEnabled) {
      component = componentRepository.findOne(deployWidget.getComponentId());
    }

    if (null == component) {
      throw new DataNotFoundException(Constants.SCORE_ERROR_NO_DATA_FOUND);
    }

    CollectorItem item = getDeployCollectorItem(component);

    ObjectId collectorItemId = item.getId();

    if (isDeploySuccessScoreEnabled) {
      processDeploySuccessScore(
        collectorItemId,
        deploySuccessSettings,
        categoryScores
      );
    }

    if (isInstancesOnlineScoreEnabled) {
      processDeployInstancesOnlineScore(
        collectorItemId,
        instanceOnlineSettings,
        categoryScores
      );
    }

  }

  private CollectorItem getDeployCollectorItem(Component component) throws DataNotFoundException {
    List<CollectorItem> deployItems = component.getCollectorItems()
      .get(CollectorType.Deployment);

    if (CollectionUtils.isEmpty(deployItems)) {
      throw new DataNotFoundException(Constants.SCORE_ERROR_NO_DATA_FOUND);
    }

    return deployItems.get(0);
  }

  private void processDeploySuccessScore(
    ObjectId collectorItemId,
    ScoreComponentSettings deploySuccessSettings,
    List<ScoreWeight> categoryScores) {
    ScoreWeight deploySuccessStatusScore = getCategoryScoreByIdName(categoryScores, WIDGET_DEPLOY_SUCCESS_ID_NAME);
    Double deploySuccessRatio = fetchDeploySuccessRatio(collectorItemId);
    if (null == deploySuccessRatio) {
      deploySuccessStatusScore.setScore(
        deploySuccessSettings.getCriteria().getNoDataFound()
      );
      deploySuccessStatusScore.setMessage(Constants.SCORE_ERROR_NO_DATA_FOUND);
      deploySuccessStatusScore.setState(ScoreWeight.ProcessingState.criteria_failed);
    } else {
      deploySuccessStatusScore.setScore(
        new ScoreTypeValue(deploySuccessRatio)
      );
      deploySuccessStatusScore.setMessage("% deploys succeeded overall");
      deploySuccessStatusScore.setState(ScoreWeight.ProcessingState.complete);
    }
  }

  private void processDeployInstancesOnlineScore(
    ObjectId collectorItemId,
    ScoreComponentSettings instanceOnlineSettings,
    List<ScoreWeight> categoryScores) {
    ScoreWeight deployInstancesOnlineScore = getCategoryScoreByIdName(categoryScores, WIDGET_DEPLOY_INSTANCES_ONLINE_ID_NAME);
    Double instancesOnlineRatio = fetchInstancesOnlineRatio(collectorItemId);
    if (null == instancesOnlineRatio) {
      deployInstancesOnlineScore.setScore(
        instanceOnlineSettings.getCriteria().getNoDataFound()
      );
      deployInstancesOnlineScore.setMessage(Constants.SCORE_ERROR_NO_DATA_FOUND);
      deployInstancesOnlineScore.setState(ScoreWeight.ProcessingState.criteria_failed);
    } else {
      deployInstancesOnlineScore.setScore(
        new ScoreTypeValue(instancesOnlineRatio)
      );
      deployInstancesOnlineScore.setMessage("% instances online");
      deployInstancesOnlineScore.setState(ScoreWeight.ProcessingState.complete);
    }
  }

  /**
   * Calculate percentage of successfully deployed components
   *
   * @param collectorItemId Collector Item Id of deploy
   * @return percentage of deployed components
   */
  private Double fetchDeploySuccessRatio(ObjectId collectorItemId) {
    int totalDeploys = 0, totalDeploySuccess = 0;
    Double deploySuccessScore = null;

    List<EnvironmentComponent> components = environmentComponentRepository
      .findByCollectorItemId(collectorItemId);

    if (null == components || components.isEmpty()) {
      return null;
    }

    for (EnvironmentComponent environmentComponent : components) {
      totalDeploys++;
      if (environmentComponent.isDeployed()) {
        totalDeploySuccess++;
      }
    }
    deploySuccessScore = ((totalDeploySuccess * 100) / (double) totalDeploys);
    LOGGER.info("totalDeploys " + totalDeploys + " totalDeploySuccess " + totalDeploySuccess + " deploySuccessScore " + deploySuccessScore);

    return deploySuccessScore;
  }

  /**
   * Calculate percentage of instances online
   *
   * @param collectorItemId Collector Item Id of deploy
   * @return percentage of instances online
   */
  private Double fetchInstancesOnlineRatio(ObjectId collectorItemId) {
    int totalInstances = 0, totalInstancesOnline = 0;
    Double instancesOnlineScore = null;

    List<EnvironmentStatus> statuses = environmentStatusRepository
      .findByCollectorItemId(collectorItemId);

    if (null == statuses || statuses.isEmpty()) {
      return null;
    }

    for (EnvironmentStatus environmentStatus : statuses) {
      totalInstances++;
      if (environmentStatus.isOnline()) {
        totalInstancesOnline++;
      }
    }
    instancesOnlineScore = ((totalInstancesOnline * 100) / (double) totalInstances);
    LOGGER.info("totalInstances " + totalInstances + " totalInstancesOnline " + totalInstancesOnline + " instancesOnlineScore " + instancesOnlineScore);

    return instancesOnlineScore;
  }

}
