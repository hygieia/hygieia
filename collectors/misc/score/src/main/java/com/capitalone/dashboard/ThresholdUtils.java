package com.capitalone.dashboard;

import java.util.*;

import com.capitalone.dashboard.model.ScoreWeight;
import org.apache.commons.collections.CollectionUtils;

import com.capitalone.dashboard.model.score.settings.ScoreThresholdSettings;
import com.capitalone.dashboard.model.score.settings.ScoreTypeValue;
import com.capitalone.dashboard.exception.ThresholdException;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;

public class ThresholdUtils {

  public static void sortByValueType(List<ScoreThresholdSettings> scoreThresholdSettings) {
    if (CollectionUtils.isEmpty(scoreThresholdSettings)) {
      return;
    }

    Collections.sort(scoreThresholdSettings, VALUE_TYPE_COMPATATOR);
  }

  private static final Comparator<ScoreThresholdSettings> VALUE_TYPE_COMPATATOR = new Comparator<ScoreThresholdSettings>() {

    @Override
    public int compare(ScoreThresholdSettings o1, ScoreThresholdSettings o2) {
      int typeCompare = o1.getType().compareTo(o2.getType());
      if (0 != typeCompare) {
        return typeCompare;
      }

      if (isComparatorForGreater(o1.getComparator()) &&
        isComparatorForGreater(o2.getComparator())) {
        return o2.getValue().compareTo(o1.getValue());
      }
      return o1.getValue().compareTo(o2.getValue());
    }

    private boolean isComparatorForGreater(ScoreThresholdSettings.ComparatorType comparatorType) {
      return (comparatorType == ScoreThresholdSettings.ComparatorType.greater
      || comparatorType == ScoreThresholdSettings.ComparatorType.greater_or_equal);

    }
  };


  public static List<ScoreThresholdSettings> findAllThresholdsByType(List<ScoreThresholdSettings> scoreThresholdSettings, ScoreThresholdSettings.ValueType valueType) {
    if (CollectionUtils.isEmpty(scoreThresholdSettings)) {
      return null;
    }

    List<ScoreThresholdSettings> filteredByType = new ArrayList<>();
    for (ScoreThresholdSettings scoreThresholdSetting : scoreThresholdSettings) {
      if (scoreThresholdSetting.getType() == valueType) {
        filteredByType.add(scoreThresholdSetting);
      }
    }
    return filteredByType;
  }


  public static List<ScoreThresholdSettings> findAllPercentThresholds(List<ScoreThresholdSettings> scoreThresholdSettings) {
    return findAllThresholdsByType(scoreThresholdSettings, ScoreThresholdSettings.ValueType.percent);
  }

  public static List<ScoreThresholdSettings> findAllDaysThresholds(List<ScoreThresholdSettings> scoreThresholdSettings) {
    return findAllThresholdsByType(scoreThresholdSettings, ScoreThresholdSettings.ValueType.days);
  }

  public static void checkPercentThresholds(List<ScoreThresholdSettings> percentThresholds, Double coveragePercent)
    throws ThresholdException {
    for (ScoreThresholdSettings percentThreshold : percentThresholds) {
      ScoreTypeValue scoreTypeValue = getScoreForThreshold(percentThreshold, coveragePercent.compareTo(percentThreshold.getValue()));
      if (null != scoreTypeValue) {
        throw new ThresholdException(
          getThresholdFailureMessage(percentThreshold),
          scoreTypeValue
        );
      }
    }
  }

  public static void checkPercentThresholds(
    List<ScoreThresholdSettings> percentThresholds,
    List<Long> timestamps,
    int days)
    throws ThresholdException {
    for (ScoreThresholdSettings percentThreshold : percentThresholds) {
      Integer numDaysToCheck = percentThreshold.getNumDaysToCheck();
      int checkLastDays = (null != numDaysToCheck) ? numDaysToCheck : days;
      Double coveragePercent = getPercentCoverage(timestamps, checkLastDays, days);
      ScoreTypeValue scoreTypeValue = getScoreForThreshold(percentThreshold, coveragePercent.compareTo(percentThreshold.getValue()));
      if (null != scoreTypeValue) {
        throw new ThresholdException(
          getThresholdFailureMessage(percentThreshold, checkLastDays),
          scoreTypeValue
        );
      }
    }
  }


  public static void checkDaysThresholds(
    List<ScoreThresholdSettings> daysThresholds,
    List<Long> timestamps,
    int days)
    throws ThresholdException {
    for (ScoreThresholdSettings daysThreshold : daysThresholds) {
      Integer numDaysToCheck = daysThreshold.getNumDaysToCheck();
      int checkLastDays = (null != numDaysToCheck) ? numDaysToCheck : days;
      Double coverageDays = Double.valueOf(getDaysCoverage(timestamps, checkLastDays, days));
      ScoreTypeValue scoreTypeValue = ThresholdUtils.getScoreForThreshold(daysThreshold, coverageDays.compareTo(daysThreshold.getValue()));
      if (null != scoreTypeValue) {
        throw new ThresholdException(
          ThresholdUtils.getThresholdFailureMessage(daysThreshold, checkLastDays),
          scoreTypeValue
        );
      }
    }
  }

  private static Double getPercentCoverage(List<Long> timestamps, int checkLastDays, int days) {
    List<Long> filteredTimestamps;
    if (checkLastDays == days) {
      filteredTimestamps = timestamps;
    } else {
      filteredTimestamps = filterTimestampsForDays(timestamps, checkLastDays);
    }
    Set<String> dates = new HashSet<>();
    for (Long timestamp : filteredTimestamps) {
      dates.add(Constants.DAY_FORMAT.format(new Date(timestamp)));
    }
    return (dates.size() * 100) / ((double) checkLastDays);
  }


