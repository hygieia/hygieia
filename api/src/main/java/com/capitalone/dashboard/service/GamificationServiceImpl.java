package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.GamificationMetric;
import com.capitalone.dashboard.repository.GamificationMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GamificationServiceImpl implements GamificationService {

    private GamificationMetricRepository gamificationMetricRepository;

    @Autowired
    public GamificationServiceImpl(GamificationMetricRepository gamificationMetricRepository) { this.gamificationMetricRepository = gamificationMetricRepository; }

    @Override
    public Collection<GamificationMetric> getGamificationMetrics(Boolean enabled) {
        return (enabled == null) ? gamificationMetricRepository.findAll() : gamificationMetricRepository.findAllByEnabled(enabled);
    }

    @Override
    public GamificationMetric saveGamificationMetric(GamificationMetric gamificationMetric) {
        GamificationMetric existing = gamificationMetricRepository.findByMetricName(gamificationMetric.getMetricName());
        if(existing != null) {
            gamificationMetric.setId(existing.getId());
        }
        return gamificationMetricRepository.save(gamificationMetric);
    }
}
