package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.LogAnalysizerRepository;
import com.capitalone.dashboard.request.LogAnalysisSearchRequest;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LogAnalysisServiceImplTest {

  @Mock
  private ComponentRepository mockComponentRepository;

  @Mock
  private CollectorRepository mockCollectorRepository;

  @Mock
  private LogAnalysizerRepository mockLogAnalyzerRepository;

  @InjectMocks
  private LogAnalysisServiceImpl subject;

  @Test
  public void nullRequestReturnsEmptyResponse() {
    long startTime = System.currentTimeMillis()-1000;
    DataResponse<Iterable<LogAnalysis>> response = subject.search(null);
    assertThat(response,notNullValue());
    assertThat(response.getResult(),nullValue());
    assertThat(response.getLastUpdated(),is(both(greaterThan(startTime)).and(lessThanOrEqualTo(System.currentTimeMillis()))));
  }

  @Test
  public void collectorItemNotFoundReturnsEmptyResponse() {
    long startTime = System.currentTimeMillis()-1000;
    LogAnalysisSearchRequest request = new LogAnalysisSearchRequest();
    request.setComponentId(ObjectId.get());
    when(mockComponentRepository.findOne(any(ObjectId.class))).thenReturn(null);
    DataResponse<Iterable<LogAnalysis>> response =subject.search(request);
    assertThat(response,notNullValue());
    assertThat(response.getResult(),nullValue());
    assertThat(response.getLastUpdated(),is(both(greaterThan(startTime)).and(lessThanOrEqualTo(System.currentTimeMillis()))));
  }

}