package com.capitalone.dashboard.widget;

import java.util.Iterator;
import java.util.List;

import com.capitalone.dashboard.model.score.settings.QualityScoreSettings;
import com.capitalone.dashboard.model.score.settings.ScoreComponentSettings;
import com.capitalone.dashboard.model.score.settings.ScoreTypeValue;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.Constants;
import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.exception.DataNotFoundException;
import com.capitalone.dashboard.exception.ThresholdException;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mysema.query.BooleanBuilder;

/**
 * Service to calculate quality widget score
 * Quality scores are based on
 * 1. Percentage of code coverage
 * 2. Percentage of successful unit tests
 * 3. Violations (Critical, Blocker, Major)
 */
@Service
public class QualityWidgetScore extends WidgetScoreAbstract {
  @SuppressWarnings({"unused", "PMD.UnusedPrivateField"})
  private static final Logger LOGGER = LoggerFactory.getLogger(QualityWidgetScore.class);

  protected final static String WIDGET_QUALITY_CC = "qualityCC";
  protected final static String WIDGET_QUALITY_CC_NAME = "Code Coverage";
  protected final static String WIDGET_QUALITY_UT = "qualityUT";
  protected final static String WIDGET_QUALITY_UT_NAME = "Unit Tests Success";
  protected final static String WIDGET_QUALITY_VIOLATIONS = "qualityViolations";
  protected final static String WIDGET_QUALITY_VIOLATIONS_NAME = "Violations";
  protected final static String QUALITY_PARAM_CC = "line_coverage";
  protected final static String QUALITY_PARAM_UT = "test_success_density";
  protected final static String QUALITY_PARAM_CRITICAL_VIOLATIONS = "critical_violations";
  protected final static String QUALITY_PARAM_BLOCKER_VIOLATIONS = "blocker_violations";
  protected final static String QUALITY_PARAM_MAJOR_VIOLATIONS = "major_violations";

  public final static IdName WIDGET_ID_NAME = new IdName(
    Constants.WIDGET_CODE_ANALYSIS,
    Constants.WIDGET_CODE_ANALYSIS_NAME
    );

  public final static IdName WIDGET_QUALITY_CC_ID_NAME = new IdName(
    WIDGET_QUALITY_CC,
    WIDGET_QUALITY_CC_NAME
    );

  public final static IdName WIDGET_QUALITY_UT_ID_NAME = new IdName(
    WIDGET_QUALITY_UT,
    WIDGET_QUALITY_UT_NAME
    );

  public final static IdName WIDGET_QUALITY_VIOLATIONS_ID_NAME = new IdName(
    WIDGET_QUALITY_VIOLATIONS,
    WIDGET_QUALITY_VIOLATIONS_NAME
  );

  private final CodeQualityRepository codeQualityRepository;
  private final ComponentRepository componentRepository;

  //Categories are various factors which contribute to the overall score of the widget
  public final List<IdName> categories;

