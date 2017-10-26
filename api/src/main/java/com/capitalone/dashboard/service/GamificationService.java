package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.GamificationMetric;

import java.util.Collection;

public interface GamificationService {
    Collection<GamificationMetric> getGamificationMetrics(Boolean enabled);
    GamificationMetric saveGamificationMetric(GamificationMetric gamificationMetric);
}
