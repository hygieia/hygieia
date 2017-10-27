package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.GamificationMetric;
import com.capitalone.dashboard.model.GamificationScoringRange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GamificationRepositoryTest extends FongoBaseRepositoryTest {
    private static GamificationMetric gamificationMetric;

    @Autowired
    private GamificationMetricRepository gamificationMetricRepository;

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
        gamificationMetricRepository.save(gamificationMetric);
    }

    @Test
    public void testGetGamificationMetricByMetricName() {
        GamificationMetric retrievedMetric = gamificationMetricRepository.findByMetricName("TestMetric");
        assertNotNull(retrievedMetric.getId());
    }

    @Test
    public void testGetGamificationMetricByInvalidMetricName() {
        GamificationMetric retrievedMetric = gamificationMetricRepository.findByMetricName("InvalidMetricName");
        assertNull(retrievedMetric);
    }

    @Test
    public void testGetGamificationMetricByEnabled() {
        Collection<GamificationMetric> retrievedMetric = gamificationMetricRepository.findAllByEnabled(true);
        assertEquals(1, retrievedMetric.size());
    }

    @Test
    public void testGetGamificationMetricByEnabledFalse() {
        Collection<GamificationMetric> retrievedMetric = gamificationMetricRepository.findAllByEnabled(false);
        assertEquals(0, retrievedMetric.size());
    }

    @After
    public void tearDown() {
        gamificationMetric = null;
        gamificationMetricRepository.deleteAll();
    }
}
