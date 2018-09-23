package com.capitalone.dashboard.cloudwatch.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import org.springframework.stereotype.Component;

/**
 * Created by stevegal on 16/06/2018.
 */
@Component
public class AwsCloudwatchLogAnalyzer extends Collector {
    public static AwsCloudwatchLogAnalyzer prototype() {
        AwsCloudwatchLogAnalyzer analyzer = new AwsCloudwatchLogAnalyzer();
        analyzer.setName("CloudwatchLogAnalyzer");
        analyzer.setCollectorType(CollectorType.Log);
        analyzer.setEnabled(true);
        analyzer.setOnline(true);
        return analyzer;
    }
}
