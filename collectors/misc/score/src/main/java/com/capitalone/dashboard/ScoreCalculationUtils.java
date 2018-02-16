package com.capitalone.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.capitalone.dashboard.collector.ScoreTypeValue;
import com.capitalone.dashboard.collector.WidgetAlert;
import com.capitalone.dashboard.exception.PropagateScoreException;
import com.capitalone.dashboard.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.collector.ScoreType;

public class ScoreCalculationUtils {
  @SuppressWarnings({"unused", "PMD.UnusedPrivateField"})
  private static final Logger LOGGER = LoggerFactory.getLogger(ScoreCalculationUtils.class);

  public static void normalizeWeightForScore(List<ScoreWeight> scoreWeights, PropagateType propagateValue) {
    //Find list of scores that should be considered while normalizing weight. They should be processed
    Pair<List<ScoreWeight>, List<ScoreWeight>> scoreWeightsPair = filterToNormalizeWeights(scoreWeights, propagateValue);

    List<ScoreWeight> scoreWeightsProcessed = scoreWeightsPair.getLeft();
    if (null == scoreWeightsProcessed || scoreWeightsProcessed.isEmpty()) {
      return;
    }

    if (scoreWeightsProcessed.size() == 1) {
      scoreWeightsProcessed.get(0).setWeight(100);
      setWeight(scoreWeightsPair.getRight(), 0);
      return;
    }

    int totalWeight = 0;
    for (ScoreWeight scoreWeight : scoreWeightsProcessed) {
      totalWeight += scoreWeight.getWeight();
    }

    int scoreWeightsSize = scoreWeightsProcessed.size();
    if (totalWeight == 0) {
      int equalWeight = 100 / scoreWeightsSize;
      int sumOfWeights = 0;
      for (int i = 0; i < scoreWeightsSize; i++) {
        if (i < (scoreWeightsSize - 1)) {
          scoreWeightsProcessed.get(i).setWeight(equalWeight);
          sumOfWeights += equalWeight;
        } else {
          scoreWeightsProcessed.get(i).setWeight(100 - sumOfWeights);
        }
      }
    } else {
      double ratioWith100 = 100 / (double) totalWeight;
      int sumOfWeights = 0;
      for (int i = 0; i < scoreWeightsSize; i++) {
        if (i < (scoreWeightsSize - 1)) {
          int weightForWidget = (int) Math.round(scoreWeightsProcessed.get(i).getWeight() * ratioWith100);
          scoreWeightsProcessed.get(i).setWeight(weightForWidget);
          sumOfWeights += weightForWidget;
        } else {
          scoreWeightsProcessed.get(i).setWeight(100 - sumOfWeights);
        }
      }
    }

    setWeight(scoreWeightsPair.getRight(), 0);
  }

  private static void setWeight(List<ScoreWeight> scoreWeights, int weight) {
    if (CollectionUtils.isEmpty(scoreWeights)) {
      return;
    }
    for (ScoreWeight scoreWeight : scoreWeights) {
      scoreWeight.setWeight(weight);
    }
  }

