package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.LogAnalysizerRepository;
import com.capitalone.dashboard.request.LogAnalysisSearchRequest;
import org.springframework.stereotype.Service;

/**
 * Created by stevegal on 22/06/2018.
 */
@Service
public class LogAnalysisServiceImpl implements LogAnalysisService {

  private final ComponentRepository componentRepository;
  private final CollectorRepository collectorRepository;
  private LogAnalysizerRepository repository;

  public LogAnalysisServiceImpl(LogAnalysizerRepository repository,
                                ComponentRepository componentRepository,
                                CollectorRepository collectorRepository) {
    this.collectorRepository = collectorRepository;
    this.componentRepository = componentRepository;
    this.repository = repository;
  }

  @Override
  public DataResponse<Iterable<LogAnalysis>> search(LogAnalysisSearchRequest request) {
    if (null == request) {
      return new DataResponse<>(null,System.currentTimeMillis());
    }
    return null;
  }
}
