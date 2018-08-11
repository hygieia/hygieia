package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.request.LogAnalysisSearchRequest;
import org.springframework.stereotype.Service;

/**
 * Created by stevegal on 22/06/2018.
 */
@Service
public class LogAnalysisServiceImpl implements LogAnalysisService {
  @Override
  public DataResponse<Iterable<LogAnalysis>> search(LogAnalysisSearchRequest value) {
    return null;
  }
}
