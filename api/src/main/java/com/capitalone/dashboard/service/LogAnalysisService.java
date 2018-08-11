package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.model.LogAnalysisMetric;
import com.capitalone.dashboard.request.LogAnalysisSearchRequest; /**
 * Created by stevegal on 22/06/2018.
 */
public interface LogAnalysisService {
  DataResponse<Iterable<LogAnalysis>> search(LogAnalysisSearchRequest value);
}

