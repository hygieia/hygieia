package com.capitalone.dashboard.cloudwatch.collector;

import com.capitalone.dashboard.model.CollectorType;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AwsCloudwatchLogAnalyzerTest {

    @Test
    public void createsPrototype(){
        AwsCloudwatchLogAnalyzer producedCollector = AwsCloudwatchLogAnalyzer.prototype();

        assertThat(producedCollector.getName()).isEqualTo("AwsCloudwatchLogAnalyzer");
        assertThat(producedCollector.getCollectorType()).isEqualTo(CollectorType.Log);
        assertThat(producedCollector.isEnabled()).isTrue();
        assertThat(producedCollector.isOnline()).isTrue();
    }
}
