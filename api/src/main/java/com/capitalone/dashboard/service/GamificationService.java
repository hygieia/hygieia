package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.GamificationMetric;

import java.util.Collection;

public interface GamificationService {

    Collection<GamificationMetric> getGamificationMetrics();
    void saveGamificationMetrics(Collection<GamificationMetric> gamificationMetrics);

}
