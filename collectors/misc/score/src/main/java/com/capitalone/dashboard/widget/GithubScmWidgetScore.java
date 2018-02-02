package com.capitalone.dashboard.widget;

import com.capitalone.dashboard.Constants;
import com.capitalone.dashboard.ThresholdUtils;
import com.capitalone.dashboard.Utils;
import com.capitalone.dashboard.collector.*;
import com.capitalone.dashboard.exception.DataNotFoundException;
import com.capitalone.dashboard.exception.ThresholdException;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.google.common.collect.Lists;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GithubScmWidgetScore extends WidgetScoreAbstract {

  public static final int GITHUB_SCM_NUM_OF_DAYS = 14;

  protected final static String WIDGET_GITHUB_SCM_COMMITS_PER_DAY = "commitsPerDay";
  protected final static String WIDGET_GITHUB_SCM_COMMITS_PER_DAY_NAME = "Commits Per Day";

  public final static IdName WIDGET_ID_NAME = new IdName(
    Constants.WIDGET_GITHUB_SCM,
    Constants.WIDGET_GITHUB_SCM_NAME
  );

  public final static IdName WIDGET_GITHUB_SCM_COMMITS_PER_DAY_ID_NAME = new IdName(
    WIDGET_GITHUB_SCM_COMMITS_PER_DAY,
    WIDGET_GITHUB_SCM_COMMITS_PER_DAY_NAME
  );

  //Categories are various factors which contribute to the overall score of the widget
  public final List<IdName> categories;

  private final CommitRepository commitRepository;
  private final ComponentRepository componentRepository;

  @Autowired
  public GithubScmWidgetScore(CommitRepository commitRepository,
    ComponentRepository componentRepository) {
    this.commitRepository = commitRepository;
    this.componentRepository = componentRepository;
    this.categories = Lists.newArrayList(
      WIDGET_GITHUB_SCM_COMMITS_PER_DAY_ID_NAME
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
  protected void calculateCategoryScores(Widget githubScmWidget, ScoreParamSettings paramSettings, List<ScoreWeight> categoryScores)
    throws DataNotFoundException, ThresholdException {
    if (CollectionUtils.isEmpty(categoryScores)) {
      return;
    }

    GithubScmScoreSettings githubScmScoreSettings = (GithubScmScoreSettings) paramSettings;

    ScoreParamSettings commitsPerDaySettings = Utils.getInstanceIfNull(
      githubScmScoreSettings.getCommitsPerDay(),
      ScoreParamSettings.class
    );

    setCategoryScoreWeight(categoryScores, WIDGET_GITHUB_SCM_COMMITS_PER_DAY_ID_NAME, commitsPerDaySettings.getWeight());

    boolean isCommitsPerDayScoreEnabled = Utils.isScoreEnabled(commitsPerDaySettings);

    Iterable<Commit> commits = null;

    if (isCommitsPerDayScoreEnabled) {
      commits = search(githubScmWidget.getComponentId(), githubScmScoreSettings.getNumberOfDays());
    }

    if (null == commits || !commits.iterator().hasNext()) {
      throw new DataNotFoundException(Constants.SCORE_ERROR_NO_DATA_FOUND);
    }

    //Check thresholds at widget level
    checkWidgetDataThresholds(githubScmScoreSettings, commits, githubScmScoreSettings.getNumberOfDays());

    if (isCommitsPerDayScoreEnabled) {
      processCommitsPerDayScore(
        commits,
        githubScmScoreSettings.getCommitsPerDay(),
        githubScmScoreSettings.getNumberOfDays(),
        categoryScores
      );
    }

  }


  public Iterable<Commit> search(ObjectId componentId, int numberOfDays) {
    QCommit commit = new QCommit("search");
    BooleanBuilder builder = new BooleanBuilder();

    Component component = componentRepository.findOne(componentId);
    CollectorItem item = (component != null) ? component.getFirstCollectorItemForType(CollectorType.SCM):null;
    if (item == null) {
      return new ArrayList<>();
    }
    builder.and(commit.collectorItemId.eq(item.getId()));

    long endTimeTarget = new LocalDate().minusDays(numberOfDays).toDate().getTime();
    builder.and(commit.scmCommitTimestamp.goe(endTimeTarget));

    return commitRepository.findAll(builder.getValue());
  }

  private void checkWidgetDataThresholds(ScoreParamSettings githubScmScoreSettings, Iterable<Commit> commits, int numberOfDays)
    throws ThresholdException {
    ScoreCriteria scoreCriteria = githubScmScoreSettings.getCriteria();
    if (null == scoreCriteria) {
      return;
    }

    List<ScoreThresholdSettings> thresholdSettings = scoreCriteria.getDataRangeThresholds();
    if (CollectionUtils.isEmpty(thresholdSettings)) {
      return;
    }

    ThresholdUtils.sortByValueType(thresholdSettings);
    List<Long> commitTimestamps = getCommitTimestamps(commits);
    List<ScoreThresholdSettings> percentThresholds = ThresholdUtils.findAllPercentThresholds(thresholdSettings);
    if (CollectionUtils.isNotEmpty(percentThresholds)) {
      //Check if it meets the first threshold, and return with Score zero or valuePercent Or throw noscoreexeption
      ThresholdUtils.checkPercentThresholds(percentThresholds, commitTimestamps, numberOfDays);

    }

    List<ScoreThresholdSettings> daysThresholds = ThresholdUtils.findAllDaysThresholds(thresholdSettings);
    if (CollectionUtils.isNotEmpty(daysThresholds)) {
      //Check if it meets the first threshold, and return with Score zero or valuePercent Or throw noscoreexeption
      ThresholdUtils.checkDaysThresholds(daysThresholds, getCommitTimestamps(commits), numberOfDays);
    }
  }

  private Double getPercentCoverageForDays(Iterable<Commit> commits, int days) {
    Set<String> dates = new HashSet<>();
    for (Commit commit : commits) {
      dates.add(Constants.DAY_FORMAT.format(new Date(commit.getScmCommitTimestamp())));
    }
    return (dates.size() / (double) days) * 100;
  }

  private List<Long> getCommitTimestamps(Iterable<Commit> commits) {
    List<Long> timestamps = new ArrayList<>();
    for (Commit commit : commits) {
      timestamps.add(commit.getScmCommitTimestamp());
    }
    return timestamps;
  }


  private void processCommitsPerDayScore(
    Iterable<Commit> commits,
    ScoreParamSettings commitsPerDaySettings,
    int numberOfDays,
    List<ScoreWeight> categoryScores) {
    ScoreWeight commitsPerDayScore = getCategoryScoreByIdName(categoryScores, WIDGET_GITHUB_SCM_COMMITS_PER_DAY_ID_NAME);
    try {
      //Check thresholds at widget level
      checkWidgetDataThresholds(commitsPerDaySettings, commits, numberOfDays);
      commitsPerDayScore.setScore(
        new ScoreTypeValue(
          getPercentCoverageForDays(commits, numberOfDays)
        )
      );
      commitsPerDayScore.setState(ScoreWeight.ProcessingState.complete);
    } catch (ThresholdException ex) {
      setThresholdFailureWeight(ex, commitsPerDayScore);
    }
  }

}
