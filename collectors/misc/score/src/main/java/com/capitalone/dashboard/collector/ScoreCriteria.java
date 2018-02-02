package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.Utils;

public class ScoreCriteria {

  private ScoreTypeValue noWidgetFound;

  private ScoreTypeValue noDataFound;

  private List<ScoreThresholdSettings> dataRangeThresholds;

  public ScoreCriteria() {}

  public static ScoreCriteria cloneScoreCriteria(ScoreCriteria scoreCriteria) {
    if (null == scoreCriteria) {
      return null;
    }

    ScoreCriteria scoreCriteriaClone = new ScoreCriteria();

    scoreCriteriaClone.setNoWidgetFound(
      ScoreTypeValue.cloneScoreTypeValue(scoreCriteria.getNoWidgetFound())
      );
    scoreCriteriaClone.setNoDataFound(
      ScoreTypeValue.cloneScoreTypeValue(scoreCriteria.getNoDataFound())
      );

    scoreCriteriaClone.setDataRangeThresholds(
      Utils.cloneThresholds(scoreCriteria.getDataRangeThresholds())
      );

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
