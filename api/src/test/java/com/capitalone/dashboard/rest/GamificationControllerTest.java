package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.GamificationMetric;
import com.capitalone.dashboard.model.GamificationScoringRange;
import com.capitalone.dashboard.service.GamificationService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class GamificationControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private GamificationService gamificationService;

    private Collection<GamificationMetric> allMetrics;
    private Collection<GamificationMetric> enabledMetrics;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        GamificationMetric gamificationMetric1 = new GamificationMetric();
        gamificationMetric1.setId(new ObjectId());
        gamificationMetric1.setMetricName("Metric1");
        GamificationMetric gamificationMetric2 = new GamificationMetric();
        gamificationMetric1.setId(new ObjectId());
        gamificationMetric2.setMetricName("Metric2");
        GamificationMetric gamificationMetric3 = new GamificationMetric();
        gamificationMetric1.setId(new ObjectId());
        gamificationMetric3.setMetricName("Metric3");
        GamificationMetric gamificationMetric4 = new GamificationMetric();
        gamificationMetric1.setId(new ObjectId());
        gamificationMetric4.setMetricName("Metric4");

        allMetrics = Lists.newArrayList(gamificationMetric1, gamificationMetric2, gamificationMetric3, gamificationMetric4);
        enabledMetrics = Lists.newArrayList(gamificationMetric1, gamificationMetric2);

        when(gamificationService.getGamificationMetrics(null)).thenReturn(allMetrics);
        when(gamificationService.getGamificationMetrics(true)).thenReturn(enabledMetrics);
    }

    @Test
    public void testGetAllMetrics() throws Exception {
        mockMvc.perform(get("/gamification/metrics").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].metricName", is("Metric1")))
                .andExpect(jsonPath("$[1].metricName", is("Metric2")))
                .andExpect(jsonPath("$[2].metricName", is("Metric3")))
                .andExpect(jsonPath("$[3].metricName", is("Metric4")));
    }

    @Test
    public void testGetEnabledMetrics() throws Exception {
        mockMvc.perform(get("/gamification/metrics?enabled=true").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].metricName", is("Metric1")))
                .andExpect(jsonPath("$[1].metricName", is("Metric2")));
    }

    @Test
    public void testSaveGamificationMetrics() throws Exception {
        GamificationMetric gamificationMetric = new GamificationMetric();
        gamificationMetric.setId(new ObjectId());
        gamificationMetric.setMetricName("Metric1");
        gamificationMetric.setFormattedName("Metric1");
        gamificationMetric.setDescription("Metric1");
        gamificationMetric.setEnabled(true);
        GamificationScoringRange gamificationScoringRange = new GamificationScoringRange();
        gamificationScoringRange.setMax(100);
        gamificationScoringRange.setMin(0);
        gamificationScoringRange.setScore(20);
        gamificationMetric.setGamificationScoringRanges(Arrays.asList(gamificationScoringRange));

        when(gamificationService.saveGamificationMetric(gamificationMetric)).thenReturn(gamificationMetric);

        mockMvc.perform(post("/gamification/metrics").contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(gamificationMetric)))
                .andExpect(status().isCreated());
    }

}
