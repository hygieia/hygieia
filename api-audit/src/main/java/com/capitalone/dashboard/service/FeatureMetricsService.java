package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.ExecutiveFeatureMetrics;
import com.capitalone.dashboard.model.ComponentFeatureMetrics;
import com.capitalone.dashboard.model.LobFeatureMetrics;
import com.capitalone.dashboard.model.ProductFeatureMetrics;


public interface FeatureMetricsService {

    ComponentFeatureMetrics getComponentFeatureMetrics(String name);

    ComponentFeatureMetrics getComponentFeatureMetricByType(String name, String type);

    ProductFeatureMetrics getProductFeatureMetrics(String name);

    ProductFeatureMetrics getProductFeatureMetricsByType(String name, String type);

    LobFeatureMetrics getLobFeatureMetrics(String lob);

    LobFeatureMetrics getLobFeatureMetricsByType(String lob, String type);

    ExecutiveFeatureMetrics getExecutiveFeatureMetrics(String name);

    ExecutiveFeatureMetrics getExecutiveFeatureMetricsByType(String name, String metricType);


}
