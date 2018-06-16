package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevegal on 16/06/2018.
 */
public class LogAnalysis extends BaseModel {

    private List<LogAnalysisMetric> metrics = new ArrayList<>();

    public List<LogAnalysisMetric> getMetrics() {
        return metrics;
    }
}
