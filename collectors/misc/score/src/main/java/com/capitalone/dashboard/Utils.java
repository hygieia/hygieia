package com.capitalone.dashboard;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.capitalone.dashboard.model.score.settings.ScoreComponentSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.model.score.settings.ScoreCriteria;
import com.capitalone.dashboard.model.score.settings.ScoreThresholdSettings;
import com.capitalone.dashboard.model.score.settings.ScoreTypeValue;

public final class Utils {

  private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

  public static String roundAlloc(Double alloc) {
    return new DecimalFormat("#.#").format(alloc);
  }

  public static boolean isScoreEnabled(ScoreComponentSettings paramSettings) {
    return (null != paramSettings && !paramSettings.isDisabled());
  }

  public static <T> T getInstanceIfNull(T obj, Class<T> tClass) {
    if (null == obj) {
      try {
        Constructor<T> ctor = tClass.getConstructor();
        return ctor.newInstance();
      } catch (Exception ex) {
        LOGGER.error("Exception to create a instance for {}", tClass, ex);
      }
    }
    return obj;
  }

  public static ScoreCriteria mergeCriteria(ScoreCriteria parent, ScoreCriteria child) {
    if (null == child) {
      return ScoreCriteria.cloneScoreCriteria(parent);
    }

    if (null == parent) {
      return child;
    }

    ScoreCriteria childMerged = ScoreCriteria.cloneScoreCriteria(child);

    if (null == childMerged.getNoWidgetFound()) {
      childMerged.setNoWidgetFound(
        ScoreTypeValue.cloneScoreTypeValue(parent.getNoWidgetFound())
        );
    }

    if (null == childMerged.getNoDataFound()) {
      childMerged.setNoDataFound(
        ScoreTypeValue.cloneScoreTypeValue(parent.getNoDataFound())
        );
    }

    if (null == childMerged.getDataRangeThresholds()) {
      childMerged.setDataRangeThresholds(
        cloneThresholds(parent.getDataRangeThresholds())
        );
    }

    return childMerged;
  }


  public static List<ScoreThresholdSettings> cloneThresholds(List<ScoreThresholdSettings> thresholds) {
    if (null == thresholds) {
      return null;
    }
    List<ScoreThresholdSettings> thresholdsClone = new ArrayList<>();
    for (ScoreThresholdSettings threshold : thresholds) {
      thresholdsClone.add(
        ScoreThresholdSettings.cloneScoreThresholdSettings(threshold)
        );
    }
    return thresholdsClone;
  }

}
