package com.capitalone.dashboard.model.score.settings;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Score Criteria
 * Criteria Conditions
 * - No Widget Found
 * - No Data Found in Widget
 * Threshold Settings for data
 */
public class ScoreCriteria {

  //Score when no widget is found
  private ScoreTypeValue noWidgetFound;

  //Score when no data is found in widget
  private ScoreTypeValue noDataFound;

  //List of threshold conditions
  private List<ScoreThresholdSettings> dataRangeThresholds;

  public static ScoreCriteria cloneScoreCriteria(ScoreCriteria scoreCriteria) {
    if (null == scoreCriteria) {
      return null;
    }

    ScoreCriteria scoreCriteriaClone = new ScoreCriteria();

    scoreCriteriaClone.setNoDataFound(
      ScoreTypeValue.cloneScoreTypeValue(scoreCriteria.getNoDataFound())
    );
    scoreCriteriaClone.setNoWidgetFound(
      ScoreTypeValue.cloneScoreTypeValue(scoreCriteria.getNoWidgetFound())
    );

    List<ScoreThresholdSettings> dataRangeThresholds = scoreCriteria.getDataRangeThresholds();
    if (CollectionUtils.isEmpty(dataRangeThresholds)) {
      return scoreCriteriaClone;
    }

    List<ScoreThresholdSettings> dataRangeThresholdsClone = new ArrayList<>();
    for (ScoreThresholdSettings dataRangeThreshold : dataRangeThresholds) {
      dataRangeThresholdsClone.add(
        ScoreThresholdSettings.cloneScoreThresholdSettings(dataRangeThreshold)
      );
    }
    scoreCriteriaClone.setDataRangeThresholds(dataRangeThresholdsClone);
    return scoreCriteriaClone;
  }

  public List<ScoreThresholdSettings> getDataRangeThresholds() {
    return dataRangeThresholds;
  }

  public void setDataRangeThresholds(List<ScoreThresholdSettings> dataRangeThresholds) {
    this.dataRangeThresholds = dataRangeThresholds;
  }

  public ScoreTypeValue getNoWidgetFound() {
    return noWidgetFound;
  }

  public void setNoWidgetFound(ScoreTypeValue noWidgetFound) {
    this.noWidgetFound = noWidgetFound;
  }

  public ScoreTypeValue getNoDataFound() {
    return noDataFound;
  }

  public void setNoDataFound(ScoreTypeValue noDataFound) {
    this.noDataFound = noDataFound;
  }

  @Override
  public String toString() {
    return "ScoreCriteria{" +
      "noWidgetFound=" + noWidgetFound +
      ", noDataFound=" + noDataFound +
      ", dataRangeThresholds=" + dataRangeThresholds +
      '}';
  }

}
