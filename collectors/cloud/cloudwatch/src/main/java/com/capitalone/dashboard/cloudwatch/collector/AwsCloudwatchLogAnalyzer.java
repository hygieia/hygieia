package com.capitalone.dashboard.cloudwatch.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import org.springframework.stereotype.Component;

@Component
public class AwsCloudwatchLogAnalyzer extends Collector {

    public static final String COLLECTOR_NAME = "AwsCloudwatchLogAnalyzer";

    public static AwsCloudwatchLogAnalyzer prototype() {
        AwsCloudwatchLogAnalyzer analyzer = new AwsCloudwatchLogAnalyzer();
        analyzer.setName(COLLECTOR_NAME);
        analyzer.setCollectorType(CollectorType.Log);
        analyzer.setEnabled(true);
        analyzer.setOnline(true);
        return analyzer;
    }
}
