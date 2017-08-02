package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.quality.CodeQualityVisitor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeMetricsCodeQualitConverterTest {

    @Test
    public void producesACodeQualityVistor() {
        CodeMetricsCodeQualityConverter codeQualityConverter = new CodeMetricsCodeQualityConverter();

        final CodeQualityVisitor producedVistor = codeQualityConverter.produceVisitor();

        assertThat(producedVistor).isNotNull();
        assertThat(producedVistor).isInstanceOf(CodeQualityMetricsConverter.class);
    }

}