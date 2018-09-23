package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.LogAnalysizerRepository;
import com.capitalone.dashboard.request.LogAnalysisSearchRequest;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanOperation;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


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

  @Test
  public void collectorItemFoundButNotOfType(){
    long startTime = System.currentTimeMillis()-1000;
    LogAnalysisSearchRequest request = new LogAnalysisSearchRequest();
    request.setComponentId(ObjectId.get());
    Component component = mock(Component.class);
    when(mockComponentRepository.findOne(any(ObjectId.class))).thenReturn(component);
    when(component.getFirstCollectorItemForType(eq(CollectorType.Log))).thenReturn(null);

    DataResponse<Iterable<LogAnalysis>> response =subject.search(request);

    assertThat(response,notNullValue());
    assertThat(response.getResult(),nullValue());
    assertThat(response.getLastUpdated(),is(both(greaterThan(startTime)).and(lessThanOrEqualTo(System.currentTimeMillis()))));

  }

  @Test
  public void collectorItemReturnsWhenFound() {
    LogAnalysisSearchRequest request = new LogAnalysisSearchRequest();
    request.setComponentId(ObjectId.get());
    Component component = mock(Component.class);
    when(mockComponentRepository.findOne(any(ObjectId.class))).thenReturn(component);
    CollectorItem item = mock(CollectorItem.class);
    when(component.getFirstCollectorItemForType(eq(CollectorType.Log))).thenReturn(item);
    ObjectId itemId = ObjectId.get();
    when(item.getId()).thenReturn(itemId);

    List<LogAnalysis> items = new ArrayList<>();
    items.add(new LogAnalysis());
    items.add(new LogAnalysis());
    when(mockLogAnalyzerRepository.findAll(any(Predicate.class),any(OrderSpecifier.class))).thenReturn(items);

    Collector mockCollector = mock(Collector.class);
    when(mockCollectorRepository.findOne(any(ObjectId.class))).thenReturn(mockCollector);
    when(mockCollector.getLastExecuted()).thenReturn(23L);

    DataResponse<Iterable<LogAnalysis>> response =subject.search(request);

    ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);
    ArgumentCaptor<OrderSpecifier> orderArgumentCaptor = ArgumentCaptor.forClass(OrderSpecifier.class);
    verify(mockLogAnalyzerRepository).findAll(predicateArgumentCaptor.capture(),orderArgumentCaptor.capture());

    assertThat(((BooleanOperation)predicateArgumentCaptor.getValue()).getArgs(),hasSize(2));
    assertThat(((BooleanOperation)predicateArgumentCaptor.getValue()).getArgs().get(0).toString(),equalTo("logAnalysis.collectorItemId"));
    assertThat(((BooleanOperation)predicateArgumentCaptor.getValue()).getArgs().get(1).toString(),equalTo(itemId.toString()));

    assertThat(orderArgumentCaptor.getValue().getOrder(),is(Order.DESC));

    assertThat(response,notNullValue());
    assertThat(response.getResult(),notNullValue());
    assertThat(response.getLastUpdated(),is(equalTo(23L)));

  }

  @Test
  public void supportsMaxResults() {
    LogAnalysisSearchRequest request = new LogAnalysisSearchRequest();
    request.setMax(100);
    request.setComponentId(ObjectId.get());
    Component component = mock(Component.class);
    when(mockComponentRepository.findOne(any(ObjectId.class))).thenReturn(component);
    CollectorItem item = mock(CollectorItem.class);
    when(component.getFirstCollectorItemForType(eq(CollectorType.Log))).thenReturn(item);
    ObjectId itemId = ObjectId.get();
    when(item.getId()).thenReturn(itemId);

    List<LogAnalysis> internalList = new ArrayList<>();
    internalList.add(new LogAnalysis());
    internalList.add(new LogAnalysis());
    Page<LogAnalysis> items = new PageImpl<>(internalList);
    when(mockLogAnalyzerRepository.findAll(any(Predicate.class),any(PageRequest.class))).thenReturn(items);

    Collector mockCollector = mock(Collector.class);
    when(mockCollectorRepository.findOne(any(ObjectId.class))).thenReturn(mockCollector);
    when(mockCollector.getLastExecuted()).thenReturn(23L);

    DataResponse<Iterable<LogAnalysis>> response =subject.search(request);

    ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);
    ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
    verify(mockLogAnalyzerRepository).findAll(predicateArgumentCaptor.capture(),pageRequestCaptor.capture());

    assertThat(((BooleanOperation)predicateArgumentCaptor.getValue()).getArgs(),hasSize(2));
    assertThat(((BooleanOperation)predicateArgumentCaptor.getValue()).getArgs().get(0).toString(),equalTo("logAnalysis.collectorItemId"));
    assertThat(((BooleanOperation)predicateArgumentCaptor.getValue()).getArgs().get(1).toString(),equalTo(itemId.toString()));

    assertThat(pageRequestCaptor.getValue().getSort().getOrderFor("timestamp").getDirection(),is(Sort.Direction.DESC));
    assertThat(pageRequestCaptor.getValue().getPageSize(),is(100));
    assertThat(pageRequestCaptor.getValue().getPageNumber(),is(0));

    assertThat(response,notNullValue());
    assertThat(response.getResult(),notNullValue());
    assertThat(response.getLastUpdated(),is(equalTo(23L)));
  }

}