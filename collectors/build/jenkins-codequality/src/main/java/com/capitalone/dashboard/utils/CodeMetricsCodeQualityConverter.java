package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.quality.QualityVisitor;
import org.springframework.stereotype.Component;

@Component
public class CodeMetricsCodeQualityConverter implements CodeQualityConverter {

    @Override
    public QualityVisitor<CodeQuality> produceVisitor() {
        return new CodeQualityMetricsConverter();
    }
}