  @Autowired
  public QualityWidgetScore(CodeQualityRepository codeQualityRepository,
    ComponentRepository componentRepository) {
    this.codeQualityRepository = codeQualityRepository;
    this.componentRepository = componentRepository;
    this.categories = Lists.newArrayList(
      WIDGET_QUALITY_CC_ID_NAME,
      WIDGET_QUALITY_UT_ID_NAME,
      WIDGET_QUALITY_VIOLATIONS_ID_NAME
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
  protected void calculateCategoryScores(Widget qualityWidget, ScoreComponentSettings paramSettings, List<ScoreWeight> categoryScores)
    throws DataNotFoundException, ThresholdException {
    if (CollectionUtils.isEmpty(categoryScores)) {
      return;
    }

    QualityScoreSettings qualityScoreSettings = (QualityScoreSettings) paramSettings;

    ScoreComponentSettings qualityCCSettings = Utils.getInstanceIfNull(qualityScoreSettings.getCodeCoverage(), ScoreComponentSettings.class);
    ScoreComponentSettings qualityUTSettings = Utils.getInstanceIfNull(qualityScoreSettings.getUnitTests(), ScoreComponentSettings.class);
    QualityScoreSettings.ViolationsScoreSettings violationsSettings = Utils.getInstanceIfNull(qualityScoreSettings.getViolations(), QualityScoreSettings.ViolationsScoreSettings.class);
    setCategoryScoreWeight(categoryScores, WIDGET_QUALITY_CC_ID_NAME, qualityCCSettings.getWeight());
    setCategoryScoreWeight(categoryScores, WIDGET_QUALITY_UT_ID_NAME, qualityUTSettings.getWeight());
    setCategoryScoreWeight(categoryScores, WIDGET_QUALITY_VIOLATIONS_ID_NAME, violationsSettings.getWeight());

    boolean isQualityCCScoreEnabled = Utils.isScoreEnabled(qualityCCSettings);
    boolean isQualityUTScoreEnabled = Utils.isScoreEnabled(qualityUTSettings);
    boolean isQualityViolationsScoreEnabled = Utils.isScoreEnabled(violationsSettings);

    Iterable<CodeQuality> codeQualityIterable = null;

    if (isQualityCCScoreEnabled || isQualityUTScoreEnabled || isQualityViolationsScoreEnabled) {
      codeQualityIterable = search(qualityWidget.getComponentId());
    }

    if (null == codeQualityIterable) {
      throw new DataNotFoundException(Constants.SCORE_ERROR_NO_DATA_FOUND);
    }
    if (isQualityCCScoreEnabled) {
      processQualityCCScore(qualityCCSettings, codeQualityIterable, categoryScores);
    }


    if (isQualityUTScoreEnabled) {
      processQualityUTScore(qualityUTSettings, codeQualityIterable, categoryScores);
    }

    if (isQualityViolationsScoreEnabled) {
      processQualityViolationsScore(violationsSettings, codeQualityIterable, categoryScores);
    }
  }

  /**
   * Process Code Coverage score
   *
   * @param qualityCCSettings Code Coverage Param Settings
   * @param codeQualityIterable Quality values
   * @param categoryScores List of category scores
   */
  private void processQualityCCScore(
    ScoreComponentSettings qualityCCSettings,
    Iterable<CodeQuality> codeQualityIterable,
    List<ScoreWeight> categoryScores) {
    ScoreWeight qualityCCScore = getCategoryScoreByIdName(categoryScores, WIDGET_QUALITY_CC_ID_NAME);
    Double qualityCCRatio = fetchQualityValue(codeQualityIterable, QUALITY_PARAM_CC);

    if (null == qualityCCRatio) {
      qualityCCScore.setScore(
        qualityCCSettings.getCriteria().getNoDataFound()
      );
      qualityCCScore.setMessage(Constants.SCORE_ERROR_NO_DATA_FOUND);
      qualityCCScore.setState(ScoreWeight.ProcessingState.criteria_failed);
    } else {
      try {
        //Check thresholds at widget level
        checkPercentThresholds(qualityCCSettings, qualityCCRatio);
        qualityCCScore.setScore(
          new ScoreTypeValue(qualityCCRatio)
        );
        qualityCCScore.setState(ScoreWeight.ProcessingState.complete);
      } catch (ThresholdException ex) {
        setThresholdFailureWeight(ex, qualityCCScore);
      }
    }
  }

  /**
   * Process Unit Tests score
   *
   * @param qualityUTSettings Unit Tests Param Settings
   * @param codeQualityIterable Quality values
   * @param categoryScores List of category scores
   */
  private void processQualityUTScore(
    ScoreComponentSettings qualityUTSettings,
    Iterable<CodeQuality> codeQualityIterable,
    List<ScoreWeight> categoryScores) {
    ScoreWeight qualityUTScore = getCategoryScoreByIdName(categoryScores, WIDGET_QUALITY_UT_ID_NAME);
    Double qualityUTRatio = fetchQualityValue(codeQualityIterable, QUALITY_PARAM_UT);

    if (null == qualityUTRatio) {
      qualityUTScore.setScore(
        qualityUTSettings.getCriteria().getNoDataFound()
      );
      qualityUTScore.setMessage(Constants.SCORE_ERROR_NO_DATA_FOUND);
      qualityUTScore.setState(ScoreWeight.ProcessingState.criteria_failed);
    } else {
      try {
        //Check thresholds at widget level
        checkPercentThresholds(qualityUTSettings, qualityUTRatio);
        qualityUTScore.setScore(
          new ScoreTypeValue(qualityUTRatio)
        );
        qualityUTScore.setState(ScoreWeight.ProcessingState.complete);
      } catch (ThresholdException ex) {
        setThresholdFailureWeight(ex, qualityUTScore);
      }
    }
  }

  /**
   * Calculate Violations score based on Blocker, Critical & Major violations
   *
   * @param qualityViolationsSettings Violations Param Settings
   * @param codeQualityIterable Quality values
   * @param categoryScores List of category scores
   */
  private void processQualityViolationsScore(
    QualityScoreSettings.ViolationsScoreSettings qualityViolationsSettings,
    Iterable<CodeQuality> codeQualityIterable,
    List<ScoreWeight> categoryScores) {
    ScoreWeight qualityViolationsScore = getCategoryScoreByIdName(categoryScores, WIDGET_QUALITY_VIOLATIONS_ID_NAME);
    Double qualityBlockerRatio = fetchQualityValue(codeQualityIterable, QUALITY_PARAM_BLOCKER_VIOLATIONS);
    Double qualityCriticalRatio = fetchQualityValue(codeQualityIterable, QUALITY_PARAM_CRITICAL_VIOLATIONS);
    Double qualityMajorRatio = fetchQualityValue(codeQualityIterable, QUALITY_PARAM_MAJOR_VIOLATIONS);

    if (null == qualityBlockerRatio && null == qualityCriticalRatio && null == qualityMajorRatio) {
      qualityViolationsScore.setScore(
        qualityViolationsSettings.getCriteria().getNoDataFound()
      );
      qualityViolationsScore.setMessage(Constants.SCORE_ERROR_NO_DATA_FOUND);
      qualityViolationsScore.setState(ScoreWeight.ProcessingState.criteria_failed);
    } else {
      try {
        //Violation score is calculated based on weight for each type
        // Blocker
        // Critical
        // Major
        Double violationScore = Double.valueOf(Constants.MAX_SCORE) - (
            getViolationScore(qualityBlockerRatio, qualityViolationsSettings.getBlockerViolationsWeight()) +
            getViolationScore(qualityCriticalRatio, qualityViolationsSettings.getCriticalViolationsWeight()) +
            getViolationScore(qualityMajorRatio, qualityViolationsSettings.getMajorViolationWeight())
          );

        if (!qualityViolationsSettings.isAllowNegative() && violationScore.compareTo(Constants.ZERO_SCORE) < 0) {
          violationScore = Constants.ZERO_SCORE;
        }

        //Check thresholds at widget level
        checkPercentThresholds(qualityViolationsSettings, violationScore);
        qualityViolationsScore.setScore(
          new ScoreTypeValue(violationScore)
        );
        qualityViolationsScore.setState(ScoreWeight.ProcessingState.complete);
      } catch (ThresholdException ex) {
        setThresholdFailureWeight(ex, qualityViolationsScore);
      }
    }
  }

  private Double getViolationScore(Double violationValue, int weight) {
    if (null == violationValue) {
      return Constants.ZERO_SCORE;
    }
    return (violationValue * (double)weight);
  }

  /**
   * Fetch param value by param name from quality values
   *
   * @param qualityIterable
   * @param param quality param
   * @return Param Value
   */
  private Double fetchQualityValue(Iterable<CodeQuality> qualityIterable, String param) {
    Double paramValue = null;
    Iterator<CodeQuality> qualityIterator = qualityIterable.iterator();

    if (!qualityIterator.hasNext()) {
      return paramValue;
    }

    CodeQuality codeQuality = qualityIterator.next();
    for (CodeQualityMetric codeQualityMetric : codeQuality.getMetrics()) {
      if (codeQualityMetric.getName().equals(param)) {
        paramValue = Double.valueOf(codeQualityMetric.getValue());
        break;
      }
    }

    return paramValue;
  }

  private Iterable<CodeQuality> search(ObjectId componentId) {
    CollectorItem item = getCollectorItem(componentId);
    if (item == null) {
      return null;
    }

    QCodeQuality quality = new QCodeQuality("quality");
    BooleanBuilder builder = new BooleanBuilder();

    builder.and(quality.collectorItemId.eq(item.getId()));

    PageRequest pageRequest =
      new PageRequest(0, 1, Sort.Direction.DESC, "timestamp");
    Iterable<CodeQuality> result = codeQualityRepository.findAll(builder.getValue(), pageRequest).getContent();
    return result;
  }

  protected CollectorItem getCollectorItem(ObjectId componentId) {
    CollectorItem item = null;
    Component component = componentRepository.findOne(componentId);
    List<CollectorItem> items = component.getCollectorItems().get(CodeQualityType.StaticAnalysis.collectorType());
    if (items != null) {
      item = Iterables.getFirst(items, null);
    }
    return item;
  }

}
