package com.capitalone.dashboard;

import com.capitalone.dashboard.model.BuildStatus;
import com.google.common.collect.Lists;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public final class Constants {

  public final static String WIDGET_BUILD = "build";
  public final static String WIDGET_BUILD_NAME = "Build";
  public final static String WIDGET_DEPLOY = "deploy";
  public final static String WIDGET_DEPLOY_NAME = "Deploy";
  public final static String WIDGET_CODE_ANALYSIS = "codeanalysis";
  public final static String WIDGET_CODE_ANALYSIS_NAME = "Quality";
  public final static String WIDGET_GITHUB_SCM = "repo";
  public final static String WIDGET_GITHUB_SCM_NAME = "SCM";

  public final static String SCORE_ERROR_NO_DATA_FOUND = "No data found!";
  public final static String SCORE_ERROR_NO_WIDGET_FOUND = "No widget found!";

  public final static int MAX_SCORE = 100;

  public final static double ZERO_SCORE = 0.0d;

  public final static DateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public static final List<BuildStatus> SUCCESS_STATUS = Collections.unmodifiableList(Lists.newArrayList(BuildStatus.Success, BuildStatus.Unstable));
  public static final List<BuildStatus> IGNORE_STATUS = Collections.unmodifiableList(Lists.newArrayList(BuildStatus.InProgress, BuildStatus.Aborted));

}
