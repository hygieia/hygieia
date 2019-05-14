package com.capitalone.dashboard.rest;


import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.request.LogAnalysisSearchRequest;
import com.capitalone.dashboard.service.LogAnalysisService;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class LogAnalysisControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;
  @Autowired
  private LogAnalysisService mockLogAnalysisService;


  @Before
  public void before() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    reset(mockLogAnalysisService);
  }

  @Test
  public void getsAllLogStreams() throws Exception {

    ObjectId objId = ObjectId.get();

    Iterable<LogAnalysis> logAnalysis = Arrays.asList(new LogAnalysis());
    DataResponse<Iterable<LogAnalysis>> searchResult = new DataResponse<>(logAnalysis,1);
    when(mockLogAnalysisService.search(any(LogAnalysisSearchRequest.class))).thenReturn(searchResult);

    this.mockMvc.perform(get("/loganalysis?componentId="+objId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", hasSize(1)));


    ArgumentCaptor<LogAnalysisSearchRequest> captor = ArgumentCaptor.forClass(LogAnalysisSearchRequest.class);
    verify(mockLogAnalysisService).search(captor.capture());

    assertThat(captor.getValue().getComponentId(),equalTo(objId));
  }

  @Test
  public void getsAllMaxLogStreams() throws Exception {

    ObjectId objId = ObjectId.get();

    Iterable<LogAnalysis> logAnalysis = Arrays.asList(new LogAnalysis());
    DataResponse<Iterable<LogAnalysis>> searchResult = new DataResponse<>(logAnalysis,1);
    when(mockLogAnalysisService.search(any(LogAnalysisSearchRequest.class))).thenReturn(searchResult);

    this.mockMvc.perform(get("/loganalysis?max=30&componentId="+objId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", hasSize(1)));


    ArgumentCaptor<LogAnalysisSearchRequest> captor = ArgumentCaptor.forClass(LogAnalysisSearchRequest.class);
    verify(mockLogAnalysisService).search(captor.capture());

    assertThat(captor.getValue().getComponentId(),equalTo(objId));
    assertThat(captor.getValue().getMax(),equalTo(30));
  }
}