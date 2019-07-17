package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.ExecutiveFeatureMetrics;
import com.capitalone.dashboard.model.FeatureMetrics;
import com.capitalone.dashboard.model.LobFeatureMetrics;
import com.capitalone.dashboard.model.ProductFeatureMetrics;


public interface FeatureMetricsService {
    FeatureMetrics getFeatureMetrics(String name);

    FeatureMetrics getFeatureMetricsByType(String name,String type);

    ProductFeatureMetrics getProductFeatureMetrics(String name);

    ProductFeatureMetrics getProductFeatureMetricsByType(String name, String type);

    LobFeatureMetrics getLobFeatureMetrics(String lob);

    LobFeatureMetrics getLobFeatureMetricsByType(String lob, String type);

    ExecutiveFeatureMetrics getExecutiveFeatureMetrics(String name);

    ExecutiveFeatureMetrics getExecutiveFeatureMetricsByType(String name, String MetricType);


}
