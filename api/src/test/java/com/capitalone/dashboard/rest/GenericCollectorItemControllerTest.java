package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import com.capitalone.dashboard.service.GenericCollectorItemService;
import com.capitalone.dashboard.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, WebMVCConfig.class })
@WebAppConfiguration
public class GenericCollectorItemControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private GenericCollectorItemService genericCollectorItemService;



    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void insertGenericItemTestHappyPath() throws Exception {
        GenericCollectorItemCreateRequest request = makeCodeQualityRequestGood();
        @SuppressWarnings("unused")
        byte[] content = TestUtil.convertObjectToJsonBytes(request);
        when(genericCollectorItemService.create(Matchers.any(GenericCollectorItemCreateRequest.class))).thenReturn("123456");
        mockMvc.perform(post("/generic-item")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated());

    }

    @Test
    public void insertGenericItemTestMissingToolName() throws Exception {
        GenericCollectorItemCreateRequest request = makeCodeQualityRequestGood();
        request.setToolName(null);
        @SuppressWarnings("unused")
        byte[] content = TestUtil.convertObjectToJsonBytes(request);
        when(genericCollectorItemService.create(Matchers.any(GenericCollectorItemCreateRequest.class))).thenReturn("123456");
        mockMvc.perform(post("/generic-item")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void insertGenericItemTestMissingRawData() throws Exception {
        GenericCollectorItemCreateRequest request = makeCodeQualityRequestGood();
        request.setRawData(null);
        @SuppressWarnings("unused")
        byte[] content = TestUtil.convertObjectToJsonBytes(request);
        when(genericCollectorItemService.create(Matchers.any(GenericCollectorItemCreateRequest.class))).thenReturn("123456");
        mockMvc.perform(post("/generic-item")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().is4xxClientError());

    }


    @Test
    public void insertGenericItemTestMissingID1() throws Exception {
        GenericCollectorItemCreateRequest request = makeCodeQualityRequestGood();
        request.setBuildId(null);
        @SuppressWarnings("unused")
        byte[] content = TestUtil.convertObjectToJsonBytes(request);
        when(genericCollectorItemService.create(Matchers.any(GenericCollectorItemCreateRequest.class))).thenReturn("123456");
        mockMvc.perform(post("/generic-item")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    public void insertGenericItemTestMissingID2() throws Exception {
        GenericCollectorItemCreateRequest request = makeCodeQualityRequestGood();
        request.setRelatedCollectorItemId(null);
        @SuppressWarnings("unused")
        byte[] content = TestUtil.convertObjectToJsonBytes(request);
        when(genericCollectorItemService.create(Matchers.any(GenericCollectorItemCreateRequest.class))).thenReturn("123456");
        mockMvc.perform(post("/generic-item")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void insertGenericItemTestMissingSource() throws Exception {
        GenericCollectorItemCreateRequest request = makeCodeQualityRequestGood();
        request.setSource(null);
        @SuppressWarnings("unused")
        byte[] content = TestUtil.convertObjectToJsonBytes(request);
        when(genericCollectorItemService.create(Matchers.any(GenericCollectorItemCreateRequest.class))).thenReturn("123456");
        mockMvc.perform(post("/generic-item")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().is4xxClientError());

    }


    private GenericCollectorItemCreateRequest makeCodeQualityRequestGood() {
        GenericCollectorItemCreateRequest genericCollectorItemCreateRequest = new GenericCollectorItemCreateRequest();
        genericCollectorItemCreateRequest.setSource("some source");
        genericCollectorItemCreateRequest.setRawData("some raw data");
        genericCollectorItemCreateRequest.setToolName("MyTool");
        genericCollectorItemCreateRequest.setBuildId("5bae541c099739600663ef9e");
        genericCollectorItemCreateRequest.setRelatedCollectorItemId("5bae541b099739600663ef9a");
        return genericCollectorItemCreateRequest;
    }



    @Test
    public void createGenericItem() {
    }
}