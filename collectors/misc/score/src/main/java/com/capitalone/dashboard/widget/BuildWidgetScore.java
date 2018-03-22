package com.capitalone.dashboard.widget;

import java.util.*;

import com.capitalone.dashboard.exception.ThresholdException;
import com.capitalone.dashboard.model.score.settings.*;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.Constants;
import com.capitalone.dashboard.ThresholdUtils;
import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.exception.DataNotFoundException;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.google.common.collect.Lists;
import com.mysema.query.BooleanBuilder;

/**
 * Service to calculate build widget score
 * Build scores are based on
 * 1. Percentage of successful builds
 * 2. Percentage of successful builds within threshold
 */
@Service
public class BuildWidgetScore extends WidgetScoreAbstract {
  @SuppressWarnings({"unused", "PMD.UnusedPrivateField"})
  private static final Logger LOGGER = LoggerFactory.getLogger(BuildWidgetScore.class);
  private final BuildRepository buildRepository;
  private final ComponentRepository componentRepository;

  protected final static String WIDGET_BUILD_STATUS = "status";
  protected final static String WIDGET_BUILD_STATUS_NAME = "Status";
  protected final static String WIDGET_BUILD_DURATION = "duration";
  protected final static String WIDGET_BUILD_DURATION_NAME = "Duration";

  public final static IdName WIDGET_ID_NAME = new IdName(
    Constants.WIDGET_BUILD,
    Constants.WIDGET_BUILD_NAME
    );

  public final static IdName WIDGET_BUILD_STATUS_ID_NAME = new IdName(
    WIDGET_BUILD_STATUS,
    WIDGET_BUILD_STATUS_NAME
    );

  public final static IdName WIDGET_BUILD_DURATION_ID_NAME = new IdName(
    WIDGET_BUILD_DURATION,
    WIDGET_BUILD_DURATION_NAME
    );

  //Categories are various factors which contribute to the overall score of the widget
  public final List<IdName> categories;

