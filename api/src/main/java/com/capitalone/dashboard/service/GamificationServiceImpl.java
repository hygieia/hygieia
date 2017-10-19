package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.GamificationMetric;
import com.capitalone.dashboard.repository.GamificationMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GamificationServiceImpl implements GamificationService {

    private GamificationMetricRepository gamificationMetricRepository;

    @Autowired
    public GamificationServiceImpl(GamificationMetricRepository gamificationMetricRepository) { this.gamificationMetricRepository = gamificationMetricRepository; }

    @Override
    public Collection<GamificationMetric> getGamificationMetrics() {
        return gamificationMetricRepository.findAll();
    }

    @Override
    public void saveGamificationMetrics(Collection<GamificationMetric> gamificationMetrics) {
        for (GamificationMetric gamificationMetric : gamificationMetrics) {
            gamificationMetricRepository.save(gamificationMetric);
        }
    }
}
