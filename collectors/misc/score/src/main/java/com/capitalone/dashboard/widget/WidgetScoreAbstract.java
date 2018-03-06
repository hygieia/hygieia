package com.capitalone.dashboard.widget;

import java.util.ArrayList;
import java.util.List;

import com.capitalone.dashboard.ThresholdUtils;
import com.capitalone.dashboard.model.score.settings.ScoreComponentSettings;
import com.capitalone.dashboard.model.score.settings.ScoreCriteria;
import com.capitalone.dashboard.model.score.settings.ScoreThresholdSettings;
import com.capitalone.dashboard.exception.PropagateScoreException;
import com.capitalone.dashboard.model.score.settings.PropagateType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.Constants;
import com.capitalone.dashboard.ScoreCalculationUtils;
import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.exception.DataNotFoundException;
import com.capitalone.dashboard.exception.ThresholdException;
import com.capitalone.dashboard.model.IdName;
import com.capitalone.dashboard.model.ScoreWeight;
import com.capitalone.dashboard.model.Widget;

/**
 * Widget Score calculation base class
 * This should be extended for writing score calculation implementations for widgets
 */
public abstract class WidgetScoreAbstract implements WidgetScore {
  private static final Logger LOGGER = LoggerFactory.getLogger(WidgetScoreAbstract.class);

  /**
   * Process widget score
   *
   * @param widget widget configuration from dashboard
   * @param paramSettings Score Settings for the widget
   * @return
   */
  @Override
  public ScoreWeight processWidgetScore(Widget widget, ScoreComponentSettings paramSettings) {

    if (!Utils.isScoreEnabled(paramSettings)) {
      return null;
    }

    //1. Init scores
    ScoreWeight scoreWidget = initWidgetScore(paramSettings);

    if (null == widget) {
      scoreWidget.setScore(paramSettings.getCriteria().getNoWidgetFound());
      scoreWidget.setState(ScoreWeight.ProcessingState.criteria_failed);
      scoreWidget.setMessage(Constants.SCORE_ERROR_NO_WIDGET_FOUND);
      processWidgetScoreChildren(scoreWidget);
      return scoreWidget;
    }

    //Set Reference Id as Widget Id
    scoreWidget.setRefId(widget.getId());

    //2. Calculate scores for each child category
    try {
      calculateCategoryScores(widget, paramSettings, scoreWidget.getChildren());
    } catch (DataNotFoundException ex) {
      scoreWidget.setState(ScoreWeight.ProcessingState.criteria_failed);
      scoreWidget.setScore(paramSettings.getCriteria().getNoDataFound());
      scoreWidget.setMessage(ex.getMessage());
      processWidgetScoreChildren(scoreWidget);
      return scoreWidget;
    } catch (ThresholdException ex) {
      setThresholdFailureWeight(ex, scoreWidget);
      processWidgetScoreChildren(scoreWidget);
      return scoreWidget;
    }

    LOGGER.debug("scoreWidget {}", scoreWidget);
    LOGGER.debug("scoreWidget.getChildren {}", scoreWidget.getChildren());
    //3. Calculate widget score
    calculateWidgetScore(scoreWidget);
    return scoreWidget;
  }

  //Categories are various factors which contribute to the overall score of the widget
  abstract List<IdName> getCategories();

  abstract IdName getWidgetIdName();

  abstract void calculateCategoryScores(Widget widget, ScoreComponentSettings paramSettings, List<ScoreWeight> categoryScores)
    throws DataNotFoundException, ThresholdException;

  protected void calculateWidgetScore(ScoreWeight scoreWidget) {
    List<ScoreWeight> categoryScores = scoreWidget.getChildren();
    try {
    scoreWidget.setScore(
      ScoreCalculationUtils.calculateComponentScoreTypeValue(categoryScores, PropagateType.widget)
      );
    scoreWidget.setState(ScoreWeight.ProcessingState.complete);
    } catch (PropagateScoreException ex) {
      scoreWidget.setScore(ex.getScore());
      scoreWidget.setMessage(ex.getMessage());
      scoreWidget.setState(ex.getState());
    }
  }

  /*
    This will normalize the score weight for all widget children categories
   */
  protected void processWidgetScoreChildren(ScoreWeight widgetScore) {
    List<ScoreWeight> categoriesWeight = widgetScore.getChildren();
    ScoreCalculationUtils.normalizeWeightForScore(categoriesWeight, PropagateType.widget);
  }

  protected ScoreWeight initWidgetScore(ScoreComponentSettings paramSettings) {
    IdName widgetIdName = getWidgetIdName();
    ScoreWeight scoreWidget = new ScoreWeight(
      widgetIdName.getId(),
      widgetIdName.getName()
      );
    scoreWidget.setWeight(paramSettings.getWeight());
    scoreWidget.setChildren(initCategoryScores());
    return scoreWidget;
  }


  protected List<ScoreWeight> initCategoryScores() {

    List<IdName> categories = getCategories();

    if (CollectionUtils.isEmpty(categories)) {
      return null;
    }

    List<ScoreWeight> categoryScores = new ArrayList<>();
    for (IdName category : categories) {
      categoryScores.add(
        new ScoreWeight(
          category.getId(),
          category.getName()
        )
        );
    }

    return categoryScores;
  }

  protected ScoreWeight getCategoryScoreByIdName(List<ScoreWeight> categoryScores, IdName category) {

    if (CollectionUtils.isEmpty(categoryScores) || null == category) {
      return null;
    }

    for (ScoreWeight categoryScore : categoryScores) {
      if (categoryScore.getId().equals(category.getId()) &&
        categoryScore.getName().equals(category.getName())) {
        return categoryScore;
      }
    }

    return null;
  }

  protected void setCategoryScoreWeight(List<ScoreWeight> categoryScores, IdName category, int weight) {
    if (CollectionUtils.isEmpty(categoryScores) || null == category) {
      return;
    }

    for (ScoreWeight categoryScore : categoryScores) {
      if (categoryScore.getId().equals(category.getId()) &&
        categoryScore.getName().equals(category.getName())) {
        categoryScore.setWeight(weight);
        return;
      }
    }
  }

  protected void setThresholdFailureWeight(ThresholdException ex, ScoreWeight scoreWeight) {
    scoreWeight.setState(ThresholdUtils.getCriteriaStateFromScore(ex.getScore()));
    scoreWeight.setScore(ex.getScore());
    if (scoreWeight.getState() == ScoreWeight.ProcessingState.criteria_failed) {
      scoreWeight.setMessage(ex.getMessagePair().getRight());
    } else {
      scoreWeight.setMessage(ex.getMessagePair().getLeft());
    }
  }

  protected void checkPercentThresholds(
    ScoreComponentSettings scoreSettings,
    Double percent)
    throws ThresholdException {
    ScoreCriteria scoreCriteria = scoreSettings.getCriteria();
    if (null == scoreCriteria) {
      return;
    }

    List<ScoreThresholdSettings> thresholdSettings = scoreCriteria.getDataRangeThresholds();
    if (CollectionUtils.isEmpty(thresholdSettings)) {
      return;
    }

    ThresholdUtils.sortByValueType(thresholdSettings);

    List<ScoreThresholdSettings> percentThresholds = ThresholdUtils.findAllPercentThresholds(thresholdSettings);
    if (CollectionUtils.isNotEmpty(percentThresholds)) {
      //Check if it meets the first threshold, and return with Score zero or valuePercent Or throw noscoreexeption
      ThresholdUtils.checkPercentThresholds(percentThresholds, percent);

    }
  }

}