  @Autowired
  public BuildWidgetScore(BuildRepository buildRepository,
    ComponentRepository componentRepository) {
    this.buildRepository = buildRepository;
    this.componentRepository = componentRepository;
    this.categories = Lists.newArrayList(
      WIDGET_BUILD_STATUS_ID_NAME,
      WIDGET_BUILD_DURATION_ID_NAME
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
  protected void calculateCategoryScores(Widget buildWidget, ScoreComponentSettings paramSettings, List<ScoreWeight> categoryScores)
    throws DataNotFoundException, ThresholdException {
    if (CollectionUtils.isEmpty(categoryScores)) {
      return;
    }

    BuildScoreSettings buildScoreSettings = (BuildScoreSettings) paramSettings;

    ScoreComponentSettings buildStatusSettings = Utils.getInstanceIfNull(
      buildScoreSettings.getStatus(),
      ScoreComponentSettings.class
      );
    BuildScoreSettings.BuildDurationScoreSettings buildDurationSettings = Utils.getInstanceIfNull(
      buildScoreSettings.getDuration(),
      BuildScoreSettings.BuildDurationScoreSettings.class
      );

    setCategoryScoreWeight(categoryScores, WIDGET_BUILD_STATUS_ID_NAME, buildStatusSettings.getWeight());
    setCategoryScoreWeight(categoryScores, WIDGET_BUILD_DURATION_ID_NAME, buildDurationSettings.getWeight());

    boolean isBuildStatusScoreEnabled = Utils.isScoreEnabled(buildStatusSettings);
    boolean isBuildDurationScoreEnabled = Utils.isScoreEnabled(buildDurationSettings);

    Iterable<Build> builds = null;

    if (isBuildStatusScoreEnabled || isBuildDurationScoreEnabled) {
      BuildSearch request = new BuildSearch();
      request.setComponentId(buildWidget.getComponentId());
      request.setNumberOfDays(buildScoreSettings.getNumberOfDays());
      builds = search(request);
    }

    if (null == builds || !builds.iterator().hasNext()) {
      throw new DataNotFoundException(Constants.SCORE_ERROR_NO_DATA_FOUND);
    }

    //Check thresholds at widget level
    checkWidgetDataThresholds(buildScoreSettings, builds);

    //Check if we can calculate score from

    if (isBuildStatusScoreEnabled) {
      Double buildSuccessRatio = fetchBuildSuccessRatio(builds);

      ScoreWeight buildCategoryStatusScore = getCategoryScoreByIdName(categoryScores, WIDGET_BUILD_STATUS_ID_NAME);
      buildCategoryStatusScore.setScore(
        new ScoreTypeValue(buildSuccessRatio)
        );
      buildCategoryStatusScore.setState(ScoreWeight.ProcessingState.complete);
    }

    if (isBuildDurationScoreEnabled) {
      Double buildDurationWithinThresholdRatio = fetchBuildDurationWithinThresholdRatio(
        builds,
        buildDurationSettings.getBuildDurationThresholdInMillis()
        );

      ScoreWeight buildCategoryDurationScore = getCategoryScoreByIdName(categoryScores, WIDGET_BUILD_DURATION_ID_NAME);
      buildCategoryDurationScore.setScore(
        new ScoreTypeValue(buildDurationWithinThresholdRatio)
        );
      buildCategoryDurationScore.setState(ScoreWeight.ProcessingState.complete);
    }

  }

  private void checkWidgetDataThresholds(BuildScoreSettings buildScoreSettings, Iterable<Build> builds)
    throws ThresholdException {
    ScoreCriteria scoreCriteria = buildScoreSettings.getCriteria();
    if (null == scoreCriteria) {
      return;
    }

    List<ScoreThresholdSettings> thresholdSettings = scoreCriteria.getDataRangeThresholds();
    if (CollectionUtils.isEmpty(thresholdSettings)) {
      return;
    }

    ThresholdUtils.sortByValueType(thresholdSettings);
    List<Long> buildTimestamps = getBuildTimestamps(builds);
    List<ScoreThresholdSettings> percentThresholds = ThresholdUtils.findAllPercentThresholds(thresholdSettings);
    if (CollectionUtils.isNotEmpty(percentThresholds)) {
      //Check if it meets the first threshold, and return with Score zero or value_percent Or throw noscoreexeption
      ThresholdUtils.checkPercentThresholds(percentThresholds, buildTimestamps, buildScoreSettings.getNumberOfDays());

    }

    List<ScoreThresholdSettings> daysThresholds = ThresholdUtils.findAllDaysThresholds(thresholdSettings);
    if (CollectionUtils.isNotEmpty(daysThresholds)) {
      //Check if it meets the first threshold, and return with Score zero or value_percent Or throw noscoreexeption
      ThresholdUtils.checkDaysThresholds(daysThresholds, buildTimestamps, buildScoreSettings.getNumberOfDays());
    }
  }

  private List<Long> getBuildTimestamps(Iterable<Build> builds) {
    List<Long> timestamps = new ArrayList<>();
    for (Build build : builds) {
      timestamps.add(build.getTimestamp());
    }
    return timestamps;
  }

  /**
   * Calculate percentage of successful builds
   * Any build with status InProgress, Aborted is excluded from calculation
   * Builds with status as Success, Unstable is included as success build
   *
   * @param builds iterable build
   * @return percentage of build success
   */
  private Double fetchBuildSuccessRatio(Iterable<Build> builds) {
    int totalBuilds = 0, totalSuccess = 0;
    for (Build build : builds) {
      if (Constants.IGNORE_STATUS.contains(build.getBuildStatus())) {
        continue;
      }

      totalBuilds++;
      if (Constants.SUCCESS_STATUS.contains(build.getBuildStatus())) {
        totalSuccess++;
      }
    }
    if (totalBuilds == 0) {
      return 0.0d;
    }
    return ((totalSuccess * 100) / (double) totalBuilds);
  }

  /**
   * Calculate builds that completed successfully within threshold time
   * Only builds with status as Success, Unstable is included for calculation
   *
   * @param builds iterable builds
   * @param thresholdInMillis threshold for build times in milliseconds
   * @return percentage of builds within threshold
   */
  private Double fetchBuildDurationWithinThresholdRatio(Iterable<Build> builds, long thresholdInMillis) {
    int totalBuilds = 0, totalBelowThreshold = 0;
    for (Build build : builds) {
      if (!Constants.SUCCESS_STATUS.contains(build.getBuildStatus())) {
        continue;
      }

      totalBuilds++;
      if (build.getDuration() < thresholdInMillis) {
        totalBelowThreshold++;
      }
    }
    if (totalBuilds == 0) {
      return 0.0d;
    }
    return ((totalBelowThreshold * 100) / (double) totalBuilds);
  }

  /**
   * Search for build records from request object
   *
   * @param request build search request object
   * @return iterable builds
   */
  private Iterable<Build> search(BuildSearch request) {
    Component component = componentRepository.findOne(request.getComponentId());
    CollectorItem item = component.getFirstCollectorItemForType(CollectorType.Build);
    if (item == null) {
      Iterable<Build> results = new ArrayList<>();
      return results;
    }

    QBuild build = new QBuild("build");
    BooleanBuilder builder = new BooleanBuilder();

    builder.and(build.collectorItemId.eq(item.getId()));

    if (request.getNumberOfDays() != null) {
      long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
      builder.and(build.endTime.goe(endTimeTarget));
    } else {
      if (request.validStartDateRange()) {
        builder.and(build.startTime.between(request.getStartDateBegins(), request.getStartDateEnds()));
      }
      if (request.validEndDateRange()) {
        builder.and(build.endTime.between(request.getEndDateBegins(), request.getEndDateEnds()));
      }
    }
    if (request.validDurationRange()) {
      builder.and(build.duration.between(request.getDurationGreaterThan(), request.getDurationLessThan()));
    }

    if (!request.getBuildStatuses().isEmpty()) {
      builder.and(build.buildStatus.in(request.getBuildStatuses()));
    }

    Iterable<Build> result;
    if (request.getMax() == null) {
      result = buildRepository.findAll(builder.getValue());
    } else {
      PageRequest pageRequest = new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
      result = buildRepository.findAll(builder.getValue(), pageRequest).getContent();
    }

    return result;
  }

}
