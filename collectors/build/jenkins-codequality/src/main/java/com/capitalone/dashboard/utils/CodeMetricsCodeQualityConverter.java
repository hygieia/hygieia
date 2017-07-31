package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.quality.CodeQualityVisitor;
import org.springframework.stereotype.Component;

@Component
public class CodeMetricsCodeQualityConverter implements CodeQualityConverter {

    @Override
    public CodeQualityVisitor produceVisitor() {
        return new CodeQualityMetricsConverter();
    }
}