  public static Pair<List<ScoreWeight>, List<ScoreWeight>> filterToNormalizeWeights(List<ScoreWeight> scoreWeights, PropagateType propagateValue) {
    if (null == scoreWeights || scoreWeights.isEmpty()) {
      return Pair.of(scoreWeights, Collections.EMPTY_LIST);
    }

    List<ScoreWeight> filterProcessedWeights = new ArrayList<>();
    List<ScoreWeight> filterNotProcessedWeights = new ArrayList<>();

    //Check for the score to propagate
    //If there is a case where the score is propagated because of criteria met
    //consider score for only propagated ones and fiter remaining
    for (ScoreWeight scoreWeight : scoreWeights) {
      if ((scoreWeight.getState() == ScoreWeight.ProcessingState.criteria_failed ||
        scoreWeight.getState() == ScoreWeight.ProcessingState.criteria_passed) &&
        scoreWeight.getPropagate().getValue() >= propagateValue.getValue()) {
        filterProcessedWeights.add(scoreWeight);
      } else {
        filterNotProcessedWeights.add(scoreWeight);
      }
    }

    if (CollectionUtils.isNotEmpty(filterProcessedWeights)) {
      return Pair.of(filterProcessedWeights, filterNotProcessedWeights);
    }

    //Check for the criterias which have score
    //If there is a case where the score is processed or criteria met
    //consider those and fiter remaining
    filterProcessedWeights = new ArrayList<>();
    filterNotProcessedWeights = new ArrayList<>();
    for (ScoreWeight scoreWeight : scoreWeights) {
      if ((scoreWeight.getState() == ScoreWeight.ProcessingState.complete ||
        scoreWeight.getState() == ScoreWeight.ProcessingState.criteria_failed ||
        scoreWeight.getState() == ScoreWeight.ProcessingState.criteria_passed) &&
        scoreWeight.getScore().getScoreType() != ScoreType.no_score) {
        filterProcessedWeights.add(scoreWeight);
      } else {
        filterNotProcessedWeights.add(scoreWeight);
      }
    }

    return Pair.of(filterProcessedWeights, filterNotProcessedWeights);
  }

  public static Double calculateWidgetScore(List<ScoreWeight> scoreWeights) {
    return calculateWidgetScoreWithMaxLimit(scoreWeights, Constants.MAX_SCORE);
  }

  public static Double calculateWidgetScoreWithMaxLimit(List<ScoreWeight> scoreWeights, int maxScore) {
    if (null == scoreWeights || scoreWeights.isEmpty()) {
      return null;
    }

    double maxScoreRatio = maxScore / (double) 100;
    double score = Constants.ZERO_SCORE;
    for (ScoreWeight scoreWeight : scoreWeights) {
      score += (scoreWeight.getScore().getScoreValue() * scoreWeight.getWeight() * maxScoreRatio) / ((double) scoreWeight.getTotal());
    }

    return score;
  }

  public static Double convertBaseMaxScore(Double score, int currentMaxScore, int newMaxScore) {
    if (null == score) {
      return Constants.ZERO_SCORE;
    }
    return (score * (double) newMaxScore) / (double) currentMaxScore;
  }


  public static ScoreMetric generateScoreMetric(ScoreWeight dashboardScore, int maxScore, ObjectId collectorItemId, ObjectId dashboardId) {
    ScoreMetric scoreMetric = new ScoreMetric();
    scoreMetric.setTotal(String.valueOf(maxScore));
    scoreMetric.setCollectorItemId(collectorItemId);
    scoreMetric.setType(ScoreValueType.DASHBOARD);
    scoreMetric.setScoreTypeId(dashboardId);
    scoreMetric.setScore(
      Utils.roundAlloc(
        convertBaseMaxScore(
          dashboardScore.getScore().getScoreValue(),
          dashboardScore.getTotal(),
          maxScore
        )
        )
      );
    scoreMetric.setNoScore(dashboardScore.getScore().isNoScore());

    List<ScoreWidgetMetric> scoreWidgetMetrics = new ArrayList<>();
    updateWidgetMetrics(dashboardScore.getChildren(), scoreWidgetMetrics, maxScore);
    scoreMetric.setScoreWidgetMetrics(scoreWidgetMetrics);

    return scoreMetric;
  }


