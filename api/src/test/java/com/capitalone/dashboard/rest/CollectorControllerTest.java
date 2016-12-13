package com.capitalone.dashboard.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.capitalone.dashboard.config.SpringDataTestConfig;
import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.service.CollectorService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class, SpringDataTestConfig.class})
@WebAppConfiguration
public class CollectorControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired private CollectorService collectorService;
    
    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void collectorItemsByTypeWithFilter() throws Exception {
        String filterString = "";
        
        Collector collector = makeCollector("Hudson", CollectorType.Build);
        CollectorItem item1 = makeCollectorItem(collector, "Build 1", false);
        CollectorItem item2 = makeCollectorItem(collector, "Build 2", true);
        Page<CollectorItem> pages = new PageImpl<CollectorItem>(Arrays.asList(item1, item2), null, 2);
        when(collectorService.collectorItemsByTypeWithFilter(eq(CollectorType.Build), eq(filterString), any(Pageable.class))).thenReturn(pages);
        mockMvc.perform(get("/collector/item/type/build?size=10&page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(item1.getId().toString())))
                .andExpect(jsonPath("$[0].collectorId", is(item1.getCollectorId().toString())))
                .andExpect(jsonPath("$[0].collector.id", is(collector.getId().toString())))
                .andExpect(jsonPath("$[0].collector.name", is(collector.getName())))
                .andExpect(jsonPath("$.[0].description", is(item1.getDescription())))
                .andExpect(jsonPath("$.[0].enabled", is(item1.isEnabled())))
                .andExpect(jsonPath("$.[1].id", is(item2.getId().toString())))
                .andExpect(jsonPath("$.[1].collectorId", is(item2.getCollectorId().toString())))
                .andExpect(jsonPath("$.[1].collector.id", is(collector.getId().toString())))
                .andExpect(jsonPath("$.[1].collector.name", is(collector.getName())))
                .andExpect(jsonPath("$.[1].description", is(item2.getDescription())))
                .andExpect(jsonPath("$.[1].enabled", is(item2.isEnabled())));
                
    }

    @Test
    public void getCollectorItemById() throws Exception {
        Collector collector = makeCollector("Hudson", CollectorType.Build);
        CollectorItem item1 = makeCollectorItem(collector, "Build 1", false);
        when(collectorService.getCollectorItem(item1.getId())).thenReturn(item1);
        mockMvc.perform(get("/collector/item/" + item1.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId().toString())))
                .andExpect(jsonPath("$.collectorId", is(item1.getCollectorId().toString())))
                .andExpect(jsonPath("$.collector.id", is(collector.getId().toString())))
                .andExpect(jsonPath("$.collector.name", is(collector.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.enabled", is(item1.isEnabled())));
    }

    private Collector makeCollector(String name, CollectorType type) {
        Collector collector = new Collector();
        collector.setId(ObjectId.get());
        collector.setName(name);
        collector.setCollectorType(type);
        collector.setEnabled(true);
        collector.setOnline(true);
        collector.setLastExecuted(System.currentTimeMillis());
        return collector;
    }

    private CollectorItem makeCollectorItem(Collector collector, String desc, boolean enabled) {
        CollectorItem item = new CollectorItem();
        item.setId(ObjectId.get());
        item.setCollectorId(collector.getId());
        item.setCollector(collector);
        item.setDescription(desc);
        item.setEnabled(enabled);
        return item;
    }
}
