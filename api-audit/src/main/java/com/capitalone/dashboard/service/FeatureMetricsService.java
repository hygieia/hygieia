package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.FeatureMetrics;

public interface FeatureMetricsService {
    FeatureMetrics getFeatureMetrics(String name);
    FeatureMetrics getFeatureMetricsByType(String name,String type);

}