  private static int getDaysCoverage(List<Long> timestamps, int checkLastDays, int days) {
    List<Long> filteredTimestamps;
    if (checkLastDays == days) {
      filteredTimestamps = timestamps;
    } else {
      filteredTimestamps = filterTimestampsForDays(timestamps, checkLastDays);
    }
    Set<String> dates = new HashSet<>();
    for (Long timestamp : filteredTimestamps) {
      dates.add(Constants.DAY_FORMAT.format(new Date(timestamp)));
    }
    return dates.size();
  }

  private static List<Long> filterTimestampsForDays(List<Long> timestamps, int checkLastDays) {
    long startTimeTarget = new LocalDate().minusDays(checkLastDays).toDate().getTime();
    List<Long> filteredTimestamps = new ArrayList<>();
    for (Long timestamp : timestamps) {
      if (timestamp >= startTimeTarget) {
        filteredTimestamps.add(timestamp);
      }
    }
    return filteredTimestamps;
  }

  public static Pair<String, String> getThresholdFailureMessage(ScoreThresholdSettings scoreThresholdSettings) {
    return getThresholdFailureMessage(scoreThresholdSettings, null);
  }


  public static Pair<String, String> getThresholdFailureMessage(ScoreThresholdSettings scoreThresholdSettings, Integer days) {
    String prefixPass = "",
      prefixFail = "",
      suffix = "",
      typeMessage = "",
      value = Utils.roundAlloc(scoreThresholdSettings.getValue());

    boolean daysPresent = false;
    boolean isValueMax = (scoreThresholdSettings.getValue().compareTo(Double.valueOf(Constants.MAX_SCORE)) == 0);
    if (null == days || days == 0) {
      prefixPass = prefixFail = "Value is ";
    } else {
      daysPresent = true;
      prefixPass = "Data is present for ";
      prefixFail = "Data should be present for ";
      suffix = " days of last " + days + " days";
    }

    Pair<String, String> comparatorMessage = getComparatorMessage(
      scoreThresholdSettings.getComparator(),
      daysPresent,
      isValueMax
    );

    if (scoreThresholdSettings.getType() == ScoreThresholdSettings.ValueType.percent) {
      typeMessage = "%";
    } else if (scoreThresholdSettings.getType() == ScoreThresholdSettings.ValueType.days) {
      typeMessage = " days";
    }

    String passMessage = prefixPass + comparatorMessage.getLeft() +
      value + typeMessage + suffix;
    String failMessage = prefixFail + comparatorMessage.getRight() +
        value + typeMessage + suffix;

    return Pair.of(passMessage, failMessage);
  }

  private static Pair<String, String> getComparatorMessage(
    ScoreThresholdSettings.ComparatorType comparatorType,
    boolean daysPresent,
    boolean isValueMax
  ) {
    String greaterOrMore = daysPresent ? "more" : "greater",
      less = "less than ",
      greater = greaterOrMore + " than ",
      lessOrEqual = "less than or equal to ",
      greaterOrEqual = greaterOrMore + " than or equal to ",
      comparatorMessagePass = "",
      comparatorMessageFail = "";


    switch (comparatorType) {
      case equals:
        break;
      case less:
        comparatorMessagePass = less;
        if (!daysPresent) {
          comparatorMessageFail = comparatorMessagePass;
        } else if (!isValueMax) {
          comparatorMessageFail = greaterOrEqual;
        }
        break;
      case greater:
        comparatorMessagePass = greater;
        if (!daysPresent) {
          comparatorMessageFail = comparatorMessagePass;
        } else {
          comparatorMessageFail = lessOrEqual;
        }
        break;
      case less_or_equal:
        comparatorMessagePass = lessOrEqual;
        if (!daysPresent) {
          comparatorMessageFail = comparatorMessagePass;
        } else {
          comparatorMessageFail = greater;
        }
        break;
      case greater_or_equal:
        if (!isValueMax) {
          comparatorMessagePass = greaterOrEqual;
        }
        if (!daysPresent) {
          comparatorMessageFail = comparatorMessagePass;
        } else {
          comparatorMessageFail = less;
        }
        break;
      default:
        break;
    }

    return Pair.of(comparatorMessagePass, comparatorMessageFail);
  }

  public static ScoreTypeValue getScoreForThreshold(ScoreThresholdSettings percentThreshold, int compareResult) {
    switch (percentThreshold.getComparator()) {
      case equals:
        if (compareResult == 0) {
          return percentThreshold.getScore();
        }
        return null;
      case less:
        if (compareResult == -1) {
          return percentThreshold.getScore();
        }
        return null;
      case greater:
        if (compareResult == 1) {
          return percentThreshold.getScore();
        }
        return null;
      case less_or_equal:
        if (compareResult == 0 || compareResult == -1) {
          return percentThreshold.getScore();
        }
        return null;
      case greater_or_equal:
        if (compareResult == 0 || compareResult == 1) {
          return percentThreshold.getScore();
        }
        return null;
      default:
        return null;
    }

  }

  public static ScoreWeight.ProcessingState getCriteriaStateFromScore(ScoreTypeValue scoreTypeValue) {
    if (scoreTypeValue.getScoreValue().compareTo(Constants.ZERO_SCORE) > 0) {
      return ScoreWeight.ProcessingState.criteria_passed;
    }
    return ScoreWeight.ProcessingState.criteria_failed;
  }

}
