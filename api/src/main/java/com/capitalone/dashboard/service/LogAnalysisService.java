package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.request.LogAnalysisSearchRequest;

public interface LogAnalysisService {

  DataResponse<Iterable<LogAnalysis>> search(LogAnalysisSearchRequest value);

}