  public static void updateWidgetMetrics(List<ScoreWeight> childrenScore, List<ScoreWidgetMetric> scoreWidgetMetrics, int maxScore) {
    if (CollectionUtils.isEmpty(childrenScore)) {
      return;
    }

    for (ScoreWeight childScore : childrenScore) {
      ScoreWidgetMetric scoreWidgetMetric = new ScoreWidgetMetric();

      updateWidgetScore(scoreWidgetMetric, childScore, maxScore);
      scoreWidgetMetric.setAlert(childScore.isAlert());

      List<ScoreWeight> childrenOfChild = childScore.getChildren();
      if (CollectionUtils.isNotEmpty(childrenOfChild)) {
        List<ScoreWidgetMetricBase> childScoreWidgetMetrics = new ArrayList<>();
        scoreWidgetMetric.setChildren(childScoreWidgetMetrics);
        updateWidgetChildrenMetrics(childrenOfChild, childScoreWidgetMetrics, maxScore);
      }

      scoreWidgetMetrics.add(scoreWidgetMetric);
    }
  }


  public static void updateWidgetChildrenMetrics(List<ScoreWeight> childrenScore, List<ScoreWidgetMetricBase> scoreWidgetMetrics, int maxScore) {
    if (CollectionUtils.isEmpty(childrenScore)) {
      return;
    }

    for (ScoreWeight childScore : childrenScore) {
      ScoreWidgetMetricBase scoreWidgetMetric = new ScoreWidgetMetricBase();
      updateWidgetScore(scoreWidgetMetric, childScore, maxScore);
      scoreWidgetMetrics.add(scoreWidgetMetric);
    }
  }


  private static void updateWidgetScore(ScoreWidgetMetricBase scoreWidgetMetric, ScoreWeight childScore, int maxScore) {
    scoreWidgetMetric.setTotal(String.valueOf(maxScore));
    scoreWidgetMetric.setScore(
      Utils.roundAlloc(
        convertBaseMaxScore(
          childScore.getScore().getScoreValue(),
          childScore.getTotal(),
          maxScore
        )
      )
    );
    scoreWidgetMetric.setNoScore(childScore.getScore().isNoScore());
    scoreWidgetMetric.setWeight(String.valueOf(childScore.getWeight()));
    scoreWidgetMetric.setId(childScore.getId());
    scoreWidgetMetric.setName(childScore.getName());
    scoreWidgetMetric.setMessage(childScore.getMessage());
    scoreWidgetMetric.setState(childScore.getState().name());
    scoreWidgetMetric.setPropagate(childScore.getPropagate().name());
  }

  public static ScoreTypeValue calculateWidgetScoreTypeValue(List<ScoreWeight> widgetScores, PropagateType propagateType)
  throws PropagateScoreException {
    ScoreCalculationUtils.normalizeWeightForScore(widgetScores, propagateType);
    ScoreWeight propagatedScore = propagatedScore(widgetScores, propagateType);
    if (null != propagatedScore) {
      throw new PropagateScoreException(propagatedScore.getName() + " - " + propagatedScore.getMessage(), propagatedScore.getScore(), propagatedScore.getState());
    }

    Double widgetScore = ScoreCalculationUtils.calculateWidgetScore(widgetScores);
    return new ScoreTypeValue(widgetScore);
  }

  public static ScoreWeight propagatedScore(List<ScoreWeight> scoreWidgets, PropagateType propagateType) {

    for (ScoreWeight scoreWidget : scoreWidgets) {
      if (scoreWidget.getPropagate().getValue() >= propagateType.getValue()) {
        return scoreWidget;
      }
    }

    return null;
  }

  public static boolean isWidgetAlert(WidgetAlert alert, Double compareValue) {
    int compareResult = compareValue.compareTo(alert.getValue());
    switch (alert.getComparator()) {
      case equals:
        if (compareResult == 0) {
          return true;
        }
        return false;
      case less:
        if (compareResult == -1) {
          return true;
        }
        return false;
      case greater:
        if (compareResult == 1) {
          return true;
        }
        return false;
      case less_or_equal:
        if (compareResult == 0 || compareResult == -1) {
          return true;
        }
        return false;
      case greater_or_equal:
        if (compareResult == 0 || compareResult == 1) {
          return true;
        }
        return false;
      default:
        return false;
    }

  }

}
