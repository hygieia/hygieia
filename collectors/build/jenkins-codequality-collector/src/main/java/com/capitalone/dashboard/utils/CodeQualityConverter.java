package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.JunitXmlReport;

import java.util.Set;

/**
 * Created by stephengalbraith on 11/10/2016.
 */
public interface CodeQualityConverter {
  Set<CodeQualityMetric> analyse(JunitXmlReport report);
}
