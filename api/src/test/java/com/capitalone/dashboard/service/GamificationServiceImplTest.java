package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.GamificationMetric;
import com.capitalone.dashboard.model.GamificationScoringRange;
import com.capitalone.dashboard.repository.GamificationMetricRepository;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GamificationServiceImplTest {

    private static GamificationMetric gamificationMetric;

    @Mock
    private GamificationMetricRepository gamificationMetricRepository;

    @InjectMocks
    private GamificationServiceImpl service;

    @Before
    public void setUp() {
        gamificationMetric = new GamificationMetric();
        gamificationMetric.setDescription("Test Metric");
        gamificationMetric.setEnabled(true);
        gamificationMetric.setMetricName("TestMetric");
        gamificationMetric.setFormattedName("Display name for the metric");
        GamificationScoringRange gamificationScoringRange = new GamificationScoringRange();
        gamificationScoringRange.setMin(0);
        gamificationScoringRange.setMax(100);
        gamificationScoringRange.setScore(20);
        gamificationMetric.setGamificationScoringRanges(Arrays.asList(gamificationScoringRange));
        when(gamificationMetricRepository.findByMetricName("TestMetric")).thenReturn(gamificationMetric);
        when(gamificationMetricRepository.findAll()).thenReturn(Arrays.asList(gamificationMetric));
        when(gamificationMetricRepository.findAllByEnabled(true)).thenReturn(Arrays.asList(gamificationMetric));
        when(gamificationMetricRepository.findAllByEnabled(false)).thenReturn(CollectionUtils.EMPTY_COLLECTION);
        when(gamificationMetricRepository.save(gamificationMetric)).thenReturn(gamificationMetric);
    }

    @Test
    public void testSaveGamificationMetricInsertsIfNoMetricDataExists() {
        GamificationMetric savedMetric = service.saveGamificationMetric(gamificationMetric);
        Assert.assertEquals("TestMetric", savedMetric.getMetricName());
        verify(gamificationMetricRepository).save(isA(GamificationMetric.class));
    }

    @Test
    public void testGetAllGamificationMetrics() {
        Collection<GamificationMetric> retrievedMetrics = service.getGamificationMetrics(null);
        Assert.assertEquals(1, retrievedMetrics.size());
    }

    @Test
    public void testGetAllEnabledGamificationMetrics() {
        Collection<GamificationMetric> retrievedMetrics = service.getGamificationMetrics(true);
        Assert.assertEquals(1, retrievedMetrics.size());
    }

    @Test
    public void testGetAllDisabledGamificationMetrics() {
        Collection<GamificationMetric> retrievedMetrics = service.getGamificationMetrics(false);
        Assert.assertEquals(0, retrievedMetrics.size());
    }
}
