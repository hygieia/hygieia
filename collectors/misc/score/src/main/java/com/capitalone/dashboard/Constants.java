package com.capitalone.dashboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class Constants {

  public final static String WIDGET_BUILD = "build";
  public final static String WIDGET_BUILD_NAME = "Build";
  public final static String WIDGET_DEPLOY = "deploy";
  public final static String WIDGET_DEPLOY_NAME = "Deploy";
  public final static String WIDGET_CODE_ANALYSIS = "codeanalysis";
  public final static String WIDGET_CODE_ANALYSIS_NAME = "Quality";
  public final static String WIDGET_GITHUB_SCM = "repo";
  public final static String WIDGET_GITHUB_SCM_NAME = "GitHub SCM";

  public final static String SCORE_ERROR_NO_DATA_FOUND = "No data found!";
  public final static String SCORE_ERROR_NO_WIDGET_FOUND = "No widget found!";

  public final static int SCORE_TOTAL = 100;
  public final static int MAX_SCORE = 100;

  public final static double ZERO_SCORE = 0.0d;

  public final static DateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


}
