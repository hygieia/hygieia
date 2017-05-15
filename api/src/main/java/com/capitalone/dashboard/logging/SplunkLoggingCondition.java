package com.capitalone.dashboard.logging;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SplunkLoggingCondition implements Condition {

    private static final String LOG_SPLUNK_REQUEST = "logSplunkRequest";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String logSplunkRequest = context.getEnvironment().getProperty(LOG_SPLUNK_REQUEST);
        return logSplunkRequest != null && Boolean.parseBoolean(logSplunkRequest);
    }

}
